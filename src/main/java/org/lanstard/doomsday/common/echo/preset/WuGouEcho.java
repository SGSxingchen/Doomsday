package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.config.EchoConfig;

public class WuGouEcho extends Echo {
    private long lastUseTime = 0;
    private static final EchoPreset PRESET = EchoPreset.WUGU;

    public WuGouEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            EchoConfig.WUGU_SANITY_COST.get(),
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
        long currentTime = player.level().getGameTime();
        if (currentTime < lastUseTime + EchoConfig.WUGU_COOLDOWN_TICKS.get()) {
            long remainingTicks = lastUseTime + EchoConfig.WUGU_COOLDOWN_TICKS.get() - currentTime;
            long remainingSeconds = remainingTicks / 20;
            player.sendSystemMessage(Component.translatable("message.doomsday.wugu.cooldown", remainingSeconds));
            return false;
        }
        
        // 检查理智值是否足够
        int sanity = SanityManager.getSanity(player);
        if (sanity < EchoConfig.WUGU_SANITY_COST.get()) {
            player.sendSystemMessage(Component.translatable("message.doomsday.wugu.low_sanity"));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 获取玩家视线所指的目标
        var lookVec = player.getLookAngle();
        var start = player.getEyePosition();
        var end = start.add(lookVec.x * EchoConfig.WUGU_BASE_REACH.get(), lookVec.y * EchoConfig.WUGU_BASE_REACH.get(), lookVec.z * EchoConfig.WUGU_BASE_REACH.get());
        
        var box = player.getBoundingBox().expandTowards(lookVec.x * EchoConfig.WUGU_BASE_REACH.get(), lookVec.y * EchoConfig.WUGU_BASE_REACH.get(), lookVec.z * EchoConfig.WUGU_BASE_REACH.get()).inflate(EchoConfig.WUGU_TARGET_BOX_INFLATE.get());
        var targets = player.level().getEntitiesOfClass(ServerPlayer.class, box, 
            entity -> entity != player && entity.isPickable() && entity.distanceToSqr(start.x, start.y, start.z) <= EchoConfig.WUGU_BASE_REACH.get() * EchoConfig.WUGU_BASE_REACH.get());
        
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
            player.sendSystemMessage(Component.translatable("message.doomsday.wugu.no_target"));
            return;
        }
        
        if (mainTarget == player) {
            player.sendSystemMessage(Component.translatable("message.doomsday.wugu.self_target"));
            return;
        }
        
        // 获取玩家的信念值
        int faith = SanityManager.getFaith(player);
        
        // 消耗理智值
        SanityManager.modifySanity(player, -EchoConfig.WUGU_SANITY_COST.get());
        
        // 恢复目标玩家的理智值
        int healAmount = EchoConfig.WUGU_SANITY_HEAL.get();
        
        // 如果信念值大于等于中等信念要求，则恢复量翻倍
        if (faith >= EchoConfig.WUGU_MID_FAITH.get()) {
            healAmount *= EchoConfig.WUGU_HIGH_FAITH_MULTIPLIER.get().intValue();
            
            // 如果信念值足够高，则进行范围治疗
            final ServerPlayer target = mainTarget;
            var nearbyPlayers = player.level().getEntitiesOfClass(ServerPlayer.class, 
                target.getBoundingBox().inflate(EchoConfig.WUGU_HIGH_FAITH_RANGE.get()),
                entity -> entity != player && entity != target);
                
            for (var nearbyPlayer : nearbyPlayers) {
                int rangeHealAmount = (int)(healAmount * EchoConfig.WUGU_RANGE_HEAL_RATIO.get());
                SanityManager.modifySanity(nearbyPlayer, rangeHealAmount);
                nearbyPlayer.sendSystemMessage(Component.translatable("message.doomsday.wugu.range_heal"));
            }
        }
        
        SanityManager.modifySanity(mainTarget, healAmount);
        mainTarget.sendSystemMessage(Component.translatable("message.doomsday.wugu.healed"));
        
        // 更新最后使用时间
        lastUseTime = player.level().getGameTime();
        
        // 更新状态
        updateState(player);
        notifyEchoClocks(player);
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        player.sendSystemMessage(Component.translatable("message.doomsday.wugu.not_continuous"));
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