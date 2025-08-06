package org.lanstard.doomsday.common.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.server.level.ServerPlayer;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.config.EchoConfig;

import java.util.UUID;

public class HuntInstinctEffect extends MobEffect {
    
    private static final UUID DAMAGE_MODIFIER_UUID = UUID.fromString("7E0292F2-9434-48D5-BE49-85229C95DBCA");
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("7E0292F2-9434-48D5-BE49-85229C95DBCB");
    
    public HuntInstinctEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF8B0000); // 深红色
    }
    
    @Override
    public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
        // 每一级狩猎本能增加1点近战伤害（amplifier 0 = 信念0，无效果；amplifier 1 = 信念1，1点伤害）
        if (amplifier > 0) {
            double damageBonus = amplifier * EchoConfig.WUZHONGSHOU_HUNT_INSTINCT_DAMAGE_PER_LEVEL.get();
            AttributeModifier damageModifier = new AttributeModifier(
                DAMAGE_MODIFIER_UUID, 
                "Hunt Instinct Damage", 
                damageBonus, 
                AttributeModifier.Operation.ADDITION
            );
            
            var damageAttribute = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);
            if (damageAttribute != null) {
                damageAttribute.addTransientModifier(damageModifier);
            }
        }
        
        // 每一级狩猎本能增加5%攻击速度
        if (amplifier > 0) {
            double speedBonus = amplifier * (EchoConfig.WUZHONGSHOU_HUNT_INSTINCT_SPEED_PER_LEVEL.get() / 100.0);
            AttributeModifier speedModifier = new AttributeModifier(
                SPEED_MODIFIER_UUID, 
                "Hunt Instinct Speed", 
                speedBonus, 
                AttributeModifier.Operation.MULTIPLY_BASE
            );
            
            var speedAttribute = livingEntity.getAttribute(Attributes.ATTACK_SPEED);
            if (speedAttribute != null) {
                speedAttribute.addTransientModifier(speedModifier);
            }
        }
        
        super.addAttributeModifiers(livingEntity, attributeMap, amplifier);
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
        var damageAttribute = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damageAttribute != null) {
            damageAttribute.removeModifier(DAMAGE_MODIFIER_UUID);
        }
        
        var speedAttribute = livingEntity.getAttribute(Attributes.ATTACK_SPEED);
        if (speedAttribute != null) {
            speedAttribute.removeModifier(SPEED_MODIFIER_UUID);
        }
        
        super.removeAttributeModifiers(livingEntity, attributeMap, amplifier);
    }
    
    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        // 不需要特殊处理，属性修改器在效果应用时自动生效
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false; // 不需要定时更新
    }
}