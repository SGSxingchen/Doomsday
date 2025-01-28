package org.lanstard.doomsday.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import org.lanstard.doomsday.network.NetworkManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class OpPermissionPacket {
    private static final Map<UUID, Boolean> lastPermissionState = new HashMap<>();
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
            boolean currentOpState = false;
            if (player != null) {
                currentOpState = player.hasPermissions(2);
            }
            if (player != null) {
                NetworkManager.getChannel().sendTo(
                        new OpPermissionPacket(currentOpState),
                        player.connection.connection,
                        NetworkDirection.PLAY_TO_CLIENT
                );
            }
        });
        ctx.get().setPacketHandled(true);
    }
} 