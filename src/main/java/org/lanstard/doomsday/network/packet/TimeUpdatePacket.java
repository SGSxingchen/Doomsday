package org.lanstard.doomsday.network.packet;

import net.minecraft.network.FriendlyByteBuf;

public class TimeUpdatePacket {
    private final int days;
    private final long worldTime;
    
    public TimeUpdatePacket(int days, long worldTime) {
        this.days = days;
        this.worldTime = worldTime;
    }
    
    public static void encode(TimeUpdatePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.days);
        buf.writeLong(msg.worldTime);
    }
    
    public static TimeUpdatePacket decode(FriendlyByteBuf buf) {
        return new TimeUpdatePacket(buf.readInt(), buf.readLong());
    }
    
    public int getDays() { return days; }
    public long getWorldTime() { return worldTime; }
} 