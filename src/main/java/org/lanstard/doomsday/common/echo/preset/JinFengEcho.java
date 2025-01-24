package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;

import java.util.List;

public class JinFengEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.JINFENG;
    private static final int SANITY_COST = 200;              // 理智消耗
    private static final int COOLDOWN = 1200;                // 1分钟冷却
    private static final int FREE_COST_THRESHOLD = 300;      // 免费释放阈值
    private static final int MIN_FAITH_REQUIREMENT = 10;     // 最低信念要求
    private static final int EFFECT_DURATION = 100;          // 效果持续时间（5秒）
    private static final double PUSH_RANGE = 10.0;           // 推动范围
    private static final double PUSH_STRENGTH = 3.0;         // 推动力度
    
    private long cooldownEndTime = 0;

    public JinFengEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            SANITY_COST,  // 主动技能消耗
            0            // 无被动消耗
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...我听到了劲风的回响..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 纯主动技能，无需更新
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...你的回响消散了..."));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 这是一个主动技能，不需要切换
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...劲风只能主动引导..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查冷却时间
        if (System.currentTimeMillis() < cooldownEndTime) {
            long remainingSeconds = (cooldownEndTime - System.currentTimeMillis()) / 1000;
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...劲风之力尚未恢复，剩余" + remainingSeconds + "秒..."));
            return false;
        }

        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = faith >= MIN_FAITH_REQUIREMENT && currentSanity < FREE_COST_THRESHOLD;

        // 如果不是免费释放且理智不足
        if (!freeCost && currentSanity < SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以引动劲风之力..."));
            return false;
        }

        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = faith >= MIN_FAITH_REQUIREMENT && currentSanity < FREE_COST_THRESHOLD;

        // 获取玩家位置和朝向
        Vec3 pos = player.position();
        Vec3 lookVec = player.getLookAngle();
        
        // 计算检测范围
        AABB box = new AABB(
            pos.x - PUSH_RANGE, pos.y - PUSH_RANGE, pos.z - PUSH_RANGE,
            pos.x + PUSH_RANGE, pos.y + PUSH_RANGE, pos.z + PUSH_RANGE
        );
        
        // 获取范围内的所有生物
        List<LivingEntity> entities = player.level().getEntitiesOfClass(
            LivingEntity.class,
            box,
            entity -> entity != player && entity.isAlive()
        );
        
        // 对每个生物应用推力
        for (LivingEntity entity : entities) {
            // 计算与玩家的方向向量
            Vec3 directionVec = lookVec.normalize();
            
            // 设置实体的运动方向和速度
            entity.setDeltaMovement(
                directionVec.x * PUSH_STRENGTH,
                0.5,  // 轻微向上推动
                directionVec.z * PUSH_STRENGTH
            );
            
            // 防止摔落伤害
            entity.fallDistance = 0;
        }
        
        // 生成粒子效果
        if (player.level() instanceof ServerLevel serverLevel) {
            double particleSpread = 2.0;
            // 对每个被推动的实体生成粒子效果
            for (LivingEntity entity : entities) {
                Vec3 entityPos = entity.position();
                Vec3 particleStartPos = pos.add(lookVec.scale(2)); // 从玩家前方2格开始
                Vec3 particleDirection = entityPos.subtract(particleStartPos).normalize();
                
                // 为每个实体生成一条粒子路径
                for (int i = 0; i < 20; i++) {
                    // 随机偏移起始位置
                    double offsetX = player.getRandom().nextGaussian() * particleSpread;
                    double offsetY = player.getRandom().nextGaussian() * particleSpread;
                    double offsetZ = player.getRandom().nextGaussian() * particleSpread;
                    
                    // 计算粒子速度（朝向目标）
                    double speedX = particleDirection.x * 0.5;
                    double speedY = particleDirection.y * 0.5;
                    double speedZ = particleDirection.z * 0.5;
                    
                    serverLevel.sendParticles(
                        ParticleTypes.DOLPHIN,  // 使用海豚粒子（青色）
                        particleStartPos.x + offsetX,
                        particleStartPos.y + offsetY,
                        particleStartPos.z + offsetZ,
                        0,  // 粒子数量（使用速度控制）
                        speedX,
                        speedY,
                        speedZ,
                        0.5  // 粒子速度
                    );
                }
            }
            
            // 如果没有目标，也生成一些向前的粒子
            if (entities.isEmpty()) {
                Vec3 particleStartPos = pos.add(lookVec.scale(2));
                for (int i = 0; i < 30; i++) {
                    double offsetX = player.getRandom().nextGaussian() * particleSpread;
                    double offsetY = player.getRandom().nextGaussian() * particleSpread;
                    double offsetZ = player.getRandom().nextGaussian() * particleSpread;
                    
                    serverLevel.sendParticles(
                        ParticleTypes.DOLPHIN,
                        particleStartPos.x + offsetX,
                        particleStartPos.y + offsetY,
                        particleStartPos.z + offsetZ,
                        0,
                        lookVec.x * 0.5,
                        lookVec.y * 0.5,
                        lookVec.z * 0.5,
                        0.5
                    );
                }
            }
        }

        // 消耗理智
        if (!freeCost) {
            SanityManager.modifySanity(player, -SANITY_COST);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + SANITY_COST + "点心神，劲风已起..."));
        } else {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念引导，劲风已起..."));
        }

        // 设置冷却
        cooldownEndTime = System.currentTimeMillis() + (COOLDOWN * 50);
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("cooldownEndTime", cooldownEndTime);
        return tag;
    }
    
    public static JinFengEcho fromNBT(CompoundTag tag) {
        JinFengEcho echo = new JinFengEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.cooldownEndTime = tag.getLong("cooldownEndTime");
        return echo;
    }
} 