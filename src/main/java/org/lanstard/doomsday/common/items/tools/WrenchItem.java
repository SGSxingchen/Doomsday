package org.lanstard.doomsday.common.items.tools;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class WrenchItem extends Item {
    public WrenchItem(Properties rarity) {
        super(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
        tooltipComponents.add(Component.translatable("item.doomsday.wrench.tooltip"));
        super.appendHoverText(stack, level, tooltipComponents, flag);
    }
} 