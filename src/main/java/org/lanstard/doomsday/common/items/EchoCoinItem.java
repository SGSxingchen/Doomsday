package org.lanstard.doomsday.common.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.lanstard.doomsday.echo.Echo;

import javax.annotation.Nullable;
import java.util.List;

public class EchoCoinItem extends Item {
    private static final String TAG_ECHO = "Echo";

    public EchoCoinItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static void setEcho(ItemStack stack, Echo echo) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.put(TAG_ECHO, echo.toNBT());
    }

    @Nullable
    public static Echo getEcho(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_ECHO)) {
            return Echo.fromNBT(tag.getCompound(TAG_ECHO));
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Echo echo = getEcho(stack);
        if (echo != null) {
            tooltip.add(Component.literal("§e回响: " + echo.getName()));
            tooltip.add(Component.literal("§6永恒的回响货币"));
        }
    }
} 