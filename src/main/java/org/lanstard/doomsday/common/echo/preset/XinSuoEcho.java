package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import org.lanstard.doomsday.common.echo.BasicEcho;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.effects.ModEffects;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.common.data.HeartMarkData;
import org.lanstard.doomsday.config.EchoConfig;

import java.util.List;
import java.util.Optional;

public class XinSuoEcho extends BasicEcho {
    private long lastUseTime = 0;
    private static final EchoPreset PRESET = EchoPreset.XINSUO;

    public XinSuoEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            EchoConfig.XINSUO_SANITY_COST.get(),
            0
        );
    }

    @Override
    public boolean doCanUse(ServerPlayer player) {
        long currentTime = player.level().getGameTime();
        
        // 检查冷却时间
        if (currentTime < lastUseTime + EchoConfig.XINSUO_COOLDOWN_TICKS.get()) {
            long remainingTicks = lastUseTime + EchoConfig.XINSUO_COOLDOWN_TICKS.get() - currentTime;
            long remainingSeconds = remainingTicks / 20;
            player.sendSystemMessage(Component.translatable("message.doomsday.xinsuo.cooldown", remainingSeconds));
            return false;
        }
        
        // 检查理智值
        int sanity = SanityManager.getSanity(player);
        if (sanity < EchoConfig.XINSUO_SANITY_COST.get()) {
            player.sendSystemMessage(Component.translatable("message.doomsday.xinsuo.low_sanity"));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 获取目标玩家
        ServerPlayer target = getTargetPlayer(player);
        if (target == null) {
            player.sendSystemMessage(Component.translatable("message.doomsday.xinsuo.no_target"));
            return;
        }
        
        if (target == player) {
            player.sendSystemMessage(Component.translatable("message.doomsday.xinsuo.self_target"));
            return;
        }
        
        // 消耗理智值
        SanityManager.modifySanity(player, -EchoConfig.XINSUO_SANITY_COST.get());
        
        // 创建心之印效果实例
        MobEffectInstance heartMarkEffect = new MobEffectInstance(
            ModEffects.HEART_MARK.get(),
            EchoConfig.XINSUO_EFFECT_DURATION_TICKS.get(), // 5分钟 = 6000 ticks
            0, // 等级 0
            false, // 不是环境效果
            true, // 显示粒子
            true  // 显示图标
        );
        
        // 为目标添加心之印效果
        target.addEffect(heartMarkEffect);
        
        // 在HeartMarkData中记录施法者信息
        if (player.level() instanceof ServerLevel serverLevel) {
            HeartMarkData data = HeartMarkData.get(serverLevel);
            data.markPlayer(target.getUUID(), player.getUUID());
        }
        
        // 发送消息
        player.sendSystemMessage(Component.translatable("message.doomsday.xinsuo.cast_success")
            .append(target.getDisplayName()));
        target.sendSystemMessage(Component.translatable("message.doomsday.xinsuo.marked_by")
            .append(player.getDisplayName()));
        
        // 更新最后使用时间
        lastUseTime = player.level().getGameTime();
        
        // 更新状态和通知回响钟
        updateState(player);
        notifyEchoClocks(player);
    }
    
    private ServerPlayer getTargetPlayer(ServerPlayer player) {
        // 获取玩家视线方向的射线
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        Vec3 endPos = eyePosition.add(lookVector.scale(EchoConfig.XINSUO_BASE_REACH.get()));
        
        // 创建搜索范围
        AABB searchBox = new AABB(eyePosition, endPos).inflate(EchoConfig.XINSUO_TARGET_BOX_INFLATE.get());
        
        // 搜索范围内的玩家
        List<ServerPlayer> nearbyPlayers = player.level().getEntitiesOfClass(ServerPlayer.class, searchBox, 
            target -> target != player && target.isPickable() && target.isAlive());
        
        // 找到视线最近的目标
        ServerPlayer closestTarget = null;
        double minDistance = Double.MAX_VALUE;
        
        for (ServerPlayer potentialTarget : nearbyPlayers) {
            AABB hitBox = potentialTarget.getBoundingBox();
            Optional<Vec3> intersection = hitBox.clip(eyePosition, endPos);
            
            if (intersection.isPresent()) {
                double distance = eyePosition.distanceToSqr(intersection.get());
                if (distance < minDistance) {
                    minDistance = distance;
                    closestTarget = potentialTarget;
                }
            }
        }
        
        return closestTarget;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("lastUseTime", lastUseTime);
        return tag;
    }

    public static XinSuoEcho fromNBT(CompoundTag tag) {
        XinSuoEcho echo = new XinSuoEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.lastUseTime = tag.getLong("lastUseTime");
        return echo;
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        player.sendSystemMessage(Component.translatable("message.doomsday.xinsuo.not_continuous"));
    }
}