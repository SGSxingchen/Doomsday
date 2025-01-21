package org.lanstard.doomsday.network.packet;

import net.minecraft.network.FriendlyByteBuf;

public class PlayAnimationPacket {
    private final String animationId;

    public PlayAnimationPacket(String animationId) {
        this.animationId = animationId;
    }

    public static void encode(PlayAnimationPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.animationId);
    }

    public static PlayAnimationPacket decode(FriendlyByteBuf buf) {
        return new PlayAnimationPacket(buf.readUtf());
    }

    public String getAnimationId() {
        return animationId;
    }
}