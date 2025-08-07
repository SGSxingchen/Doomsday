package org.lanstard.doomsday.common.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import org.lanstard.doomsday.common.data.HuntData;
import org.lanstard.doomsday.config.EchoConfig;

import java.util.UUID;

public class HuntedMarkEffect extends MobEffect {
    
    public HuntedMarkEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF6B1A1A); // 深红色
    }
    
    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity instanceof ServerPlayer player) {
            int speedLevel = EchoConfig.WUZHONGSHOU_HUNTED_MARK_SPEED_LEVEL.get();
            int weaknessLevel = EchoConfig.WUZHONGSHOU_HUNTED_MARK_WEAKNESS_LEVEL.get();
            
            // 应用速度效果
            if (speedLevel > 0) {
                if (!player.hasEffect(MobEffects.MOVEMENT_SPEED) || 
                    player.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() < speedLevel) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, speedLevel, false, false));
                }
            }
            
            // 应用虚弱效果
            if (weaknessLevel >= 0) {
                if (!player.hasEffect(MobEffects.WEAKNESS) || 
                    player.getEffect(MobEffects.WEAKNESS).getAmplifier() < weaknessLevel) {
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, weaknessLevel, false, false));
                }
            }
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0; // 每秒执行一次
    }
}