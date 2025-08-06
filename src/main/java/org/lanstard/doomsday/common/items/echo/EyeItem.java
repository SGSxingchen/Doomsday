package org.lanstard.doomsday.common.items.echo;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.level.ServerPlayer;
import org.lanstard.doomsday.common.items.ModItem;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.config.DoomsdayConfig;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import net.minecraft.world.item.TooltipFlag;

public class EyeItem extends AbstractEchoStorageItem {
    private static final String TAG_CREATION_TIME = "CreationTime";

    public EyeItem(Properties properties) {
        super(properties.rarity(Rarity.COMMON).stacksTo(1));
    }

    @Override
    protected Component getEquipSuccessMessage() {
        return Component.literal("§b...成功为目标装配了眼球...");
    }

    @Override
    protected Component getEquippedMessage() {
        return Component.literal("§b...你被装配了眼球...");
    }

    @Override
    protected Component getEquipFailMessage() {
        return Component.literal("§c...目标无法装配更多眼球...");
    }

    @Override
    protected Component getTooltipTranslation() {
        return Component.translatable("item.doomsday.eye.tooltip");
    }

    @Override
    public void storeEcho(ItemStack stack, Echo echo) {
        super.storeEcho(stack, echo);
        // 设置创建时间（如果还没有设置）
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(TAG_CREATION_TIME)) {
            tag.putLong(TAG_CREATION_TIME, System.currentTimeMillis());
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 显示腐烂进度
        if (Screen.hasControlDown()) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains(TAG_CREATION_TIME)) {
                long creationTime = tag.getLong(TAG_CREATION_TIME);
                long elapsedTime = System.currentTimeMillis() - creationTime;
                long decayTime = DoomsdayConfig.EYE_DECAY_TIME_HOURS.get() * 3600000L; // 小时转毫秒
                int progress = (int) ((float) elapsedTime / decayTime * 100);
                progress = Math.min(progress, 100);
                
                ChatFormatting color;
                if (progress < 30) color = ChatFormatting.GREEN;
                else if (progress < 60) color = ChatFormatting.YELLOW;
                else if (progress < 90) color = ChatFormatting.GOLD;
                else color = ChatFormatting.RED;
                
                tooltip.add(Component.literal("腐烂进度: " + progress + "%").withStyle(color));
                
                if (progress < 100) {
                    long remainingTime = (decayTime - elapsedTime) / 1000;
                    long minutes = remainingTime / 60;
                    long seconds = remainingTime % 60;
                    tooltip.add(Component.literal(String.format("剩余时间: %d分%d秒", minutes, seconds))
                        .withStyle(ChatFormatting.GRAY));
                }
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.contains(TAG_CREATION_TIME)) {
                tag.putLong(TAG_CREATION_TIME, System.currentTimeMillis());
            }

            long creationTime = tag.getLong(TAG_CREATION_TIME);
            long decayTime = DoomsdayConfig.EYE_DECAY_TIME_HOURS.get() * 3600000L; // 小时转毫秒
            if (System.currentTimeMillis() - creationTime >= decayTime) {
                ItemStack moldyEye = new ItemStack(ModItem.MOLDY_EYE.get());
                if (entity instanceof ServerPlayer player) {
                    player.getInventory().setItem(slotId, moldyEye);
                    player.displayClientMessage(Component.literal("§c...一颗眼球腐烂了..."), true);
                }
            }
        }
    }
} 