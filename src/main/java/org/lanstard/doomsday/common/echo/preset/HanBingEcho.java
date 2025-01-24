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
    private static final int SANITY_COST = 20;               // 理智消耗
    private static final int COOLDOWN = 400;                 // 20秒冷却
    private static final int FREE_COST_THRESHOLD = 300;      // 免费释放阈值
    private static final int MIN_BELIEF = 10;                // 最小信念要求
    
    private long cooldownEndTime = 0;

    public HanBingEcho() {
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

        if (beliefLevel >= MIN_BELIEF && currentSanity >= FREE_COST_THRESHOLD) {
            return true;
        }

        // 检查理智值是否足够
        if (currentSanity < SANITY_COST || (currentSanity < FREE_COST_THRESHOLD && beliefLevel < MIN_BELIEF)) {
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
        if (currentSanity >= FREE_COST_THRESHOLD && currentBelief >= MIN_BELIEF) {
            SanityManager.modifySanity(player, -SANITY_COST);
        }

        // 发射寒冰实体
        Level level = player.level();
        IceBlockEntity iceBlock = new IceBlockEntity(level, player);
        iceBlock.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
        level.addFreshEntity(iceBlock);

        // 设置冷却时间
        cooldownEndTime = System.currentTimeMillis() + COOLDOWN * 50; // 转换为毫秒
        updateState(player);

        // 发送使用提示
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...寒冰之力爆发，冻结周围的一切..."));
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