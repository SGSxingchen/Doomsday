package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import net.minecraft.nbt.CompoundTag;

public class YanPinEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.YANPIN;
    private static final int SANITY_COST = 200;              // 理智消耗
    private static final int COOL_DOWN = 12000;              // 10分钟冷却
    private static final int FREE_COST_THRESHOLD = 300;      // 免费释放阈值
    private static final int MIN_FAITH_REQUIREMENT = 10;     // 最低信念要求
    private static final int MAX_STACK_SIZE = 32;            // 最大堆叠数量
    private static final int HIGH_FAITH_STACK_SIZE = 64;    // 高信念时最大堆叠数量
    
    private long lastUseTime = 0;

    public YanPinEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            
            SANITY_COST,  // 主动技能消耗
            0            // 无被动消耗
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...我听到了赝品的回响..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 纯主动技能，无需更新
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...你的回响消散了..."));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 这是一个主动技能，不需要切换
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...赝品只能主动引导..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查冷却时间
        long currentTime = player.level().getGameTime();
        int cooldown = SanityManager.getFaith(player) >= 5 ? COOL_DOWN / 2 : COOL_DOWN;
        
        if (currentTime - lastUseTime < cooldown) {
            int remainingSeconds = (int)((cooldown - (currentTime - lastUseTime)) / 20);
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...赝品之力尚需" + remainingSeconds + "秒恢复..."));
            return false;
        }

        // 检查副手物品
        ItemStack offhandItem = player.getOffhandItem();
        if (offhandItem.isEmpty()) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...副手无物品可复制..."));
            return false;
        }

        // 检查物品是否可以复制
        if (!canDuplicate(offhandItem)) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...此物品无法复制..."));
            return false;
        }

        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = faith >= MIN_FAITH_REQUIREMENT && currentSanity < FREE_COST_THRESHOLD;

        // 如果不是免费释放且理智不足
        if (!freeCost && currentSanity < SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以引动仙法之力..."));
            return false;
        }

        return true;
    }

    private boolean canDuplicate(ItemStack item) {
        // 这里可以添加不允许复制的物品列表
        return !item.hasCustomHoverName() && // 不能复制改名物品
               !item.isEnchanted() &&        // 不能复制附魔物品
               item.getCount() <= MAX_STACK_SIZE; // 检查堆叠数量
    }

    @Override
    protected void doUse(ServerPlayer player) {
        ItemStack offhandItem = player.getOffhandItem();
        if (offhandItem.isEmpty() || !canDuplicate(offhandItem)) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...无法复制此物品..."));
            return;
        }

        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = faith >= MIN_FAITH_REQUIREMENT && currentSanity < FREE_COST_THRESHOLD;

        // 确定可复制的堆叠数量
        int maxStackSize = faith >= 5 ? HIGH_FAITH_STACK_SIZE : MAX_STACK_SIZE;
        if (offhandItem.getCount() > maxStackSize) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...物品数量过多，无法复制..."));
            return;
        }

        // 创建物品副本
        ItemStack copy = offhandItem.copy();
        if (!player.getInventory().add(copy)) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...背包已满，无法容纳赝品..."));
            return;
        }

        // 消耗理智
        if (!freeCost) {
            SanityManager.modifySanity(player, -SANITY_COST);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + SANITY_COST + "点心神，赝品已成..."));
        } else {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念引导，赝品已成..."));
        }

        lastUseTime = player.level().getGameTime();
        updateState(player);
        notifyEchoClocks(player);
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("lastUseTime", lastUseTime);
        return tag;
    }
    
    public static YanPinEcho fromNBT(CompoundTag tag) {
        YanPinEcho echo = new YanPinEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.lastUseTime = tag.getLong("lastUseTime");
        return echo;
    }
} 