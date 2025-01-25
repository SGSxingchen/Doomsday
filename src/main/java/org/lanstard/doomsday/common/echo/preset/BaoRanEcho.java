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

import java.util.List;
import java.util.Random;

public class BaoRanEcho extends Echo {
    private static final int SANITY_COST = 20;
    private static final int MIN_FAITH = 10;
    private static final int FREE_COST_THRESHOLD = 300;
    private static final int COOLDOWN = 3 * 60 * 20; // 3分钟 = 20tick/s * 60s = 1200tick
    private static final Random random = new Random();
    
    private long lastUseTime = 0;

    public BaoRanEcho() {
        super("baoran", "爆燃", EchoType.ACTIVE, ActivationType.TRIGGER, SANITY_COST);
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
        // 主动技能，不支持持续施展
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...爆燃之法不支持持续施展..."));
    }

    @Override
    public boolean doCanUse(ServerPlayer player) {
        // 检查冷却时间
        long currentTime = player.level().getGameTime();
        if (currentTime - lastUseTime < COOLDOWN) {
            int remainingSeconds = (int)((COOLDOWN - (currentTime - lastUseTime)) / 20);
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...爆燃之法尚需" + remainingSeconds + "秒冷却..."));
            return false;
        }

        // 检查背包是否有可转换的物品
        if (!hasConvertibleItems(player)) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...背包中没有可转化的物品..."));
            return false;
        }

        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);

        if (faith >= MIN_FAITH && currentSanity <= FREE_COST_THRESHOLD) {
            return true;
        }

        // 检查理智值是否足够
        if (currentSanity < SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不宁，难以施展爆燃之法..."));
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
        boolean freeCost = currentSanity < FREE_COST_THRESHOLD && faith >= MIN_FAITH;

        // 如果不是免费释放，消耗理智值
        if (!freeCost) {
            SanityManager.modifySanity(player, -SANITY_COST);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗了" + SANITY_COST + "点心神之力..."));
        }

        // 随机转换一个物品为爆燃弹
        convertRandomItem(player);
        
        // 更新状态
        updateState(player);
    }

    private boolean hasConvertibleItems(ServerPlayer player) {
        List<ItemStack> items = player.getInventory().items;
        if (items.isEmpty()) {
            return false;
        }
        boolean flag = false;
        for (ItemStack item : items) {
            if (!item.isEmpty()) {
                if (item.getItem() != ModItem.FIRE_BOMB.get()) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    private void convertRandomItem(ServerPlayer player) {
        List<ItemStack> items = player.getInventory().items;
        List<Integer> validSlots = new java.util.ArrayList<>();
        if (items.isEmpty()) {
            return;
        }

        // 找出所有除了爆然弹的非空的物品槽
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).isEmpty()) {
                if(items.get(i).getItem() == ModItem.FIRE_BOMB.get()) continue;
                validSlots.add(i);
            }
        }

        if (!validSlots.isEmpty()) {
            // 随机选择一个物品槽
            int slot = validSlots.get(random.nextInt(validSlots.size()));
            ItemStack oldStack = items.get(slot);

            
            // 记录原物品名称
            String oldItemName = oldStack.getHoverName().getString();

            // 转换为爆燃弹
            ItemStack newStack = new ItemStack(ModItem.FIRE_BOMB.get());
            if (oldStack.getCount() > 1) {
                oldStack.shrink(1);
                player.getInventory().add(newStack);
            } else {
                items.set(slot, newStack);
            }

            // 发送转换提示
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f..." + oldItemName + "在爆燃之力下化为了爆燃弹..."));
        }
    }
} 