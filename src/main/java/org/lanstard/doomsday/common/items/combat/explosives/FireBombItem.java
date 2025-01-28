package org.lanstard.doomsday.common.items.combat.explosives;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import org.lanstard.doomsday.common.entities.FireBombEntity;

import javax.annotation.Nullable;
import java.util.List;

public class FireBombItem extends Item {
    public FireBombItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            FireBombEntity fireBomb = new FireBombEntity(level, player);
            fireBomb.setItem(itemstack);
            
            // 从物品NBT中读取强化等级
            if (itemstack.hasTag() && itemstack.getTag().contains("EnhancedLevel")) {
                fireBomb.setEnhanced(itemstack.getTag().getInt("EnhancedLevel"));
            }
            
            // 根据强化等级调整投掷速度和精确度
            float speed = 1.5F;
            float inaccuracy = 1.0F;
            
            if (itemstack.hasTag()) {
                int enhancedLevel = itemstack.getTag().getInt("EnhancedLevel");
                if (enhancedLevel >= 2) {
                    speed = 2.0F;
                    inaccuracy = 0.5F;
                } else if (enhancedLevel >= 1) {
                    speed = 1.75F;
                    inaccuracy = 0.75F;
                }
            }
            
            fireBomb.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, speed, inaccuracy);
            level.addFreshEntity(fireBomb);
        }

        if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        // 添加基础提示
        tooltip.add(Component.translatable("item.doomsday.fire_bomb.tooltip"));
        
        // 添加强化等级提示
        if (stack.hasTag() && stack.getTag().contains("EnhancedLevel")) {
            int enhancedLevel = stack.getTag().getInt("EnhancedLevel");
            String levelText = enhancedLevel >= 2 ? "高等" : (enhancedLevel >= 1 ? "中等" : "普通");
            tooltip.add(Component.literal("§6强化等级: " + levelText));
            
            // 添加伤害提示
            float maxDamage = enhancedLevel >= 2 ? 20.0f : (enhancedLevel >= 1 ? 15.0f : 10.0f);
            float minDamage = enhancedLevel >= 2 ? 8.0f : (enhancedLevel >= 1 ? 6.0f : 4.0f);
            tooltip.add(Component.literal("§c伤害: " + minDamage + "-" + maxDamage));
            
            // 添加效果提示
            if (enhancedLevel >= 2) {
                tooltip.add(Component.literal("§7效果: 燃烧(10秒) + 虚弱(5秒)"));
            } else if (enhancedLevel >= 1) {
                tooltip.add(Component.literal("§7效果: 燃烧(5秒)"));
            }
        }
    }
} 