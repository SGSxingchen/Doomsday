package org.lanstard.doomsday.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraft.client.gui.screens.Screen;
import javax.annotation.Nullable;
import java.util.List;

public class DaoItem extends Item {
    public DaoItem(Properties properties) {
        super(properties.rarity(Rarity.EPIC).food(new net.minecraft.world.food.FoodProperties.Builder()
            .nutrition(10)  // 提供10点饱食度
            .saturationMod(5.0f)  // 5的饱和度
            .alwaysEat()  // 允许在饱食度满时食用
            .build())
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.doomsday.dao.tooltip").withStyle(ChatFormatting.GRAY));
        
        // 当按下CTRL键时显示额外信息
        if (Screen.hasControlDown()) {
            tooltip.add(Component.translatable("item.doomsday.dao.tooltip.ctrl").withStyle(ChatFormatting.DARK_PURPLE));
        }
    }
} 