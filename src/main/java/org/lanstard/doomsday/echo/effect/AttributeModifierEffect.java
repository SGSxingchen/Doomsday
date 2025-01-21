package org.lanstard.doomsday.echo.effect;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.UUID;

public class AttributeModifierEffect extends EchoEffect {
    private final Attribute attribute;
    private final double amount;
    private final AttributeModifier.Operation operation;
    private final UUID modifierId;
    
    public AttributeModifierEffect(String id, String name, int duration, int amplifier,
                                 Attribute attribute, double amount, 
                                 AttributeModifier.Operation operation) {
        super(id, name, duration, amplifier);
        this.attribute = attribute;
        this.amount = amount;
        this.operation = operation;
        this.modifierId = UUID.randomUUID();
    }
    
    @Override
    public void onActivate(ServerPlayer player) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.addTransientModifier(createModifier());
        }
    }
    
    @Override
    public void onUpdate(ServerPlayer player) {
        // 属性修改器是持续性的,不需要每tick更新
    }
    
    @Override
    public void onDeactivate(ServerPlayer player) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(modifierId);
        }
    }
    
    private AttributeModifier createModifier() {
        return new AttributeModifier(
            modifierId,
            getName(),
            amount * getAmplifier(),
            operation
        );
    }
} 