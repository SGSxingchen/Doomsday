package org.lanstard.doomsday.common.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.lanstard.doomsday.echo.Echo;

import javax.annotation.Nullable;
import java.util.List;

public class EchoEyeItem extends Item {
    private static final String TAG_ECHO = "Echo";
    private static final String TAG_DECAY_TIME = "DecayTime";
    private static final int DEFAULT_DECAY_TIME = 12000; // 10分钟 = 20ticks * 60 * 10
    private static final FoodProperties FOOD_PROPERTIES = new FoodProperties.Builder()
        .nutrition(4)
        .saturationMod(0.5f)
        .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0), 1.0f)
        .alwaysEat()
        .build();

    public EchoEyeItem(Properties properties) {
        super(properties.stacksTo(1).food(FOOD_PROPERTIES));
    }

    public static void setEcho(ItemStack stack, Echo echo) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.put(TAG_ECHO, echo.toNBT());
        tag.putInt(TAG_DECAY_TIME, DEFAULT_DECAY_TIME);
    }

    @Nullable
    public static Echo getEcho(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_ECHO)) {
            return Echo.fromNBT(tag.getCompound(TAG_ECHO));
        }
        return null;
    }

    public static int getDecayTime(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getInt(TAG_DECAY_TIME) : 0;
    }

    public static void decreaseDecayTime(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        int time = tag.getInt(TAG_DECAY_TIME);
        if (time > 0) {
            tag.putInt(TAG_DECAY_TIME, time - 1);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Echo echo = getEcho(stack);
        if (echo != null) {
            tooltip.add(Component.literal("§e回响: " + echo.getName()));
            int decayTime = getDecayTime(stack);
            if (decayTime > 0) {
                int seconds = decayTime / 20;
                tooltip.add(Component.literal(String.format("§c腐烂倒计时: %d:%02d", seconds / 60, seconds % 60)));
            }
        }
    }
} 