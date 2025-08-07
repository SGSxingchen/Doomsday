package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.common.entities.BombsEntity;
import org.lanstard.doomsday.config.EchoConfig;
import net.minecraft.nbt.CompoundTag;

public class BaoShanEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.BAOSHAN;
    
    private long cooldownEndTime = 0;

    public BaoShanEcho() {
        super(PRESET.name().toLowerCase(), PRESET.getDisplayName(), PRESET.getType(), EchoConfig.BAOSHAN_SANITY_COST.get(), 0);
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.translatable("message.doomsday.baoshan.activate"));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 这是一个主动技能，不需要更新
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        if (System.currentTimeMillis() < cooldownEndTime) {
            long remainingSeconds = (cooldownEndTime - System.currentTimeMillis()) / 1000;
            player.sendSystemMessage(Component.translatable("message.doomsday.baoshan.cooldown", remainingSeconds));
            return false;
        }

        // 检查信念和理智，判断是否免费释放
        int currentSanity = SanityManager.getSanity(player);
        
        if (SanityManager.getFaith(player) >= EchoConfig.BAOSHAN_MIN_FAITH.get() && currentSanity < EchoConfig.BAOSHAN_FREE_COST_THRESHOLD.get()) {
            return true;
        }
        
        // 检查理智是否足够
        if (currentSanity < EchoConfig.BAOSHAN_SANITY_COST.get()) {
            player.sendSystemMessage(Component.translatable("message.doomsday.baoshan.low_sanity"));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 检查是否免费释放
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean isFree = faith >= EchoConfig.BAOSHAN_MIN_FAITH.get() && currentSanity < EchoConfig.BAOSHAN_FREE_COST_THRESHOLD.get();
        
        // 根据信念等级调整消耗
        int actualCost = EchoConfig.BAOSHAN_SANITY_COST.get();
        if (faith >= EchoConfig.BAOSHAN_MID_FAITH.get()) {
            actualCost = (int)(EchoConfig.BAOSHAN_SANITY_COST.get() * EchoConfig.BAOSHAN_SANITY_COST_REDUCTION_RATIO.get());
        }
        
        // 消耗理智
        if (!isFree) {
            SanityManager.modifySanity(player, -actualCost);
        }

        // 创建并发射爆闪实体
        Level level = player.level();
        BombsEntity bombs = new BombsEntity(level, player);
        bombs.setPos(
            player.getX(),
            player.getEyeY() - 0.1,
            player.getZ()
        );
        
        // 根据信念等级设置爆闪特性
        if (faith >= EchoConfig.BAOSHAN_MIN_FAITH.get()) {
            bombs.setEnhanced(EchoConfig.BAOSHAN_HIGH_ENHANCE_LEVEL.get());
        } else if (faith >= EchoConfig.BAOSHAN_MID_FAITH.get()) {
            bombs.setEnhanced(EchoConfig.BAOSHAN_MID_ENHANCE_LEVEL.get());
        } else {
            bombs.setEnhanced(EchoConfig.BAOSHAN_BASE_ENHANCE_LEVEL.get());
        }
        
        // 根据信念等级设置投掷速度
        float speed;
        if (faith >= EchoConfig.BAOSHAN_MIN_FAITH.get()) {
            speed = EchoConfig.BAOSHAN_HIGH_SPEED.get().floatValue();
        } else if (faith >= EchoConfig.BAOSHAN_MID_FAITH.get()) {
            speed = EchoConfig.BAOSHAN_MID_SPEED.get().floatValue();
        } else {
            speed = EchoConfig.BAOSHAN_BASE_SPEED.get().floatValue();
        }
        
        // 设置投掷速度和精确度
        float inaccuracy = faith >= EchoConfig.BAOSHAN_MID_FAITH.get() ? 
            EchoConfig.BAOSHAN_MID_INACCURACY.get().floatValue() : 
            EchoConfig.BAOSHAN_BASE_INACCURACY.get().floatValue();
        bombs.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, speed, inaccuracy);
        level.addFreshEntity(bombs);

        // 设置冷却
        cooldownEndTime = System.currentTimeMillis() + EchoConfig.BAOSHAN_COOLDOWN_TICKS.get() * EchoConfig.BAOSHAN_COOLDOWN_MILLISECONDS_PER_TICK.get();
        notifyEchoClocks(player);
        updateState(player);
        
        // 发送消息
        String faithLevelKey;
        if (faith >= EchoConfig.BAOSHAN_MIN_FAITH.get()) {
            faithLevelKey = "message.doomsday.baoshan.faith_level.firm";
        } else if (faith >= EchoConfig.BAOSHAN_MID_FAITH.get()) {
            faithLevelKey = "message.doomsday.baoshan.faith_level.stable";
        } else {
            faithLevelKey = "message.doomsday.baoshan.faith_level.weak";
        }
        
        Component faithLevelText = Component.translatable(faithLevelKey);
        if (isFree) {
            player.sendSystemMessage(Component.translatable("message.doomsday.baoshan.use_free", faithLevelText));
        } else {
            player.sendSystemMessage(Component.translatable("message.doomsday.baoshan.use_cost", faithLevelText));
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.translatable("message.doomsday.baoshan.deactivate"));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 这是一个主动技能，不需要切换持续状态
        player.sendSystemMessage(Component.translatable("message.doomsday.baoshan.not_continuous"));
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