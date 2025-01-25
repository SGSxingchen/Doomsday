package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.common.entities.BombsEntity;
import net.minecraft.nbt.CompoundTag;

public class BaoShanEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.BAOSHAN;
    private static final int SANITY_COST = 10;               // 理智消耗
    private static final int COOL_DOWN = 8 * 20;                 // 8秒冷却
    private static final int FREE_COST_THRESHOLD = 300;      // 免费释放阈值
    private static final int MIN_BELIEF = 10;                // 最小信念要求
    
    private long cooldownEndTime = 0;

    public BaoShanEcho() {
        super(PRESET.name().toLowerCase(), PRESET.getDisplayName(), PRESET.getType(), PRESET.getActivationType(), SANITY_COST, 0);
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...爆闪之术已成..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 这是一个主动技能，不需要更新
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        if (System.currentTimeMillis() < cooldownEndTime) {
            long remainingSeconds = (cooldownEndTime - System.currentTimeMillis()) / 1000;
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...爆闪未息，需等待" + remainingSeconds + "秒..."));
            return false;
        }

        // 检查信念和理智，判断是否免费释放
        int currentSanity = SanityManager.getSanity(player);
        
        if (SanityManager.getFaith(player) >= MIN_BELIEF && currentSanity < FREE_COST_THRESHOLD) {
            return true;
        }
        
        // 检查理智是否足够
        if (currentSanity < SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以引动爆闪..."));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 检查是否免费释放
        int currentSanity = SanityManager.getSanity(player);
        boolean isFree = SanityManager.getFaith(player) >= MIN_BELIEF && currentSanity < FREE_COST_THRESHOLD;
        
        // 消耗理智
        if (!isFree) {
            SanityManager.modifySanity(player, -SANITY_COST);
        }

        // 创建并发射爆闪实体
        Level level = player.level();
        BombsEntity bombs = new BombsEntity(level, player);
        bombs.setPos(
            player.getX(),
            player.getEyeY() - 0.1,
            player.getZ()
        );
        
        // 设置投掷速度
        bombs.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
        level.addFreshEntity(bombs);

        // 设置冷却
        cooldownEndTime = System.currentTimeMillis() + COOL_DOWN * 50;
        updateState(player);
        
        // 发送消息
        if (isFree) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念引闪，爆闪飞出..."));
        } else {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...心随光动，爆闪涌出..."));
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...爆闪消散..."));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 这是一个主动技能，不需要切换持续状态
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...爆闪之力需主动引导..."));
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("cooldownEndTime", cooldownEndTime);
        return tag;
    }
    
    public static BaoShanEcho fromNBT(CompoundTag tag) {
        BaoShanEcho echo = new BaoShanEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.cooldownEndTime = tag.getLong("cooldownEndTime");
        return echo;
    }
} 