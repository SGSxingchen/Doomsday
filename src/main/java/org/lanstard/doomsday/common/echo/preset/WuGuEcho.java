package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoType;
import org.lanstard.doomsday.common.echo.ActivationType;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.common.echo.EchoPreset;

public class WuGuEcho extends Echo {
    private static final int SANITY_COST = 100;
    private static final int SANITY_HEAL = 200;
    private static final int MIN_FAITH = 10;
    private static final int FREE_SANITY_THRESHOLD = 300;
    private static final int COOLDOWN_TICKS = 1200; // 1分钟 = 60 * 20 ticks
    private long cooldownEndTime = 0;
    private static final EchoPreset PRESET = EchoPreset.WUGU;

    public WuGuEcho() {
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
        // 检查冷却时间
        // if (System.currentTimeMillis() < cooldownEndTime) {
        //     player.sendSystemMessage(Component.literal("§b[十日终焉] §f...此法尚需时日方可再施展..."));
        //     return false;
        // }
        long timeMs = cooldownEndTime - System.currentTimeMillis();
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
        double reach = 32.0D;
        var hitResult = player.pick(reach, 1.0F, false);
        var lookVec = player.getLookAngle();
        var start = player.getEyePosition();
        var end = start.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        
        var box = player.getBoundingBox().expandTowards(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach).inflate(1.0D);
        var targets = player.level().getEntitiesOfClass(ServerPlayer.class, box, 
            entity -> entity != player && entity.isPickable() && entity.distanceToSqr(start.x, start.y, start.z) <= reach * reach);
        
        ServerPlayer target = null;
        double minDist = Double.MAX_VALUE;
        
        for (var potentialTarget : targets) {
            var hitBox = potentialTarget.getBoundingBox();
            var intersection = hitBox.clip(start, end).orElse(null);
            
            if (intersection != null) {
                double dist = start.distanceToSqr(intersection);
                if (dist < minDist) {
                    minDist = dist;
                    target = potentialTarget;
                }
            }
        }
        
        if (target == null) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...未找到合适的目标..."));
            return;
        }
        
        if (target == player) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...无法对自己使用无垢回响..."));
            return;
        }
        
        // 只有在不满足免费释放条件时才消耗理智
        if (!freeCast) {
            SanityManager.modifySanity(player, -SANITY_COST);
        }

        // 恢复目标的理智值
        SanityManager.modifySanity(target, SANITY_HEAL);
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...无垢之力涤荡心神，为 ")
            .append(target.getDisplayName())
            .append(Component.literal(" 恢复了 "))
            .append(Component.literal(String.valueOf(SANITY_HEAL)))
            .append(Component.literal(" 点理智")));
        
        target.sendSystemMessage(Component.literal("§b[十日终焉] §f...")
            .append(player.getDisplayName())
            .append(Component.literal(" 的无垢之力为你恢复了 "))
            .append(Component.literal(String.valueOf(SANITY_HEAL)))
            .append(Component.literal(" 点理智")));

        // 设置冷却时间
        cooldownEndTime = System.currentTimeMillis() + (COOLDOWN_TICKS * 50); // 50ms per tick
        updateState(player);
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...此法不可持续施展..."));
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("cooldownEndTime", cooldownEndTime);
        return tag;
    }

    public static WuGuEcho fromNBT(CompoundTag tag) {
        WuGuEcho echo = new WuGuEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.cooldownEndTime = tag.getLong("cooldownEndTime");
        return echo;
    }
} 