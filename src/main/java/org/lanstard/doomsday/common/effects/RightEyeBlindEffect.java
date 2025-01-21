package org.lanstard.doomsday.common.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class RightEyeBlindEffect extends MobEffect {
    public RightEyeBlindEffect() {
        super(MobEffectCategory.HARMFUL, 0x800000); // 深红色
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 效果的状态由事件处理器管理
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
} 