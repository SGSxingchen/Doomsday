package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import org.lanstard.doomsday.common.echo.BasicEcho;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoManager;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.config.EchoConfig;

import java.util.List;
import java.util.Optional;

public class LunXiEcho extends BasicEcho {
    private long lastUseTime = 0;
    private static final EchoPreset PRESET = EchoPreset.LUNXI;

    public LunXiEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            EchoConfig.LUNXI_SANITY_COST.get(),
            0
        );
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        long currentTime = player.level().getGameTime();
        
        // 检查冷却时间
        if (currentTime < lastUseTime + EchoConfig.LUNXI_COOLDOWN_TICKS.get()) {
            long remainingTicks = lastUseTime + EchoConfig.LUNXI_COOLDOWN_TICKS.get() - currentTime;
            long remainingSeconds = remainingTicks / 20;
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f轮息回响冷却中，剩余 " + remainingSeconds + " 秒"));
            return false;
        }
        
        // 检查理智值
        int sanity = SanityManager.getSanity(player);
        if (sanity < EchoConfig.LUNXI_SANITY_COST.get()) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f理智不足，无法使用轮息回响"));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 获取目标玩家
        ServerPlayer target = getTargetPlayer(player);
        if (target == null) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f没有找到有效目标"));
            return;
        }
        
        if (target == player) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f不能对自己使用"));
            return;
        }
        
        // 消耗理智值
        SanityManager.modifySanity(player, -EchoConfig.LUNXI_SANITY_COST.get());
        
        // 重置目标的所有回响冷却时间
        List<Echo> targetEchoes = EchoManager.getPlayerEchoes(target);
        for (Echo echo : targetEchoes) {
            // 1. 清除禁用状态（通用的冷却机制）
            echo.enable();
            
            // 2. 使用反射统一重置所有可能的冷却时间字段
            resetEchoCooldownFields(echo);
        }
        
        // 发送消息
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f成功为 " + target.getDisplayName().getString() + " 重置了所有回响的冷却时间"));
        target.sendSystemMessage(Component.literal("§b[十日终焉] §f" + player.getDisplayName().getString() + " 为你重置了所有回响的冷却时间"));
        
        // 更新最后使用时间
        lastUseTime = player.level().getGameTime();
        
        // 更新状态和通知回响钟
        updateState(player);
        notifyEchoClocks(player);
    }
    
    private void resetEchoCooldownFields(Echo echo) {
        try {
            Class<?> clazz = echo.getClass();
            
            // 遍历所有字段，寻找可能的冷却时间相关字段
            java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName().toLowerCase();
                
                // 重置所有与冷却时间相关的字段
                if (fieldName.contains("cooldown") || fieldName.contains("lastuse") || fieldName.contains("time")) {
                    if (field.getType() == long.class) {
                        field.setLong(echo, 0L);
                    } else if (field.getType() == int.class) {
                        field.setInt(echo, 0);
                    }
                }
            }
        } catch (Exception e) {
            // 如果反射失败，至少enable()已经清除了基础的禁用状态
        }
    }
    
    private ServerPlayer getTargetPlayer(ServerPlayer player) {
        // 获取玩家视线方向的射线
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        Vec3 endPos = eyePosition.add(lookVector.scale(EchoConfig.LUNXI_BASE_REACH.get()));
        
        // 创建搜索范围
        AABB searchBox = new AABB(eyePosition, endPos).inflate(EchoConfig.LUNXI_TARGET_BOX_INFLATE.get());
        
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

    public static LunXiEcho fromNBT(CompoundTag tag) {
        LunXiEcho echo = new LunXiEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.lastUseTime = tag.getLong("lastUseTime");
        return echo;
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f轮息回响已激活"));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 轮息是主动回响，不需要持续更新
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        // 轮息是主动回响，不需要停用处理
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f轮息回响不是持续性回响"));
    }
}