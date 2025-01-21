package org.lanstard.doomsday.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import org.lanstard.doomsday.sanity.SanityManager;

public class BeliefPointsItem extends Item {
    public BeliefPointsItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            // 增加信念点
            SanityManager.modifyFaith(serverPlayer, 1);
            serverPlayer.sendSystemMessage(Component.literal("§b[十日终焉] §f你的信念增强了..."));
            
            // 消耗物品
            itemstack.shrink(1);
        }
        
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
} 