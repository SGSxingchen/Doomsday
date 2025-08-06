package org.lanstard.doomsday.common.items.hunt;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import javax.annotation.Nullable;
import java.util.List;

public class InvisibilityCloakItem extends ArmorItem {
    
    // 自定义盔甲材料
    private static final ArmorMaterial INVISIBILITY_CLOAK_MATERIAL = new ArmorMaterial() {
        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return 1000; // 高耐久度
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return 0; // 无防御力
        }

        @Override
        public int getEnchantmentValue() {
            return 15; // 高附魔值
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_LEATHER;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY; // 无法修复
        }

        @Override
        public String getName() {
            return "invisibility_cloak";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };
    
    public InvisibilityCloakItem() {
        super(INVISIBILITY_CLOAK_MATERIAL, Type.CHESTPLATE, new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }
    
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide() && entity instanceof Player player) {
            // 检查是否装备在胸甲槽
            if (player.getItemBySlot(EquipmentSlot.CHEST).equals(stack)) {
                // 给予隐身效果（无粒子）
                if (!player.hasEffect(MobEffects.INVISIBILITY) || 
                    player.getEffect(MobEffects.INVISIBILITY).getDuration() < 40) {
                    player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 100, 0, false, false, false));
                }
                
                // 给予速度1效果
                if (!player.hasEffect(MobEffects.MOVEMENT_SPEED) || 
                    player.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() < 0 ||
                    player.getEffect(MobEffects.MOVEMENT_SPEED).getDuration() < 40) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 0, false, false));
                }
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§9效果: 隐身 + 速度I").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.literal("§7穿在胸甲槽时生效").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("§6狩猎专用装备").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("§7如影随形的神秘斗篷").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("§c消失诅咒 - 无法丢弃").withStyle(ChatFormatting.RED));
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // 始终发光
    }
    
    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return false; // 无法修复
    }
    
    @Override
    public boolean canBeDepleted() {
        return false; // 不会损坏
    }
    
    public static ItemStack createInvisibilityCloak() {
        ItemStack cloak = new ItemStack(org.lanstard.doomsday.common.items.ModItem.INVISIBILITY_CLOAK.get());
        
        // 添加消失诅咒
        cloak.enchant(Enchantments.VANISHING_CURSE, 1);
        
        return cloak;
    }
}