package org.lanstard.doomsday.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import org.lanstard.doomsday.client.render.World3DTextPreset;
import org.lanstard.doomsday.client.render.World3DTextRenderer;

import java.util.UUID;
import java.util.function.Supplier;

public class SpawnWorld3DTextPacket {
    private static class CustomParams {
        final int color;
        final float scale;
        final float alpha;
        final boolean glowing;
        final int duration;
        final float fadeInTime;
        final float fadeOutTime;
        final boolean facingPlayer;
        final float rotationX;
        final float rotationY;
        final float rotationZ;

        CustomParams(int color, float scale, float alpha, boolean glowing,
                    int duration, float fadeInTime, float fadeOutTime,
                    boolean facingPlayer, float rotationX, float rotationY, float rotationZ) {
            this.color = color;
            this.scale = scale;
            this.alpha = alpha;
            this.glowing = glowing;
            this.duration = duration;
            this.fadeInTime = fadeInTime;
            this.fadeOutTime = fadeOutTime;
            this.facingPlayer = facingPlayer;
            this.rotationX = rotationX;
            this.rotationY = rotationY;
            this.rotationZ = rotationZ;
        }

        void encode(FriendlyByteBuf buf) {
            buf.writeInt(color);
            buf.writeFloat(scale);
            buf.writeFloat(alpha);
            buf.writeBoolean(glowing);
            buf.writeInt(duration);
            buf.writeFloat(fadeInTime);
            buf.writeFloat(fadeOutTime);
            buf.writeBoolean(facingPlayer);
            buf.writeFloat(rotationX);
            buf.writeFloat(rotationY);
            buf.writeFloat(rotationZ);
        }

        static CustomParams decode(FriendlyByteBuf buf) {
            return new CustomParams(
                buf.readInt(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readBoolean(),
                buf.readInt(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readBoolean(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat()
            );
        }
    }

    private final String text;
    private final World3DTextPreset preset;
    private final double radius;
    private final boolean isClear;
    private final Vec3 position;
    private final UUID targetPlayerId;
    private final CustomParams customParams;

    // 自定义参数构造函数
    public SpawnWorld3DTextPacket(
            String text, Vec3 position, double radius, UUID targetPlayerId,
            int color, float scale, float alpha, boolean glowing,
            int duration, float fadeInTime, float fadeOutTime,
            boolean facingPlayer, float rotationX, float rotationY, float rotationZ) {
        this.text = text;
        this.position = position;
        this.radius = radius;
        this.targetPlayerId = targetPlayerId;
        this.preset = null;
        this.isClear = false;
        this.customParams = new CustomParams(
            color, scale, alpha, glowing,
            duration, fadeInTime, fadeOutTime,
            facingPlayer, rotationX, rotationY, rotationZ
        );
    }

    // 预设构造函数
    public SpawnWorld3DTextPacket(String text, World3DTextPreset preset, double radius, 
                                Vec3 position, UUID targetPlayerId) {
        this.text = text;
        this.preset = preset;
        this.radius = radius;
        this.position = position;
        this.targetPlayerId = targetPlayerId;
        this.isClear = false;
        this.customParams = null;
    }

    // 清除构造函数
    public SpawnWorld3DTextPacket(UUID targetPlayerId, boolean clear) {
        this.text = "";
        this.preset = null;
        this.radius = 0;
        this.position = null;
        this.targetPlayerId = targetPlayerId;
        this.isClear = clear;
        this.customParams = null;
    }

    public static void encode(SpawnWorld3DTextPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.isClear);
        buf.writeUUID(msg.targetPlayerId);
        
        if (!msg.isClear) {
            buf.writeUtf(msg.text);
            buf.writeBoolean(msg.preset != null);
            if (msg.preset != null) {
                buf.writeEnum(msg.preset);
            } else {
                msg.customParams.encode(buf);
            }
            buf.writeDouble(msg.radius);
            if (msg.position != null) {
                buf.writeDouble(msg.position.x);
                buf.writeDouble(msg.position.y);
                buf.writeDouble(msg.position.z);
            }
        }
    }

    public static SpawnWorld3DTextPacket decode(FriendlyByteBuf buf) {
        boolean isClear = buf.readBoolean();
        UUID targetId = buf.readUUID();
        
        if (isClear) {
            return new SpawnWorld3DTextPacket(targetId, true);
        }
        
        String text = buf.readUtf();
        boolean hasPreset = buf.readBoolean();
        
        if (hasPreset) {
            World3DTextPreset preset = buf.readEnum(World3DTextPreset.class);
            double radius = buf.readDouble();
            Vec3 pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
            return new SpawnWorld3DTextPacket(text, preset, radius, pos, targetId);
        } else {
            CustomParams params = CustomParams.decode(buf);
            double radius = buf.readDouble();
            Vec3 pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
            
            return new SpawnWorld3DTextPacket(
                text, pos, radius, targetId,
                params.color, params.scale, params.alpha, params.glowing,
                params.duration, params.fadeInTime, params.fadeOutTime,
                params.facingPlayer, params.rotationX, params.rotationY, params.rotationZ
            );
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (!ctx.get().getDirection().getReceptionSide().isClient()) {
                return;
            }

            if (isClear) {
                World3DTextRenderer.clearAll(targetPlayerId);
            } else if (preset != null) {
                World3DTextRenderer.spawnText(text, preset, position, radius, targetPlayerId);
            } else {
                World3DTextRenderer.spawnCustomText(
                    text, position, radius, targetPlayerId,
                    customParams.color, customParams.scale, customParams.alpha, customParams.glowing,
                    customParams.duration, customParams.fadeInTime, customParams.fadeOutTime,
                    customParams.facingPlayer, customParams.rotationX, customParams.rotationY, customParams.rotationZ
                );
            }
        });
        ctx.get().setPacketHandled(true);
    }
}