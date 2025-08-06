package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoManager;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import org.lanstard.doomsday.common.entities.QiheiSwordEntity;

import java.util.List;

public class BreakAllEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.BREAKALL;
    private static final int RANGE = 10;                       // 影响范围
    private static final int DISABLE_DURATION = 5 * 60 * 20;   // 5分钟的tick数
    private static final int COOLDOWN_DURATION = 30 * 60 * 20; // 30分钟的tick数
    private static final int ACTIVE_SANITY_COST = 200;        // 主动使用消耗200点理智
    private static final int FREE_COST_THRESHOLD = 300;       // 免费释放阈值
    private long lastUsedTime = 0;
    
    public BreakAllEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            ACTIVE_SANITY_COST
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...破万法之力在体内流转..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 不需要更新逻辑
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        // 不需要停用逻辑
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查是否被禁用
        if (this.isDisabled()) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...仙法暂失其效，难以施为..."));
            return false;
        }
        
        // 检查冷却时间
        long remainingCooldown = (lastUsedTime + COOLDOWN_DURATION * 50) - System.currentTimeMillis();
        if (remainingCooldown > 0) {
            int remainingSeconds = (int) (remainingCooldown / 1000);
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...破万法尚需积蓄，余下")
                .append(Component.literal(String.format("%d分%d秒", remainingSeconds / 60, remainingSeconds % 60)))
                .append(Component.literal("...")));
            return false;
        }
        
        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        
        // 如果信念大于等于10点且理智低于300，则可以免费释放
        boolean freeCost = faith >= 10 && currentSanity < FREE_COST_THRESHOLD;
        
        // 如果不是免费释放且理智不足，则无法使用
        if (!freeCost && currentSanity < ACTIVE_SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以施展仙法..."));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 获取范围内的所有玩家
        AABB box = player.getBoundingBox().inflate(RANGE);
        List<ServerPlayer> nearbyPlayers = player.level().getEntitiesOfClass(ServerPlayer.class, box);
        
        // 移除范围内的七黑剑
        List<QiheiSwordEntity> nearbySwords = player.level().getEntitiesOfClass(
            QiheiSwordEntity.class,
            box
        );
        
        boolean hasTargets = false;
        if (!nearbySwords.isEmpty()) {
            hasTargets = true;
            for (QiheiSwordEntity sword : nearbySwords) {
                sword.remove(Entity.RemovalReason.DISCARDED);
                // 生成粒子效果表示剑被破除
                if (player.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                        ParticleTypes.EXPLOSION,
                        sword.getX(),
                        sword.getY(),
                        sword.getZ(),
                        1, 0, 0, 0, 0
                    );
                }
            }
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...破万法摧毁了" + nearbySwords.size() + "把七黑剑..."));
        }
        
        for (ServerPlayer target : nearbyPlayers) {
            if (target == player) continue; // 不影响自己
            
            // 获取目标玩家的所有回响
            List<Echo> targetEchoes = EchoManager.getPlayerEchoes(target);
            boolean hasDisabledAny = false;
            
            // 使所有回响失效
            for (Echo echo : targetEchoes) {
                if (echo instanceof BreakAllEcho) continue; // 破万法不会被破万法影响
                // 先关闭回响
                echo.setActiveAndUpdate(target, false);
                // 再禁用回响
                echo.disable(DISABLE_DURATION);
                // 最后触发停用效果
                echo.onDeactivate(target);
                hasDisabledAny = true;
            }
            
            // 如果有回响被禁用，发送提示
            if (hasDisabledAny) {
                hasTargets = true;
                target.sendSystemMessage(Component.literal("§c[十日终焉] §f...你的仙法被破万法所破，暂时失效..."));
            }
        }
        
        if (!hasTargets) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...破万法无处可施..."));
            return;
        }
        
        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        
        // 如果信念大于等于10点且理智低于300，则不消耗理智
        boolean freeCost = faith >= 10 && currentSanity < FREE_COST_THRESHOLD;
        
        // 如果不是免费释放，消耗理智值
        if (!freeCost) {
            SanityManager.modifySanity(player, -ACTIVE_SANITY_COST);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + ACTIVE_SANITY_COST + "点心神，施展破万法..."));
        } else {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念引导，万法皆破..."));
        }
        
        // 记录使用时间
        lastUsedTime = System.currentTimeMillis();
        notifyEchoClocks(player);
        updateState(player);
        
        // 播放粒子效果
        spawnRingParticles(player);
        
        // 发送使用成功消息
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...破万法已成，诸法皆寂..."));
    }
    
    private void spawnRingParticles(ServerPlayer player) {
        Vec3 center = player.position();
        int particleCount = 36; // 每圈的粒子数
        float maxRadius = RANGE; // 最大半径
        
        // 在主线程中启动粒子生成
        new Thread(() -> {
            try {
                float angleStep = (float) (2 * Math.PI / particleCount);
                
                // 逐渐增加半径生成圆圈
                for (float radius = 0.5f; radius <= maxRadius; radius += 0.5f) {
                    // 逐个生成粒子画圈
                    for (float angle = 0; angle < 2 * Math.PI; angle += angleStep) {
                        double x = center.x + radius * Math.cos(angle);
                        double y = center.y + (radius * 0.1);
                        double z = center.z + radius * Math.sin(angle);
                        
                        // 在主线程中生成粒子
                        ServerPlayer finalPlayer = player;
                        player.getServer().execute(() -> {
                            if (finalPlayer.isAlive()) {
                                finalPlayer.serverLevel().sendParticles(
                                    ParticleTypes.END_ROD,
                                    x, y, z,
                                    1,
                                    0, 0, 0,
                                    0
                                );
                            }
                        });
                        
                        // 每个粒子生成后短暂延迟
                        Thread.sleep(5);
                    }
                    
                    // 每画完一圈后稍作延迟
                    Thread.sleep(20);
                }
            } catch (InterruptedException e) {
                // 忽略中断异常
            }
        }).start();
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {}

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("last_used_time", lastUsedTime);
        return tag;
    }
    
    public static BreakAllEcho fromNBT(CompoundTag tag) {
        BreakAllEcho echo = new BreakAllEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.lastUsedTime = tag.getLong("last_used_time");
        return echo;
    }
}