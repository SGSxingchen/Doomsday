package org.lanstard.doomsday.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class DecayedEchoEyeItem extends Item {
    private static final FoodProperties FOOD_PROPERTIES = new FoodProperties.Builder()
        .nutrition(2)
        .saturationMod(0.2f)
        .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 1.0f)
        .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 300, 0), 0.5f)
        .alwaysEat()
        .build();

    public DecayedEchoEyeItem(Properties properties) {
        super(properties.food(FOOD_PROPERTIES));
    }
} 