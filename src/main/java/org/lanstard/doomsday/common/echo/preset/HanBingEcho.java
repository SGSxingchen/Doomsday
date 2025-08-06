package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.common.entities.IceBlockEntity;

public class HanBingEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.HANBING;
    private static final int SANITY_COST = 10;               // 理智消耗
    private static final int COOL_DOWN = 8 * 20;             // 8秒冷却
    private static final int MID_BELIEF = 5;                 // 中等信念要求
    private static final int MIN_BELIEF = 10;                // 最小信念要求
    private static final int FREE_COST_THRESHOLD = 300;      // 免费释放阈值
    
    private long cooldownEndTime = 0;

    public HanBingEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            SANITY_COST,
            0
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...寒冰之力在体内流转..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 不需要更新逻辑
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...寒冰之力消散..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        long timeMs = cooldownEndTime - System.currentTimeMillis();
        if (timeMs > 0) {
            long remainingSeconds = timeMs / 20 / 50;
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...寒冰之力尚需" + remainingSeconds + "秒恢复..."));
            return false;
        }

        int currentSanity = SanityManager.getSanity(player);
        int beliefLevel = SanityManager.getBeliefLevel(player);

        if (beliefLevel >= MIN_BELIEF && currentSanity <= FREE_COST_THRESHOLD) {
            return true;
        }

        // 检查理智值是否足够
        if (currentSanity < SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...你的理智不足以释放寒冰之力..."));
            return false;
        }

        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 消耗理智值（如果当前理智值低于阈值则免费释放）
        int currentSanity = SanityManager.getSanity(player);
        int currentBelief = SanityManager.getBeliefLevel(player);
        boolean freeCost = currentSanity <= FREE_COST_THRESHOLD && currentBelief >= MIN_BELIEF;
        
        // 根据信念等级调整消耗
        int actualCost = SANITY_COST;
        if (currentBelief >= MID_BELIEF) {
            actualCost = SANITY_COST / 2;  // 信念≥5时消耗减半
        }
        
        if (!freeCost) {
            SanityManager.modifySanity(player, -actualCost);
        }

        // 发射3个寒冰实体（扇形分散）
        Level level = player.level();
        float[] yawOffsets = {-15.0F, 0.0F, 15.0F}; // 扇形分散角度
        
        for (int i = 0; i < 3; i++) {
            IceBlockEntity iceBlock = new IceBlockEntity(level, player);
            
            // 根据信念等级设置冰块特性
            if (currentBelief >= MID_BELIEF) {
                iceBlock.setEnhanced(true);  // 设置为增强状态
                // 增加发射速度和精确度，带有扇形偏移
                iceBlock.shootFromRotation(player, player.getXRot(), player.getYRot() + yawOffsets[i], 0.0F, 2.0F, 0.3F);
            } else {
                iceBlock.setEnhanced(false);
                // 普通状态下也采用扇形发射，但精确度稍低
                iceBlock.shootFromRotation(player, player.getXRot(), player.getYRot() + yawOffsets[i], 0.0F, 1.5F, 0.7F);
            }
            
            level.addFreshEntity(iceBlock);
        }

        // 设置冷却时间
        int baseCoolDown = COOL_DOWN;
        if (currentBelief >= MID_BELIEF) {
            baseCoolDown = (int)(COOL_DOWN * 0.75);  // 信念≥5时冷却时间减少25%
        }
        cooldownEndTime = System.currentTimeMillis() + baseCoolDown * 50;
        notifyEchoClocks(player);
        updateState(player);

        // 发送使用提示
        String beliefLevel = currentBelief >= MIN_BELIEF ? "坚定" : (currentBelief >= MID_BELIEF ? "稳固" : "微弱");
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...寒冰(" + beliefLevel + ")之力爆发，三道冰霜齐射而出..."));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 这是一个主动技能，不需要切换状态
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...寒冰之力只能主动释放..."));
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("cooldownEndTime", cooldownEndTime);
        return tag;
    }
    
    public static HanBingEcho fromNBT(CompoundTag tag) {
        HanBingEcho echo = new HanBingEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.cooldownEndTime = tag.getLong("cooldownEndTime");
        return echo;
    }
} 