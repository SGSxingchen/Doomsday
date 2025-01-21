package org.lanstard.doomsday.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import org.lanstard.doomsday.network.NetworkManager;

import java.util.function.Supplier;

public class OpPermissionPacket {
    private boolean isOp;

    public OpPermissionPacket() {}
    
    public OpPermissionPacket(boolean isOp) {
        this.isOp = isOp;
    }

    public static void encode(OpPermissionPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.isOp);
    }

    public static OpPermissionPacket decode(FriendlyByteBuf buf) {
        return new OpPermissionPacket(buf.readBoolean());
    }

    public boolean isOp() {
        return isOp;
    }

    public static void handleOpPermissionCheck(OpPermissionPacket msg,
                                               Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                boolean isOp = player.hasPermissions(2);
                NetworkManager.getChannel().sendTo(new OpPermissionPacket(isOp),
                        player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            } else {

            }
        });
        ctx.get().setPacketHandled(true);
    }
} 