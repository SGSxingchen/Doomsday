package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;

public class JiaJieEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.JIAJIE;

    public JiaJieEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            
            0,  // 无主动消耗
            0   // 无被动消耗
        );
        setActive(true); // 默认激活
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...嫁接之力觉醒，你现在可以为他人装配回响..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 纯被动能力，不需要更新
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...嫁接之力消散..."));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 这是一个纯被动技能，不需要手动开关
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...嫁接之力无法人为控制..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 这是一个纯被动技能，不需要主动使用
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...嫁接并非主动之力..."));
        return false;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 这是一个纯被动技能，不需要主动使用
    }
} 