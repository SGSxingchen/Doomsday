package org.lanstard.doomsday.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;

import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.server.data.DisplayData;
import org.lanstard.doomsday.network.NetworkManager;
import org.lanstard.doomsday.network.packet.DisplaySettingsPacket;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class HealthDisplayCommand {
    
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        DisplayData.init(event.getServer());
    }
    
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        
        dispatcher.register(Commands.literal("health_display")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("global")
                .then(Commands.argument("show", BoolArgumentType.bool())
                    .executes(context -> setGlobalHealth(context.getSource(), BoolArgumentType.getBool(context, "show")))))
            .then(Commands.literal("personal")
                .then(Commands.argument("show", BoolArgumentType.bool())
                    .executes(context -> setPersonalHealth(context.getSource(), BoolArgumentType.getBool(context, "show"))))));
    }

    private static int setGlobalHealth(CommandSourceStack source, boolean show) {
        DisplayData.setGlobalSettings(show);
        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            if(!player.hasPermissions(2)) {
                NetworkManager.getChannel().sendTo(
                        new DisplaySettingsPacket(show, player.hasPermissions(2)),
                        player.connection.connection,
                        NetworkDirection.PLAY_TO_CLIENT
                );
            }
        }
        source.sendSuccess(() -> Component.literal("已设置全局生命值显示为: " + show), true);
        return 1;
    }

    private static int setPersonalHealth(CommandSourceStack source, boolean show) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("此命令只能由玩家执行"));
            return 0;
        }
        if(player.hasPermissions(2)) {
            DisplayData.setPlayerSettings(player, show);
            NetworkManager.getChannel().sendTo(
                    new DisplaySettingsPacket(show, true),
                    player.connection.connection,
                    NetworkDirection.PLAY_TO_CLIENT
            );
            source.sendSuccess(() -> Component.literal("已设置您的生命值显示为: " + show), false);
        }
        else{
            source.sendSuccess(() -> Component.literal("该命令只允许管理员使用"), false);
        }
        return 1;
    }
} 