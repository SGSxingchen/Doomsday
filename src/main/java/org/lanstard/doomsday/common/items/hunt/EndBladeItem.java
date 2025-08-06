package org.lanstard.doomsday.common.items.hunt;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class EndBladeItem extends SwordItem {
    
    // 自定义材料属性
    private static final Tier END_BLADE_TIER = new Tier() {
        @Override
        public int getUses() {
            return 1561; // 钻石剑的耐久度
        }

        @Override
        public float getSpeed() {
            return 8.0F; // 钻石工具的挖掘速度
        }

        @Override
        public float getAttackDamageBonus() {
            return 5.0F; // 6点总伤害 (基础1 + 奖励5)
        }

        @Override
        public int getLevel() {
            return 3; // 钻石级别
        }

        @Override
        public int getEnchantmentValue() {
            return 10; // 钻石的附魔值
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY; // 无法修复
        }
    };
    
    public EndBladeItem() {
        super(END_BLADE_TIER, 1, -2.4F, new Properties().stacksTo(1).rarity(Rarity.EPIC)); // 攻击速度1.6 (4.0 - 2.4)
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§c攻击伤害: 6").withStyle(ChatFormatting.RED));
        tooltip.add(Component.literal("§c攻击速度: 1.6").withStyle(ChatFormatting.RED));
        tooltip.add(Component.literal("§6狩猎专用武器").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("§7锋利如末日之刃").withStyle(ChatFormatting.GRAY));
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
    
    public static ItemStack createEndBlade() {
        ItemStack blade = new ItemStack(org.lanstard.doomsday.common.items.ModItem.END_BLADE.get());
        
        // 添加消失诅咒
        blade.enchant(Enchantments.VANISHING_CURSE, 1);
        
        return blade;
    }
}