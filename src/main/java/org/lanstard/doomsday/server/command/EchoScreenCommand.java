package org.lanstard.doomsday.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.network.NetworkManager;
import org.lanstard.doomsday.network.packet.OpenEchoScreenPacket;
//@Mod.EventBusSubscriber(modid = Doomsday.MODID)
/**
 * 暂时弃用
 * */
public class EchoScreenCommand {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        // 注册回响状态命令
        // EchoScreenCommand.register(event.getDispatcher());
    }
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("echostatus")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("player", EntityArgument.player())
                .executes(context -> {
                    ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
                    NetworkManager.getChannel().sendTo(
                        new OpenEchoScreenPacket(),
                        targetPlayer.connection.connection,
                        NetworkDirection.PLAY_TO_CLIENT
                    );
                    return 1;
                }))
            .executes(context -> {
                if (context.getSource().getEntity() instanceof ServerPlayer player) {
                    NetworkManager.getChannel().sendTo(
                        new OpenEchoScreenPacket(),
                        player.connection.connection,
                        NetworkDirection.PLAY_TO_CLIENT
                    );
                }
                return 1;
            }));
    }
} 