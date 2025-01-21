package org.lanstard.doomsday.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class SoulCompassItem extends Item implements ICurioItem {
    public SoulCompassItem() {
        super(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
        tooltipComponents.add(Component.translatable("item.doomsday.soul_compass.tooltip"));
        super.appendHoverText(stack, level, tooltipComponents, flag);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 确保只能装备在"功能"栏位
        return slotContext.identifier().equals("function");
    }

    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        // 当装备时触发的效果
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        // 当卸下时触发的效果
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 每tick执行的逻辑
    }
} 