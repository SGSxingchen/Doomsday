package org.lanstard.doomsday.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import org.lanstard.doomsday.network.NetworkManager;
import org.lanstard.doomsday.network.packet.OpenEchoScreenPacket;

public class EchoScreenCommand {
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