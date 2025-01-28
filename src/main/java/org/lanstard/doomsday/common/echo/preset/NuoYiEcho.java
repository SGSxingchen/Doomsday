package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import net.minecraft.nbt.CompoundTag;

import java.util.Random;

public class NuoYiEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.NUOYI;
    private static final int GIVE_SANITY_COST = 20;          // 给予物品消耗
    private static final int TAKE_SANITY_COST = 40;          // 获取物品消耗
    private static final int COOL_DOWN = 30 * 20;            // 30秒冷却
    private static final int FREE_COST_THRESHOLD = 300;      // 免费释放阈值
    private static final int MIN_FAITH_REQUIREMENT = 10;     // 最低信念要求
    
    private long cooldownEndTime = 0;
    private final Random random = new Random();

    public NuoYiEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            GIVE_SANITY_COST,  // 默认消耗
            0                  // 无被动消耗
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...挪移之力涌动，虚实交错..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 纯主动技能，无需更新
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...挪移之力消散..."));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 这是一个主动技能，不需要切换
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查冷却时间
        // if (System.currentTimeMillis() < cooldownEndTime) {
        //     long remainingSeconds = (cooldownEndTime - System.currentTimeMillis()) / 1000;
        //     player.sendSystemMessage(Component.literal("§c[十日终焉] §f...挪移之力，剩余" + remainingSeconds + "秒..."));
        //     return false;
        // }
        long timeMs = cooldownEndTime - System.currentTimeMillis();
        if (timeMs > 0) {
            long remainingSeconds = timeMs / 20 / 50;
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...挪移之力尚需" + remainingSeconds + "秒恢复..."));
            return false;
        }


        // 获取目标玩家
        var target = player.level().getNearestPlayer(player, 10);
        if (target == null) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...未找到目标..."));
            return false;
        }

        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = faith >= MIN_FAITH_REQUIREMENT && currentSanity < FREE_COST_THRESHOLD;
        int cost = player.isShiftKeyDown() ? TAKE_SANITY_COST : GIVE_SANITY_COST;

        // 如果不是免费释放且理智不足
        if (!freeCost && currentSanity < cost) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以引动挪移之力..."));
            return false;
        }

        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        var target = player.level().getNearestPlayer(player, 10);
        if (target == player) return;
        if (target == null) return;  // 安全检查

        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = faith >= MIN_FAITH_REQUIREMENT && currentSanity < FREE_COST_THRESHOLD;

        if (player.isShiftKeyDown()) {
            // 获取目标随机物品
            ItemStack targetItem = getRandomItemFromInventory((ServerPlayer)target);
            if (targetItem.isEmpty()) {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...目标身上无可获取之物..."));
                return;
            }

            // 设置物品到副手
            ItemStack oldOffhand = player.getOffhandItem();
            if (!oldOffhand.isEmpty()) {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...副手已有物品，无法获取..."));
                return;
            }

            // 转移物品
            player.setItemInHand(InteractionHand.OFF_HAND, targetItem);
            target.sendSystemMessage(Component.literal("§c[十日终焉] §f...？？？..."));
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...成功获取目标物品..."));

            // 消耗理智
            if (!freeCost) {
                int actualCost = faith >= MIN_FAITH_REQUIREMENT ? TAKE_SANITY_COST / 2 : TAKE_SANITY_COST;
                SanityManager.modifySanity(player, -actualCost);
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + actualCost + "点心神..."));
            }
        } else {
            // 转移副手物品
            ItemStack offhandItem = player.getOffhandItem();
            if (offhandItem.isEmpty()) {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...副手无物品可转移..."));
                return;
            }

            // 尝试添加到目标背包
            if (!((ServerPlayer)target).getInventory().add(offhandItem)) {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...目标背包已满..."));
                return;
            }

            // 清空副手
            player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            target.sendSystemMessage(Component.literal("§b[十日终焉] §f...？？？..."));
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...物品已转移至目标背包..."));

            // 消耗理智
            if (!freeCost) {
                int actualCost = faith >= MIN_FAITH_REQUIREMENT ? GIVE_SANITY_COST / 2 : GIVE_SANITY_COST;
                SanityManager.modifySanity(player, -actualCost);
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + actualCost + "点心神..."));
            }
        }

        // 设置冷却
        long cooldown = faith >= MIN_FAITH_REQUIREMENT ? COOL_DOWN / 2 : COOL_DOWN;
        cooldownEndTime = System.currentTimeMillis() + (cooldown * 50);
        updateState(player);
        notifyEchoClocks(player);
    }

    private ItemStack getRandomItemFromInventory(ServerPlayer player) {
        var inventory = player.getInventory();
        var nonEmptySlots = new java.util.ArrayList<Integer>();
        
        // 收集所有非空格子
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (!inventory.getItem(i).isEmpty()) {
                nonEmptySlots.add(i);
            }
        }
        
        if (nonEmptySlots.isEmpty()) {
            return ItemStack.EMPTY;
        }
        
        // 随机选择一个格子
        int slot = nonEmptySlots.get(random.nextInt(nonEmptySlots.size()));
        ItemStack item = inventory.getItem(slot).copy();
        inventory.setItem(slot, ItemStack.EMPTY);
        return item;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("cooldownEndTime", cooldownEndTime);
        return tag;
    }
    
    public static NuoYiEcho fromNBT(CompoundTag tag) {
        NuoYiEcho echo = new NuoYiEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.cooldownEndTime = tag.getLong("cooldownEndTime");
        return echo;
    }
} 