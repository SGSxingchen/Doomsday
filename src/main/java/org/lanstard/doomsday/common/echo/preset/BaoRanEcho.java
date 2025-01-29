package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoType;
import org.lanstard.doomsday.common.echo.ActivationType;
import org.lanstard.doomsday.common.items.ModItem;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.config.EchoConfig;

import java.util.List;
import java.util.Random;

public class BaoRanEcho extends Echo {
    private static final Random random = new Random();
    private long lastUseTime = 0;

    public BaoRanEcho() {
        super("baoran", "爆燃", EchoType.ACTIVE, ActivationType.TRIGGER, EchoConfig.BAORAN_SANITY_COST.get());
    }

    @Override
    public boolean doCanUse(ServerPlayer player) {
        // 检查冷却时间
        long currentTime = player.level().getGameTime();
        if (currentTime - lastUseTime < EchoConfig.BAORAN_COOLDOWN_TICKS.get()) {
            int remainingSeconds = (int)((EchoConfig.BAORAN_COOLDOWN_TICKS.get() - (currentTime - lastUseTime)) / 20);
            player.sendSystemMessage(Component.translatable("message.doomsday.baoran.cooldown", remainingSeconds));
            return false;
        }

        // 检查背包是否有可转换的物品
        if (!hasConvertibleItems(player)) {
            player.sendSystemMessage(Component.translatable("message.doomsday.baoran.no_items"));
            return false;
        }

        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);

        if (faith >= EchoConfig.BAORAN_MIN_FAITH.get() && currentSanity <= EchoConfig.BAORAN_FREE_COST_THRESHOLD.get()) {
            return true;
        }

        // 检查理智值是否足够
        if (currentSanity < EchoConfig.BAORAN_SANITY_COST.get()) {
            player.sendSystemMessage(Component.translatable("message.doomsday.baoran.low_sanity"));
            return false;
        }

        return true;
    }

    @Override
    public void doUse(ServerPlayer player) {
        // 更新最后使用时间
        lastUseTime = player.level().getGameTime();

        // 检查是否需要消耗理智值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = currentSanity < EchoConfig.BAORAN_FREE_COST_THRESHOLD.get() && faith >= EchoConfig.BAORAN_MIN_FAITH.get();

        // 根据信念等级调整消耗
        int actualCost = EchoConfig.BAORAN_SANITY_COST.get();
        if (faith >= EchoConfig.BAORAN_MID_FAITH.get()) {
            actualCost = EchoConfig.BAORAN_SANITY_COST.get() / 2;
        }

        // 如果不是免费释放，消耗理智值
        if (!freeCost) {
            SanityManager.modifySanity(player, -actualCost);
            player.sendSystemMessage(Component.translatable("message.doomsday.baoran.cost", actualCost));
        }

        // 根据信念等级决定转换数量
        int convertCount = EchoConfig.BAORAN_BASE_CONVERT_COUNT.get();
        int maxConvertCount = convertCount;
        if (faith >= EchoConfig.BAORAN_MIN_FAITH.get()) {
            convertCount = EchoConfig.BAORAN_HIGH_FAITH_CONVERT_COUNT.get();
            maxConvertCount = convertCount;
        } else if (faith >= EchoConfig.BAORAN_MID_FAITH.get()) {
            convertCount = EchoConfig.BAORAN_MID_FAITH_CONVERT_COUNT.get();
            maxConvertCount = convertCount;
        }

        // 转换物品
        int converted = 0;
        for (int i = 0; i < convertCount; i++) {
            if (hasConvertibleItems(player)) {
                convertRandomItem(player, faith);
                converted++;
            }
        }

        // 根据信念等级发送不同的消息
        if (converted > 0) {
            String faithLevel = faith >= EchoConfig.BAORAN_MIN_FAITH.get() ? "high" : 
                              (faith >= EchoConfig.BAORAN_MID_FAITH.get() ? "mid" : "base");
            player.sendSystemMessage(Component.translatable("message.doomsday.baoran.convert." + faithLevel, 
                converted, maxConvertCount));
        }
        
        // 更新状态
        notifyEchoClocks(player);
        updateState(player);
    }

    private boolean hasConvertibleItems(ServerPlayer player) {
        List<ItemStack> items = player.getInventory().items;
        if (items.isEmpty()) {
            return false;
        }
        boolean flag = false;
        for (ItemStack item : items) {
            if (!item.isEmpty() && item.getItem() != ModItem.FIRE_BOMB.get()) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private void convertRandomItem(ServerPlayer player, int faith) {
        List<ItemStack> items = player.getInventory().items;
        List<Integer> validSlots = new java.util.ArrayList<>();

        // 找出所有除了爆然弹的非空的物品槽
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).isEmpty() && items.get(i).getItem() != ModItem.FIRE_BOMB.get()) {
                validSlots.add(i);
            }
        }

        if (!validSlots.isEmpty()) {
            // 随机选择一个物品槽
            int slot = validSlots.get(random.nextInt(validSlots.size()));
            ItemStack oldStack = items.get(slot);
            
            // 记录原物品名称
            String oldItemName = oldStack.getHoverName().getString();

            // 转换为爆燃弹并根据信念等级设置强化等级
            ItemStack newStack = new ItemStack(ModItem.FIRE_BOMB.get());
            int enhanceLevel;
            String enhanceLevelKey;
            
            if (faith >= EchoConfig.BAORAN_MIN_FAITH.get()) {
                enhanceLevel = EchoConfig.BAORAN_HIGH_ENHANCE_LEVEL.get();
                enhanceLevelKey = "high";
            } else if (faith >= EchoConfig.BAORAN_MID_FAITH.get()) {
                enhanceLevel = EchoConfig.BAORAN_MID_ENHANCE_LEVEL.get();
                enhanceLevelKey = "mid";
            } else {
                enhanceLevel = EchoConfig.BAORAN_BASE_ENHANCE_LEVEL.get();
                enhanceLevelKey = "base";
            }

            if (enhanceLevel > 0) {
                if (newStack.getTag() == null) {
                    newStack.setTag(new CompoundTag());
                }
                newStack.getTag().putInt("EnhancedLevel", enhanceLevel);
            }

            if (oldStack.getCount() > 1) {
                oldStack.shrink(1);
                player.getInventory().add(newStack);
            } else {
                items.set(slot, newStack);
            }

            // 发送转换提示
            player.sendSystemMessage(Component.translatable("message.doomsday.baoran.convert_item." + enhanceLevelKey, 
                oldItemName));
        }
    }

    @Override
    public void onActivate(ServerPlayer player) {
        // 主动技能，不需要激活逻辑
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 主动技能，不需要更新逻辑
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        // 主动技能，不需要停用逻辑
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        player.sendSystemMessage(Component.translatable("message.doomsday.baoran.not_continuous"));
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("lastUseTime", lastUseTime);
        return tag;
    }

    public static BaoRanEcho fromNBT(CompoundTag tag) {
        BaoRanEcho echo = new BaoRanEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.lastUseTime = tag.getLong("lastUseTime");
        return echo;
    }
} 