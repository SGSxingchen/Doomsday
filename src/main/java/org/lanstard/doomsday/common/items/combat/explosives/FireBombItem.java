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
import org.lanstard.doomsday.config.EchoConfig;

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
            float speed = EchoConfig.FIRE_BOMB_BASE_SPEED.get().floatValue();
            float inaccuracy = EchoConfig.FIRE_BOMB_BASE_INACCURACY.get().floatValue();
            
            if (itemstack.hasTag()) {
                int enhancedLevel = itemstack.getTag().getInt("EnhancedLevel");
                if (enhancedLevel >= 2) {
                    speed = EchoConfig.FIRE_BOMB_HIGH_SPEED.get().floatValue();
                    inaccuracy = EchoConfig.FIRE_BOMB_HIGH_INACCURACY.get().floatValue();
                } else if (enhancedLevel >= 1) {
                    speed = EchoConfig.FIRE_BOMB_MID_SPEED.get().floatValue();
                    inaccuracy = EchoConfig.FIRE_BOMB_MID_INACCURACY.get().floatValue();
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
            String levelKey = enhancedLevel >= 2 ? "high" : (enhancedLevel >= 1 ? "mid" : "base");
            tooltip.add(Component.translatable("item.doomsday.fire_bomb.enhance_level." + levelKey));
            
            // 添加伤害提示
            float maxDamage;
            float minDamage;
            if (enhancedLevel >= 2) {
                maxDamage = EchoConfig.FIRE_BOMB_HIGH_MAX_DAMAGE.get().floatValue();
                minDamage = EchoConfig.FIRE_BOMB_HIGH_MIN_DAMAGE.get().floatValue();
            } else if (enhancedLevel >= 1) {
                maxDamage = EchoConfig.FIRE_BOMB_MID_MAX_DAMAGE.get().floatValue();
                minDamage = EchoConfig.FIRE_BOMB_MID_MIN_DAMAGE.get().floatValue();
            } else {
                maxDamage = EchoConfig.FIRE_BOMB_BASE_MAX_DAMAGE.get().floatValue();
                minDamage = EchoConfig.FIRE_BOMB_BASE_MIN_DAMAGE.get().floatValue();
            }
            tooltip.add(Component.translatable("item.doomsday.fire_bomb.damage", minDamage, maxDamage));
            
            // 添加效果提示
            if (enhancedLevel >= 2) {
                tooltip.add(Component.translatable("item.doomsday.fire_bomb.effect.high", 
                    EchoConfig.FIRE_BOMB_HIGH_BURN_DURATION.get(),
                    EchoConfig.FIRE_BOMB_WEAKNESS_DURATION.get() / 20));
            } else if (enhancedLevel >= 1) {
                tooltip.add(Component.translatable("item.doomsday.fire_bomb.effect.mid",
                    EchoConfig.FIRE_BOMB_MID_BURN_DURATION.get()));
            }
        }
    }
} 