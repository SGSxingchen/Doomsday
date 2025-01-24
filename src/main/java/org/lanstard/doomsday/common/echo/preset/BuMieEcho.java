package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoType;
import org.lanstard.doomsday.common.echo.ActivationType;
import org.lanstard.doomsday.common.sanity.SanityManager;

public class BuMieEcho extends Echo {
    private static final int SANITY_COST = 500;
    private static final int MIN_FAITH = 10;
    private static final int FREE_COST_THRESHOLD = 300;
    private static final int DURATION = 6000; // 5分钟 = 20tick/s * 60s * 5
    private static final int COOLDOWN = 36000; // 30分钟 = 20tick/s * 60s * 30
    private static final float END_DAMAGE = 19.0f;
    
    private long lastUseTime = 0;
    private long immortalEndTime = 0;
    private int tickCounter = 0;

    public BuMieEcho() {
        super("bumie", "不灭", EchoType.ACTIVE, ActivationType.TRIGGER, SANITY_COST);
    }

    @Override
    public void onActivate(ServerPlayer player) {
        // 主动技能，不需要激活逻辑
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        if (isImmortal(player)) {
            tickCounter++;
            
            // 每秒检查一次
            if (tickCounter >= 20) {
                tickCounter = 0;
                // 检查是否到达结束时间
                if (player.level().getGameTime() >= immortalEndTime) {
                    // 结束不灭状态并造成伤害
                    player.hurt(player.damageSources().magic(), END_DAMAGE);
                    player.sendSystemMessage(Component.literal("§c[十日终焉] §f...不灭之法已然消散，反噬之力涌现..."));
                    immortalEndTime = 0;
                    updateState(player);
                }
            }
            // 保持生命值不低于1
            if (player.getHealth() < 1.0f) {
                player.setHealth(1.0f);
            }
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        // 主动技能，不需要停用逻辑
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 主动技能，不支持持续施展
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...不灭之法不支持持续施展..."));
    }

    @Override
    public boolean doCanUse(ServerPlayer player) {
        // 检查是否已经处于不灭状态
        if (isImmortal(player)) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...不灭之法正在生效中..."));
            return false;
        }
        // 检查冷却时间
        long currentTime = player.level().getGameTime();
        if (currentTime - lastUseTime < COOLDOWN) {
            int remainingMinutes = (int)((COOLDOWN - (currentTime - lastUseTime)) / 1200);
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...不灭之法尚需" + remainingMinutes + "分钟冷却..."));
            return false;
        }

        return true;
    }

    @Override
    public void doUse(ServerPlayer player) {
        // 检查信仰值和理智值，决定是否免费释放
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = currentSanity < FREE_COST_THRESHOLD || faith >= MIN_FAITH;

        // 如果不是免费释放，检查理智值是否足够
        if (!freeCost && currentSanity < SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不宁，难以施展不灭之法..."));
            return;
        }

        // 更新最后使用时间和不灭状态结束时间
        lastUseTime = player.level().getGameTime();
        immortalEndTime = lastUseTime + DURATION;

        // 如果不是免费释放，消耗理智值
        if (!freeCost) {
            SanityManager.modifySanity(player, -SANITY_COST);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗了" + SANITY_COST + "点心神之力..."));
        }

        // 发送激活消息
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...不灭之法已然生效，五分钟内将不会死亡..."));
        
        // 更新状态
        updateState(player);
    }

    public boolean isImmortal(ServerPlayer player) {
        return immortalEndTime > 0 && player.level().getGameTime() < immortalEndTime;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("lastUseTime", lastUseTime);
        tag.putLong("immortalEndTime", immortalEndTime);
        tag.putInt("tickCounter", tickCounter);
        return tag;
    }

    public static BuMieEcho fromNBT(CompoundTag tag) {
        BuMieEcho echo = new BuMieEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.lastUseTime = tag.getLong("lastUseTime");
        echo.immortalEndTime = tag.getLong("immortalEndTime");
        echo.tickCounter = tag.getInt("tickCounter");
        return echo;
    }
} 