package org.lanstard.doomsday.common.sanity;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.sanity.config.SanityConfig;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class SanityCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {

        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("sanity")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("set")
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("value", IntegerArgumentType.integer(SanityConfig.getConfig().sanity_limits.min, SanityConfig.getConfig().sanity_limits.max))
                        .executes(context -> setSanity(
                            context.getSource(),
                            EntityArgument.getPlayer(context, "player"),
                            IntegerArgumentType.getInteger(context, "value")
                        )))))
            .then(Commands.literal("modify")
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("delta", IntegerArgumentType.integer(-100, 100))
                        .executes(context -> modifySanity(
                            context.getSource(),
                            EntityArgument.getPlayer(context, "player"),
                            IntegerArgumentType.getInteger(context, "delta")
                        )))))
            .then(Commands.literal("get")
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(context -> getSanity(
                        context.getSource(),
                        EntityArgument.getPlayer(context, "player")
                    )))));

        dispatcher.register(Commands.literal("maxsanity")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("set")
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("value", IntegerArgumentType.integer(100, 1000))
                        .executes(context -> setMaxSanity(
                            context.getSource(),
                            EntityArgument.getPlayer(context, "player"),
                            IntegerArgumentType.getInteger(context, "value")
                        )))))
            .then(Commands.literal("modify")
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("delta", IntegerArgumentType.integer(-100, 100))
                        .executes(context -> modifyMaxSanity(
                            context.getSource(),
                            EntityArgument.getPlayer(context, "player"),
                            IntegerArgumentType.getInteger(context, "delta")
                        )))))
            .then(Commands.literal("get")
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(context -> getMaxSanity(
                        context.getSource(),
                        EntityArgument.getPlayer(context, "player")
                    )))));
    }

    private static int setSanity(CommandSourceStack source, ServerPlayer player, int value) {
        SanityManager.setSanity(player, value);
        source.sendSuccess(() -> Component.literal(
            String.format("已将玩家 %s 的理智值设置为: %d", player.getName().getString(), value)
        ), true);
        return 1;
    }

    private static int modifySanity(CommandSourceStack source, ServerPlayer player, int delta) {
        SanityManager.modifySanity(player, delta);
        int newValue = SanityManager.getSanity(player);
        source.sendSuccess(() -> Component.literal(
            String.format("已将玩家 %s 的理智值调整 %d 点，当前为: %d", 
                player.getName().getString(), delta, newValue)
        ), true);
        return 1;
    }

    private static int getSanity(CommandSourceStack source, ServerPlayer player) {
        int value = SanityManager.getSanity(player);
        source.sendSuccess(() -> Component.literal(
            String.format("玩家 %s 的理智值为: %d", player.getName().getString(), value)
        ), false);
        return 1;
    }

    private static int setMaxSanity(CommandSourceStack source, ServerPlayer player, int value) {
        SanityManager.modifyMaxSanity(player, value - SanityManager.getMaxSanity(player));
        source.sendSuccess(() -> Component.literal(
            String.format("已将玩家 %s 的理智值上限设置为: %d", player.getName().getString(), value)
        ), true);
        return 1;
    }

    private static int modifyMaxSanity(CommandSourceStack source, ServerPlayer player, int delta) {
        SanityManager.modifyMaxSanity(player, delta);
        int newValue = SanityManager.getMaxSanity(player);
        source.sendSuccess(() -> Component.literal(
            String.format("已将玩家 %s 的理智值上限调整 %d 点，当前为: %d", 
                player.getName().getString(), delta, newValue)
        ), true);
        return 1;
    }

    private static int getMaxSanity(CommandSourceStack source, ServerPlayer player) {
        int value = SanityManager.getMaxSanity(player);
        source.sendSuccess(() -> Component.literal(
            String.format("玩家 %s 的理智值上限为: %d", player.getName().getString(), value)
        ), false);
        return 1;
    }
} 