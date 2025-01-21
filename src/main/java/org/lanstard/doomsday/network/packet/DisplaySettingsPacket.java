package org.lanstard.doomsday.network.packet;

import net.minecraft.network.FriendlyByteBuf;

public class DisplaySettingsPacket {
    private final boolean showHealth;
    private final boolean isOp;
    
    public DisplaySettingsPacket(boolean showHealth, boolean isOp) {
        this.showHealth = showHealth;
        this.isOp = isOp;
    }
    
    public static void encode(DisplaySettingsPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.showHealth);
        buf.writeBoolean(msg.isOp);
    }
    
    public static DisplaySettingsPacket decode(FriendlyByteBuf buf) {
        return new DisplaySettingsPacket(buf.readBoolean(), buf.readBoolean());
    }
    
    public boolean getShowHealth() { return showHealth; }
    public boolean isOp() { return isOp; }
} 