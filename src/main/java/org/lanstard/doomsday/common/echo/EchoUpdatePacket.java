package org.lanstard.doomsday.common.echo;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import org.lanstard.doomsday.client.manage.ClientEchoManager;

import java.util.function.Supplier;

public class EchoUpdatePacket {
    private final PlayerEchoData echoData;

    public EchoUpdatePacket(PlayerEchoData echoData) {
        this.echoData = echoData;
    }

    public static void encode(EchoUpdatePacket msg, FriendlyByteBuf buf) {
        if (msg.echoData != null) {
            buf.writeBoolean(true);
            buf.writeNbt(msg.echoData.toNBT());
        } else {
            buf.writeBoolean(false);
        }
    }

    public static EchoUpdatePacket decode(FriendlyByteBuf buf) {
        boolean hasData = buf.readBoolean();
        return hasData ? new EchoUpdatePacket(PlayerEchoData.fromNBT(buf.readNbt())) 
                      : new EchoUpdatePacket(null);
    }
    
    public static void handle(EchoUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            ClientEchoManager.handleEchoUpdate(msg.getEchoData());
            ctx.get().setPacketHandled(true);
        }
    }
    
    public PlayerEchoData getEchoData() { return echoData; }
} 