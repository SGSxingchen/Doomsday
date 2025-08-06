package org.lanstard.doomsday.common.items.echo;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import java.util.List;

public class HeartLockItem extends Item {
    public HeartLockItem(Properties properties) {
        super(properties.rarity(Rarity.RARE).stacksTo(1));
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
        
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("target_player")) {
            String targetName = tag.getString("target_player");
            if (!targetName.isEmpty()) {
                tooltipComponents.add(Component.literal("§c来自: " + targetName).withStyle(ChatFormatting.DARK_RED));
                tooltipComponents.add(Component.literal("§7一颗被夺走的心...").withStyle(ChatFormatting.GRAY));
            }
        }
        tooltipComponents.add(Component.literal("§8[心锁]").withStyle(ChatFormatting.DARK_GRAY));
    }
    
    /**
     * 创建包含目标玩家信息的心锁道具
     * @param targetPlayerName 目标玩家的名称
     * @return 带有玩家信息的心锁道具
     */
    public static ItemStack createWithTarget(String targetPlayerName) {
        ItemStack stack = new ItemStack(org.lanstard.doomsday.common.items.ModItem.HEART_LOCK.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("target_player", targetPlayerName);
        stack.setTag(tag);
        return stack;
    }
}