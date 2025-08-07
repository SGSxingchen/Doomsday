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
    private static final UUID MOVEMENT_SPEED_MODIFIER_UUID = UUID.fromString("7E0292F2-9434-48D5-BE49-85229C95DBCC");
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("7E0292F2-9434-48D5-BE49-85229C95DBCD");
    private static final UUID KNOCKBACK_RESISTANCE_MODIFIER_UUID = UUID.fromString("7E0292F2-9434-48D5-BE49-85229C95DBCE");
    
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
        
        // 每一级狩猎本能增加移动速度
        if (amplifier > 0) {
            double movementSpeedBonus = amplifier * (EchoConfig.WUZHONGSHOU_HUNT_INSTINCT_MOVEMENT_SPEED_PER_LEVEL.get() / 100.0);
            if (movementSpeedBonus > 0) {
                AttributeModifier movementSpeedModifier = new AttributeModifier(
                    MOVEMENT_SPEED_MODIFIER_UUID, 
                    "Hunt Instinct Movement Speed", 
                    movementSpeedBonus, 
                    AttributeModifier.Operation.MULTIPLY_BASE
                );
                
                var movementSpeedAttribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
                if (movementSpeedAttribute != null) {
                    movementSpeedAttribute.addTransientModifier(movementSpeedModifier);
                }
            }
        }
        
        // 每一级狩猎本能增加护甲值
        if (amplifier > 0) {
            double armorBonus = amplifier * EchoConfig.WUZHONGSHOU_HUNT_INSTINCT_ARMOR_PER_LEVEL.get();
            if (armorBonus > 0) {
                AttributeModifier armorModifier = new AttributeModifier(
                    ARMOR_MODIFIER_UUID, 
                    "Hunt Instinct Armor", 
                    armorBonus, 
                    AttributeModifier.Operation.ADDITION
                );
                
                var armorAttribute = livingEntity.getAttribute(Attributes.ARMOR);
                if (armorAttribute != null) {
                    armorAttribute.addTransientModifier(armorModifier);
                }
            }
        }
        
        // 每一级狩猎本能增加击退抗性
        if (amplifier > 0) {
            double knockbackResistanceBonus = amplifier * (EchoConfig.WUZHONGSHOU_HUNT_INSTINCT_KNOCKBACK_RESISTANCE_PER_LEVEL.get() / 100.0);
            if (knockbackResistanceBonus > 0) {
                AttributeModifier knockbackResistanceModifier = new AttributeModifier(
                    KNOCKBACK_RESISTANCE_MODIFIER_UUID, 
                    "Hunt Instinct Knockback Resistance", 
                    knockbackResistanceBonus, 
                    AttributeModifier.Operation.ADDITION
                );
                
                var knockbackResistanceAttribute = livingEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
                if (knockbackResistanceAttribute != null) {
                    knockbackResistanceAttribute.addTransientModifier(knockbackResistanceModifier);
                }
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
        
        var movementSpeedAttribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeedAttribute != null) {
            movementSpeedAttribute.removeModifier(MOVEMENT_SPEED_MODIFIER_UUID);
        }
        
        var armorAttribute = livingEntity.getAttribute(Attributes.ARMOR);
        if (armorAttribute != null) {
            armorAttribute.removeModifier(ARMOR_MODIFIER_UUID);
        }
        
        var knockbackResistanceAttribute = livingEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (knockbackResistanceAttribute != null) {
            knockbackResistanceAttribute.removeModifier(KNOCKBACK_RESISTANCE_MODIFIER_UUID);
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