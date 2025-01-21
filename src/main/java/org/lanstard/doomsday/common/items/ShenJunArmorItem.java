package org.lanstard.doomsday.common.items;

import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;

import java.util.EnumMap;

public class ShenJunArmorItem extends ArmorItem {
    private static final ArmorMaterial SHENJUN = new ArmorMaterial() {
        private static final EnumMap<Type, Integer> DURABILITY_PER_SLOT = Util.make(new EnumMap<>(Type.class), (map) -> {
            map.put(Type.BOOTS, 481);
            map.put(Type.LEGGINGS, 555);
            map.put(Type.CHESTPLATE, 592);
            map.put(Type.HELMET, 407);
        });

        @Override
        public int getDurabilityForType(Type type) {
            return DURABILITY_PER_SLOT.get(type);
        }

        @Override
        public int getDefenseForType(Type type) {
            switch (type) {
                case BOOTS: return 4;
                case LEGGINGS: return 7;
                case CHESTPLATE: return 9;
                case HELMET: return 4;
                default: return 0;
            }
        }

        @Override
        public int getEnchantmentValue() {
            return 25;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_NETHERITE;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(Items.NETHERITE_INGOT);
        }

        @Override
        public String getName() {
            return "shenjun"; // 这个名字会用于材质路径
        }

        @Override
        public float getToughness() {
            return 4.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.2F;
        }
    };

    public ShenJunArmorItem(Type type, Item.Properties properties) {
        super(SHENJUN, type, properties.stacksTo(1));
    }
} 