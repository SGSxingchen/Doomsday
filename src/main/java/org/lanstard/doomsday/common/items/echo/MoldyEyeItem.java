package org.lanstard.doomsday.common.items.echo;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import java.util.List;

public class MoldyEyeItem extends Item {
    public MoldyEyeItem(Properties properties) {
        super(properties.rarity(Rarity.RARE));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.doomsday.moldy_eye.tooltip").withStyle(ChatFormatting.GRAY));
    }
} 