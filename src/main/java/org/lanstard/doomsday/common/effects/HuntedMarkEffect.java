package org.lanstard.doomsday.common.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import org.lanstard.doomsday.common.data.HuntData;

import java.util.UUID;

public class HuntedMarkEffect extends MobEffect {
    
    public HuntedMarkEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF6B1A1A); // 深红色
    }
    
    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity instanceof ServerPlayer player) {
            // 每秒检查并应用速度2和虚弱1效果
            if (!player.hasEffect(MobEffects.MOVEMENT_SPEED) || 
                player.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() < 1) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 1, false, false));
            }
            
            if (!player.hasEffect(MobEffects.WEAKNESS) || 
                player.getEffect(MobEffects.WEAKNESS).getAmplifier() < 0) {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, false, false));
            }
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0; // 每秒执行一次
    }
}