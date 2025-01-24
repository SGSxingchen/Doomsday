package org.lanstard.doomsday.common.sanity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SanityUpdatePacket {
    private final int sanity;
    private final int faith;
    
    public SanityUpdatePacket(int sanity) {
        this(sanity, 0); // 默认信念为0
    }
    
    public SanityUpdatePacket(int sanity, int faith) {
        this.sanity = sanity;
        this.faith = faith;
    }
    
    public static void encode(SanityUpdatePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.sanity);
        buf.writeInt(msg.faith);
    }
    
    public static SanityUpdatePacket decode(FriendlyByteBuf buf) {
        return new SanityUpdatePacket(buf.readInt(), buf.readInt());
    }
    
    public static void handle(SanityUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            ClientSanityManager.handleSanityUpdate(msg.getSanity(), msg.getFaith());
            ctx.get().setPacketHandled(true);
        }
    }
    
    public int getSanity() { return sanity; }
    public int getFaith() { return faith; }
} 