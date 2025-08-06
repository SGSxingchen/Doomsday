package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import org.lanstard.doomsday.common.echo.BasicEcho;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.config.EchoConfig;

import java.util.List;

public class WoLiuEcho extends BasicEcho {
    private static final EchoPreset PRESET = EchoPreset.WOLIU;
    
    private long cooldownEndTime = 0;

    public WoLiuEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            EchoConfig.WOLIU_SANITY_COST.get(),
            0
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...涡流的回响在耳边响起..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 主动技能，不需要持续更新
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...涡流的力量渐渐消散..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查冷却
        if (System.currentTimeMillis() < cooldownEndTime) {
            long remainingSeconds = (cooldownEndTime - System.currentTimeMillis()) / 1000;
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f涡流尚需等待" + remainingSeconds + "秒..."));
            return false;
        }

        // 检查理智是否足够
        int currentSanity = SanityManager.getSanity(player);
        int sanityConsumption = EchoConfig.WOLIU_SANITY_COST.get();
        
        if (currentSanity < sanityConsumption) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f心神不足，难以引动涡流..."));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();
        int faith = SanityManager.getFaith(player);
        
        // 消耗理智
        int sanityConsumption = EchoConfig.WOLIU_SANITY_COST.get();
        SanityManager.modifySanity(player, -sanityConsumption);
        
        // 获取使用者位置
        Vec3 userPos = player.position();
        double baseRange = EchoConfig.WOLIU_RANGE.get();
        
        // 根据信念值和配置倍率调整范围
        double faithRangeMultiplier = EchoConfig.WOLIU_FAITH_RANGE_MULTIPLIER.get();
        double range = baseRange * (1.0 + faith * faithRangeMultiplier);
        
        // 创建检测范围
        AABB searchBox = AABB.ofSize(userPos, range * 2, range * 2, range * 2);
        
        // 找到范围内的所有玩家（除了使用者自己）
        List<ServerPlayer> nearbyPlayers = level.getEntitiesOfClass(ServerPlayer.class, searchBox);
        nearbyPlayers.removeIf(p -> p == player);
        
        if (nearbyPlayers.isEmpty()) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f范围内没有找到其他玩家..."));
            // 退还理智消耗
            SanityManager.modifySanity(player, sanityConsumption);
            return;
        }
        
        // 对每个玩家施加吸引力
        int affectedCount = 0;
        double baseAttractionStrength = EchoConfig.WOLIU_ATTRACTION_STRENGTH.get();
        
        // 根据信念值和配置倍率调整引力强度  
        double faithStrengthMultiplier = EchoConfig.WOLIU_FAITH_STRENGTH_MULTIPLIER.get();
        double attractionStrength = baseAttractionStrength * (1.0 + faith * faithStrengthMultiplier);
        
        for (ServerPlayer target : nearbyPlayers) {
            Vec3 targetPos = target.position();
            double distance = userPos.distanceTo(targetPos);
            
            // 跳过距离过近的玩家（避免过度吸引）
            if (distance < 2.0) {
                continue;
            }
            
            // 计算吸引向量（从目标指向使用者）
            Vec3 attractionDirection = userPos.subtract(targetPos).normalize();
            
            // 根据距离调整吸引力强度（距离越远，需要越强的初始速度）
            double distanceMultiplier = Math.min(distance / 5.0, 2.0); // 最大2倍强度
            Vec3 attractionVelocity = attractionDirection.scale(attractionStrength * distanceMultiplier);
            
            // 应用动量
            target.setDeltaMovement(attractionVelocity);
            // 关键：标记需要同步给客户端
            target.hurtMarked = true;
            
            // 给被吸引的玩家发送消息
            target.sendSystemMessage(Component.literal("§e[十日终焉] §f你被涡流的力量吸引..."));
            
            // 在目标玩家位置生成粒子效果
            spawnVortexParticles(level, targetPos, userPos);
            
            affectedCount++;
        }
        
        // 设置冷却时间
        int baseCooldown = EchoConfig.WOLIU_COOLDOWN_TICKS.get();
        int faithRequirement = EchoConfig.WOLIU_FAITH_REQUIREMENT.get();
        int cooldown = faith >= faithRequirement ? baseCooldown / 2 : baseCooldown;
        cooldownEndTime = System.currentTimeMillis() + (cooldown * 50); // tick转毫秒
        
        // 发送反馈消息
        String faithLevel = faith >= faithRequirement ? "强化" : "基础";
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f涡流吸引了" + affectedCount + "名玩家 (" + faithLevel + "效果)"));
        
        // 在使用者周围生成涡流中心粒子
        spawnCenterVortexParticles(level, userPos);
        
        updateState(player);
        notifyEchoClocks(player);
    }

    private void spawnVortexParticles(ServerLevel level, Vec3 targetPos, Vec3 centerPos) {
        // 生成从目标向中心移动的螺旋粒子效果
        for (int i = 0; i < 15; i++) {
            double t = i / 15.0;
            Vec3 direction = centerPos.subtract(targetPos);
            Vec3 particlePos = targetPos.add(direction.scale(t));
            
            // 添加螺旋效果
            double angle = t * Math.PI * 4; // 4圈螺旋
            double spiralRadius = 0.5 * (1 - t); // 螺旋半径递减
            Vec3 perpendicular = new Vec3(-direction.z, 0, direction.x).normalize();
            Vec3 spiralOffset = perpendicular.scale(spiralRadius * Math.cos(angle))
                    .add(new Vec3(0, spiralRadius * Math.sin(angle), 0));
            
            particlePos = particlePos.add(spiralOffset);
            
            level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.PORTAL,
                particlePos.x, particlePos.y + 1.0, particlePos.z,
                1, 0, 0, 0, 0.1
            );
        }
    }
    
    private void spawnCenterVortexParticles(ServerLevel level, Vec3 centerPos) {
        // 在涡流中心生成环形粒子效果
        for (int i = 0; i < 20; i++) {
            double angle = (i / 20.0) * Math.PI * 2;
            double radius = 1.5;
            double x = centerPos.x + radius * Math.cos(angle);
            double z = centerPos.z + radius * Math.sin(angle);
            double y = centerPos.y + 0.2;
            
            level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.WITCH,
                x, y, z,
                1, 0, 0.1, 0, 0.05
            );
        }
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f涡流只能主动释放..."));
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("cooldownEndTime", cooldownEndTime);
        return tag;
    }
    
    public static WoLiuEcho fromNBT(CompoundTag tag) {
        WoLiuEcho echo = new WoLiuEcho();
        echo.cooldownEndTime = tag.getLong("cooldownEndTime");
        return echo;
    }
}