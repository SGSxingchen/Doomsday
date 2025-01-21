package org.lanstard.doomsday.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.command.EnumArgument;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.network.NetworkManager;
import org.lanstard.doomsday.network.packet.SpawnScreenTextPacket;
import org.lanstard.doomsday.client.gui.text.ScreenTextPreset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class ScreenTextCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenTextCommand.class);
    
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("screentext")
            .requires(source -> source.hasPermission(2)) // 需要权限等级2
            
            // 预设效果命令
            .then(Commands.literal("preset")
                .then(Commands.argument("targets", EntityArgument.players())
                .then(Commands.argument("preset", EnumArgument.enumArgument(ScreenTextPreset.class))
                .then(Commands.argument("text", StringArgumentType.greedyString())
                .executes(context -> spawnScreenText(
                    context.getSource(),
                    EntityArgument.getPlayers(context, "targets"),
                    StringArgumentType.getString(context, "text"),
                    context.getArgument("preset", ScreenTextPreset.class)
                ))))))
            
            // 批量文本命令
            .then(Commands.literal("batch")
                .then(Commands.argument("targets", EntityArgument.players())
                .then(Commands.argument("preset", EnumArgument.enumArgument(ScreenTextPreset.class))
                .then(Commands.argument("texts", StringArgumentType.greedyString())
                .executes(context -> spawnBatchText(
                    context.getSource(),
                    EntityArgument.getPlayers(context, "targets"),
                    StringArgumentType.getString(context, "texts"),
                    context.getArgument("preset", ScreenTextPreset.class)
                ))))))
            
            // 列出所有可用预设
            .then(Commands.literal("list")
                .executes(context -> listPresets(context.getSource())))
            
            // 清除所有文本
            .then(Commands.literal("clear")
                .executes(context -> clearTexts(context.getSource())))
            
            // 添加自定义参数命令
            .then(Commands.literal("custom")
                .then(Commands.argument("targets", EntityArgument.players())
                .then(Commands.argument("text", StringArgumentType.string())
                .then(Commands.argument("color", IntegerArgumentType.integer(0, 0xFFFFFF))
                .then(Commands.argument("scale", FloatArgumentType.floatArg(0.1f, 100.0f))
                .then(Commands.argument("alpha", FloatArgumentType.floatArg(0.0f, 1.0f))
                .then(Commands.argument("glowing", BoolArgumentType.bool())
                .then(Commands.argument("duration", IntegerArgumentType.integer(100, 10000))
                .then(Commands.argument("fadeIn", IntegerArgumentType.integer(0, 5000))
                .then(Commands.argument("fadeOut", IntegerArgumentType.integer(0, 5000))
                .then(Commands.argument("scaleStart", FloatArgumentType.floatArg(0.1f, 500.0f))
                .then(Commands.argument("scaleEnd", FloatArgumentType.floatArg(0.1f, 500.0f))
                .then(Commands.argument("moveSpeed", FloatArgumentType.floatArg(0.0f, 5.0f))
                .then(Commands.argument("rotationSpeed", FloatArgumentType.floatArg(0.0f, 10.0f))
                .then(Commands.argument("direction", EnumArgument.enumArgument(ScreenTextPreset.Direction.class))
                .executes(context -> spawnCustomText(
                    context.getSource(),
                    EntityArgument.getPlayers(context, "targets"),
                    StringArgumentType.getString(context, "text"),
                    IntegerArgumentType.getInteger(context, "color"),
                    FloatArgumentType.getFloat(context, "scale"),
                    FloatArgumentType.getFloat(context, "alpha"),
                    BoolArgumentType.getBool(context, "glowing"),
                    IntegerArgumentType.getInteger(context, "duration"),
                    IntegerArgumentType.getInteger(context, "fadeIn"),
                    IntegerArgumentType.getInteger(context, "fadeOut"),
                    FloatArgumentType.getFloat(context, "scaleStart"),
                    FloatArgumentType.getFloat(context, "scaleEnd"),
                    FloatArgumentType.getFloat(context, "moveSpeed"),
                    FloatArgumentType.getFloat(context, "rotationSpeed"),
                    context.getArgument("direction", ScreenTextPreset.Direction.class)
                )))))))))))))))));
        
        dispatcher.register(command);
    }
    
    private static int spawnScreenText(CommandSourceStack source, Collection<ServerPlayer> targets, 
                                     String text, ScreenTextPreset preset) {
        try {
            int count = 0;
            for (ServerPlayer player : targets) {
                NetworkManager.getChannel().sendTo(
                    new SpawnScreenTextPacket(text, preset),
                    player.connection.connection,
                    NetworkDirection.PLAY_TO_CLIENT
                );
                count++;
            }
            int finalCount = count;
            source.sendSuccess(() -> Component.literal(
                String.format("已向 %d 个玩家生成屏幕文本: %s", finalCount, text)), true);
            return count;
        } catch (Exception e) {
            source.sendFailure(Component.literal("生成屏幕文本时发生错误：" + e.getMessage()));
            LOGGER.error("Error spawning screen text: ", e);
            return 0;
        }
    }
    
    private static int spawnBatchText(CommandSourceStack source, Collection<ServerPlayer> targets, 
                                    String textsString, ScreenTextPreset preset) {
        try {
            List<String> texts = Arrays.asList(textsString.split("\\|"));
            int playerCount = 0;
            
            for (ServerPlayer player : targets) {
                NetworkManager.getChannel().sendTo(
                    new SpawnScreenTextPacket(texts.toArray(new String[0]), preset),
                    player.connection.connection,
                    NetworkDirection.PLAY_TO_CLIENT
                );
                playerCount++;
            }

            int finalPlayerCount = playerCount;
            source.sendSuccess(() -> Component.literal(
                String.format("已向 %d 个玩家生成 %d 条屏幕文本", finalPlayerCount, texts.size())), true);
            return playerCount * texts.size();
        } catch (Exception e) {
            source.sendFailure(Component.literal("生成批量文本时发生错误：" + e.getMessage()));
            LOGGER.error("Error spawning batch texts: ", e);
            return 0;
        }
    }
    
    private static int listPresets(CommandSourceStack source) {
        StringBuilder message = new StringBuilder("可用的文本效果预设：\n");
        for (ScreenTextPreset preset : ScreenTextPreset.values()) {
            message.append("- ").append(preset.name()).append("\n");
        }
        source.sendSuccess(() -> Component.literal(message.toString()), false);
        return ScreenTextPreset.values().length;
    }
    
    private static int clearTexts(CommandSourceStack source) {
        try {
            Collection<ServerPlayer> targets = source.getServer().getPlayerList().getPlayers();
            int count = 0;
            for (ServerPlayer player : targets) {
                NetworkManager.getChannel().sendTo(
                    new SpawnScreenTextPacket("", ScreenTextPreset.DAMAGE, true),
                    player.connection.connection,
                    NetworkDirection.PLAY_TO_CLIENT
                );
                count++;
            }
            int finalCount = count;
            source.sendSuccess(() -> Component.literal(
                String.format("已清除 %d 个玩家的所有屏幕文本", finalCount)), true);
            return count;
        } catch (Exception e) {
            source.sendFailure(Component.literal("清除屏幕文本时发生错误：" + e.getMessage()));
            return 0;
        }
    }
    
    private static int spawnCustomText(CommandSourceStack source, Collection<ServerPlayer> targets,
                                     String text, int color, float scale, float alpha, boolean glowing,
                                     int duration, int fadeIn, int fadeOut,
                                     float scaleStart, float scaleEnd,
                                     float moveSpeed, float rotationSpeed,
                                     ScreenTextPreset.Direction direction) {
        try {
            int count = 0;
            for (ServerPlayer player : targets) {
                NetworkManager.getChannel().sendTo(
                    new SpawnScreenTextPacket(
                        text, color, scale, alpha, glowing,
                        duration, fadeIn, fadeOut,
                        scaleStart, scaleEnd,
                        moveSpeed, rotationSpeed,
                        direction
                    ),
                    player.connection.connection,
                    NetworkDirection.PLAY_TO_CLIENT
                );
                count++;
            }
            int finalCount = count;
            source.sendSuccess(() -> Component.literal(
                String.format("已向 %d 个玩家生成自定义屏幕文本: %s", finalCount, text)), true);
            return count;
        } catch (Exception e) {
            source.sendFailure(Component.literal("生成自定义屏幕文本时发生错误：" + e.getMessage()));
            LOGGER.error("Error spawning custom screen text: ", e);
            return 0;
        }
    }
}