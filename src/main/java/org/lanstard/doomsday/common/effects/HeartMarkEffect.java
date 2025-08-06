package org.lanstard.doomsday.common.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class HeartMarkEffect extends MobEffect {
    public HeartMarkEffect() {
        super(MobEffectCategory.NEUTRAL, 0xFF6B6B); // 红色效果颜色
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false; // 不需要周期性效果
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        // 心之印效果不需要周期性处理，主要用于标记和存储施法者信息
    }
    
    /**
     * 获取效果的显示名称
     */
    public Component getDisplayName() {
        return Component.translatable("effect.doomsday.heart_mark");
    }
}