package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.common.echo.EchoPreset;

public class WuGouEcho extends Echo {
    private static final int SANITY_COST = 100;
    private static final int SANITY_HEAL = 200;
    private static final int MIN_FAITH = 10;
    private static final int MID_FAITH = 5;  // 添加中等信念要求
    private static final int FREE_SANITY_THRESHOLD = 300;
    private static final int COOL_DOWN_TICKS = 1200; // 1分钟 = 60 * 20 ticks
    private static final double BASE_REACH = 32.0D;
    private static final double HIGH_FAITH_RANGE = 3.0D; // 高信念时的范围治疗半径
    private long lastUseTime = 0;
    private static final EchoPreset PRESET = EchoPreset.WUGU;

    public WuGouEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            SANITY_COST,
            0
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        // 激活时不需要特殊处理
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 更新时不需要特殊处理
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        // 停用时不需要特殊处理
    }

    @Override
    public boolean doCanUse(ServerPlayer player) {
        long timeMs = lastUseTime - System.currentTimeMillis();
        if (timeMs > 0) {
            long remainingSeconds = timeMs / 20 / 50;
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...无垢之力尚需" + remainingSeconds + "秒恢复..."));
            return false;
        }
        
        // 检查信仰和理智
        int faith = SanityManager.getFaith(player);
        int sanity = SanityManager.getSanity(player);
        
        // 当信仰大于等于10且理智小于300时，不消耗理智
        if (faith >= MIN_FAITH && sanity < FREE_SANITY_THRESHOLD) {
            return true;
        }
        
        // 其他情况需要消耗理智
        if (sanity < SANITY_COST) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...理智不足，无法使用无垢回响"));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 检查是否需要消耗理智值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCast = faith >= MIN_FAITH && currentSanity < FREE_SANITY_THRESHOLD;
        
        // 获取玩家视线所指的目标
        var lookVec = player.getLookAngle();
        var start = player.getEyePosition();
        var end = start.add(lookVec.x * BASE_REACH, lookVec.y * BASE_REACH, lookVec.z * BASE_REACH);
        
        var box = player.getBoundingBox().expandTowards(lookVec.x * BASE_REACH, lookVec.y * BASE_REACH, lookVec.z * BASE_REACH).inflate(1.0D);
        var targets = player.level().getEntitiesOfClass(ServerPlayer.class, box, 
            entity -> entity != player && entity.isPickable() && entity.distanceToSqr(start.x, start.y, start.z) <= BASE_REACH * BASE_REACH);
        
        ServerPlayer mainTarget = null;
        double minDist = Double.MAX_VALUE;
        
        for (var potentialTarget : targets) {
            var hitBox = potentialTarget.getBoundingBox();
            var intersection = hitBox.clip(start, end).orElse(null);
            
            if (intersection != null) {
                double dist = start.distanceToSqr(intersection);
                if (dist < minDist) {
                    minDist = dist;
                    mainTarget = potentialTarget;
                }
            }
        }
        
        if (mainTarget == null) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...未找到合适的目标..."));
            return;
        }
        
        if (mainTarget == player) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...无法对自己使用无垢回响..."));
            return;
        }

        // 计算治疗量
        int healAmount = SANITY_HEAL;
        if (faith >= MID_FAITH) {
            healAmount = (int)(SANITY_HEAL * 1.5); // 信念≥5时提升50%治疗量
        }
        
        // 只有在不满足免费释放条件时才消耗理智
        if (!freeCast) {
            SanityManager.modifySanity(player, -SANITY_COST);
        }

        // 恢复主目标的理智值
        final ServerPlayer finalMainTarget = mainTarget; // 创建final变量
        SanityManager.modifySanity(finalMainTarget, healAmount);
        
        // 信念≥5时，对主目标周围的玩家进行范围治疗
        if (faith >= MID_FAITH) {
            var nearbyPlayers = finalMainTarget.level().getEntitiesOfClass(ServerPlayer.class, 
                finalMainTarget.getBoundingBox().inflate(HIGH_FAITH_RANGE),
                entity -> entity != finalMainTarget && entity != player);
                
            for (ServerPlayer nearbyPlayer : nearbyPlayers) {
                SanityManager.modifySanity(nearbyPlayer, healAmount / 2); // 范围治疗效果为主目标的50%
                nearbyPlayer.sendSystemMessage(Component.literal("§b[十日终焉] §f...")
                    .append(player.getDisplayName())
                    .append(Component.literal(" 的无垢之力余波为你恢复了 "))
                    .append(Component.literal(String.valueOf(healAmount / 2)))
                    .append(Component.literal(" 点理智")));
            }
            
            // 发送范围治疗效果的消息
            if (!nearbyPlayers.isEmpty()) {
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...无垢之力涤荡心神，为 ")
                    .append(finalMainTarget.getDisplayName())
                    .append(Component.literal(" 恢复了 "))
                    .append(Component.literal(String.valueOf(healAmount)))
                    .append(Component.literal(" 点理智，并对周围 "))
                    .append(Component.literal(String.valueOf(nearbyPlayers.size())))
                    .append(Component.literal(" 名玩家产生了治疗效果")));
            } else {
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...无垢之力涤荡心神，为 ")
                    .append(finalMainTarget.getDisplayName())
                    .append(Component.literal(" 恢复了 "))
                    .append(Component.literal(String.valueOf(healAmount)))
                    .append(Component.literal(" 点理智")));
            }
        } else {
            // 原始效果的消息
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...无垢之力涤荡心神，为 ")
                .append(finalMainTarget.getDisplayName())
                .append(Component.literal(" 恢复了 "))
                .append(Component.literal(String.valueOf(healAmount)))
                .append(Component.literal(" 点理智")));
        }
        
        finalMainTarget.sendSystemMessage(Component.literal("§b[十日终焉] §f...")
            .append(player.getDisplayName())
            .append(Component.literal(" 的无垢之力为你恢复了 "))
            .append(Component.literal(String.valueOf(healAmount)))
            .append(Component.literal(" 点理智")));

        // 设置冷却时间
        lastUseTime = player.level().getGameTime();
        updateState(player);
        notifyEchoClocks(player);
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...此法不可持续施展..."));
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("lastUseTime", lastUseTime);
        return tag;
    }

    public static WuGouEcho fromNBT(CompoundTag tag) {
        WuGouEcho echo = new WuGouEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.lastUseTime = tag.getLong("lastUseTime");
        return echo;
    }
} 