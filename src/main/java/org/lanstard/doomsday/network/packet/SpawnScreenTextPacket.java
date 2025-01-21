package org.lanstard.doomsday.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.lanstard.doomsday.client.gui.text.ScreenTextManager;
import org.lanstard.doomsday.client.gui.text.ScreenTextPreset;

import java.util.function.Supplier;

public class SpawnScreenTextPacket {
    private final String text;
    private final ScreenTextPreset preset;
    private final boolean isBatch;
    private final String[] batchTexts;
    private final boolean isClear;
    private final CustomParams customParams;

    // 单个文本构造函数
    public SpawnScreenTextPacket(String text, int color, float scale, float alpha, boolean glowing,
                                 int duration, int fadeInTime, int fadeOutTime,
                                 float scaleStart, float scaleEnd, float moveSpeed,
                                 float rotationSpeed, ScreenTextPreset.Direction direction) {
        this.text = text;
        this.preset = null;
        this.isBatch = false;
        this.batchTexts = null;
        this.isClear = false;
        // 保存自定义参数
        this.customParams = new CustomParams(
            color, scale, alpha, glowing, duration, fadeInTime, fadeOutTime,
            scaleStart, scaleEnd, moveSpeed, rotationSpeed, direction
        );
    }

    // 批量文本构造函数
    public SpawnScreenTextPacket(String[] texts, ScreenTextPreset preset) {
        this.text = null;
        this.preset = preset;
        this.isClear = false;
        this.isBatch = true;
        this.batchTexts = texts;
        this.customParams = null;
    }

    public SpawnScreenTextPacket(String text, ScreenTextPreset preset) {
        this.text = text;
        this.preset = preset;
        this.isClear = false;
        this.isBatch = false;
        this.batchTexts = null;
        this.customParams = null;
    }

    public SpawnScreenTextPacket(String text, ScreenTextPreset preset, boolean clear) {
        this.text = text;
        this.preset = preset;
        this.isClear = clear;
        this.isBatch = false;
        this.batchTexts = null;
        this.customParams = null;
    }

    public static void encode(SpawnScreenTextPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.isBatch);
        buf.writeBoolean(msg.preset != null);
        
        if (msg.preset != null) {
            buf.writeEnum(msg.preset);
        }
        
        if (msg.isBatch) {
            buf.writeInt(msg.batchTexts.length);
            for (String text : msg.batchTexts) {
                buf.writeUtf(text);
            }
        } else {
            buf.writeUtf(msg.text);
            if (msg.customParams != null) {
                buf.writeInt(msg.customParams.color);
                buf.writeFloat(msg.customParams.scale);
                buf.writeFloat(msg.customParams.alpha);
                buf.writeBoolean(msg.customParams.glowing);
                buf.writeInt(msg.customParams.duration);
                buf.writeInt(msg.customParams.fadeInTime);
                buf.writeInt(msg.customParams.fadeOutTime);
                buf.writeFloat(msg.customParams.scaleStart);
                buf.writeFloat(msg.customParams.scaleEnd);
                buf.writeFloat(msg.customParams.moveSpeed);
                buf.writeFloat(msg.customParams.rotationSpeed);
                buf.writeEnum(msg.customParams.direction);
            }
        }
    }

    public static SpawnScreenTextPacket decode(FriendlyByteBuf buf) {
        boolean isBatch = buf.readBoolean();
        boolean hasPreset = buf.readBoolean();
        ScreenTextPreset preset = hasPreset ? buf.readEnum(ScreenTextPreset.class) : null;

        if (isBatch) {
            int count = buf.readInt();
            String[] texts = new String[count];
            for (int i = 0; i < count; i++) {
                texts[i] = buf.readUtf();
            }
            return new SpawnScreenTextPacket(texts, preset);
        } else {
            String text = buf.readUtf();
            if (!hasPreset) {
                return new SpawnScreenTextPacket(
                    text,
                    buf.readInt(),      // color
                    buf.readFloat(),    // scale
                    buf.readFloat(),    // alpha
                    buf.readBoolean(), // glowing
                    buf.readInt(),     // duration
                    buf.readInt(),     // fadeInTime
                    buf.readInt(),     // fadeOutTime
                    buf.readFloat(),   // scaleStart
                    buf.readFloat(),   // scaleEnd
                    buf.readFloat(),   // moveSpeed
                    buf.readFloat(),   // rotationSpeed
                    buf.readEnum(ScreenTextPreset.Direction.class) // direction
                );
            }
            return new SpawnScreenTextPacket(text, preset);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (isClear) {
                ScreenTextManager.clearAll();
            } else if (isBatch) {
                for (String text : batchTexts) {
                    ScreenTextManager.addText(text, preset);
                }
            } else if (customParams != null) {
                ScreenTextManager.addCustomText(text, customParams.color, customParams.scale,
                    customParams.alpha, customParams.glowing, customParams.duration, customParams.fadeInTime,
                    customParams.fadeOutTime, customParams.scaleStart, customParams.scaleEnd,
                    customParams.moveSpeed, customParams.rotationSpeed, customParams.direction);
            } else {
                ScreenTextManager.addText(text, preset);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static class CustomParams {
        final int color;
        final float scale;
        final float alpha;
        final boolean glowing;
        final int duration;
        final int fadeInTime;
        final int fadeOutTime;
        final float scaleStart;
        final float scaleEnd;
        final float moveSpeed;
        final float rotationSpeed;
        final ScreenTextPreset.Direction direction;

        CustomParams(int color, float scale, float alpha, boolean glowing, int duration,
                    int fadeInTime, int fadeOutTime, float scaleStart, float scaleEnd,
                    float moveSpeed, float rotationSpeed, ScreenTextPreset.Direction direction) {
            this.color = color;
            this.scale = scale;
            this.alpha = alpha;
            this.glowing = glowing;
            this.duration = duration;
            this.fadeInTime = fadeInTime;
            this.fadeOutTime = fadeOutTime;
            this.scaleStart = scaleStart;
            this.scaleEnd = scaleEnd;
            this.moveSpeed = moveSpeed;
            this.rotationSpeed = rotationSpeed;
            this.direction = direction;
        }
    }
} 