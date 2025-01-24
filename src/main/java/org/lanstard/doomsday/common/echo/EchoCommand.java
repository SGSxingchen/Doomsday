package org.lanstard.doomsday.common.echo;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.EnumArgument;
import org.lanstard.doomsday.Doomsday;

import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class EchoCommand {
    
    // 创建一个建议提供者用于回响ID的自动补全
    private static final SuggestionProvider<CommandSourceStack> ECHO_ID_SUGGESTIONS = (context, builder) -> {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        return SharedSuggestionProvider.suggest(
            EchoManager.getPlayerEchoes(player)
                .stream()
                .map(Echo::getId)
                .collect(Collectors.toList()),
            builder
        );
    };
    
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("echo")
            .requires(source -> source.hasPermission(2))
            
            // 添加回响
            .then(Commands.literal("add")
                .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("preset", EnumArgument.enumArgument(EchoPreset.class))
                .executes(context -> addEcho(
                    context.getSource(),
                    EntityArgument.getPlayer(context, "player"),
                    context.getArgument("preset", EchoPreset.class)
                )))))
            
            // 列出回响
            .then(Commands.literal("list")
                .then(Commands.argument("player", EntityArgument.player())
                .executes(context -> listEchoes(
                    context.getSource(),
                    EntityArgument.getPlayer(context, "player")
                ))))
            
            // 移除回响
            .then(Commands.literal("remove")
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("echoId", StringArgumentType.word())
                        .suggests(ECHO_ID_SUGGESTIONS)
                        .executes(context -> {
                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                            String echoId = StringArgumentType.getString(context, "echoId");

                            for (Echo echo : EchoManager.getPlayerEchoes(player)) {
                                if (echo.getId().equals(echoId)) {
                                    EchoManager.removeEcho(player, echo);
                                    context.getSource().sendSuccess(() -> Component.literal("成功移除回响"), true);
                                    return 1;
                                }
                            }
                            context.getSource().sendFailure(Component.literal("未找到指定回响"));
                            return 0;
                        }))))
            
            // 解除回响禁用
            .then(Commands.literal("enable")
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(context -> {
                        ServerPlayer player = EntityArgument.getPlayer(context, "player");
                        final int[] enabledCount = {0}; // 使用数组来存储计数
                        
                        for (Echo echo : EchoManager.getPlayerEchoes(player)) {
                            if (echo.isDisabled()) {
                                echo.enable();
                                enabledCount[0]++;
                            }
                        }
                        
                        if (enabledCount[0] > 0) {
                            context.getSource().sendSuccess(() -> Component.literal(
                                String.format("已解除玩家 %s 的 %d 个回响的禁用状态", 
                                    player.getName().getString(), enabledCount[0])
                            ), true);
                            
                            player.sendSystemMessage(Component.literal("§b[十日终焉] §f你的回响已被解除禁用状态！"));
                        } else {
                            context.getSource().sendSuccess(() -> Component.literal(
                                String.format("玩家 %s 没有被禁用的回响", 
                                    player.getName().getString())
                            ), true);
                        }
                        
                        return enabledCount[0];
                    })))
            
            // 列出预设
            .then(Commands.literal("presets")
                .executes(context -> listPresets(context.getSource())));
        
        dispatcher.register(command);
    }

    private static int addEcho(CommandSourceStack source, ServerPlayer player, EchoPreset preset) {
        Echo echo = preset.createEcho();
        EchoManager.addEcho(player, echo);
        
        source.sendSuccess(() -> Component.literal(
            String.format("已为玩家 %s 添加回响: %s", 
                player.getName().getString(), 
                preset.name())
        ), true);
        return 1;
    }

    private static int listEchoes(CommandSourceStack source, ServerPlayer player) {
        PlayerEchoData echoData = EchoManager.getPlayerEchoData(player.getUUID());
        source.sendSuccess(() -> Component.literal(
            String.format("玩家 %s 的回响列表:", player.getName().getString())
        ), false);
        
        for (Echo echo : echoData.getActiveEchoes()) {
            String status = echo.isDisabled() ? "§c[已禁用]§r" : "§a[可用]§r";
            source.sendSuccess(() -> Component.literal(
                String.format("%s - 名称:%s, 类型:%s, 激活方式:%s, 理智消耗:%d", 
                    status,
                    echo.getName(),
                    echo.getType(),
                    echo.getActivationType(),
                    echo.getSanityConsumption())
            ), false);
        }
        return 1;
    }

    private static int listPresets(CommandSourceStack source) {
        StringBuilder message = new StringBuilder("可用的回响预设:\n");
        for (EchoPreset preset : EchoPreset.values()) {
            message.append(String.format("- %s\n", preset.name()));
        }
        source.sendSuccess(() -> Component.literal(message.toString()), false);
        return 1;
    }
}