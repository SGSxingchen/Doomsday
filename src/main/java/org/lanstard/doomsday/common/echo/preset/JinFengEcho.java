package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LivingEntity;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import net.minecraft.nbt.CompoundTag;
import org.joml.Vector3f;

import java.util.*;

public class JinFengEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.JINFENG;
    private static final int SANITY_COST = 100;               // 理智消耗
    private static final int COOLDOWN = 1200;                 // 1分钟冷却
    private static final int EFFECT_DURATION = 100;           // 5秒持续时间
    private static final int BASE_PUSH_RANGE = 10;           // 基础推动范围
    private static final int MID_PUSH_RANGE = 15;            // 中等信念推动范围
    private static final int HIGH_PUSH_RANGE = 20;           // 高等信念推动范围
    private static final float BASE_PUSH_STRENGTH = 1.0F;     // 基础推动力度
    private static final float MID_PUSH_STRENGTH = 1.5F;      // 中等信念推动力度
    private static final float HIGH_PUSH_STRENGTH = 2.0F;     // 高等信念推动力度
    private static final int FREE_COST_THRESHOLD = 300;       // 免费释放阈值
    private static final int MIN_BELIEF = 10;                 // 最小信念要求
    private static final int MID_BELIEF = 5;                  // 中等信念要求
    
    // 粒子效果相关
    private static final float WIND_RED = 0.9F;
    private static final float WIND_GREEN = 0.9F;
    private static final float WIND_BLUE = 1.0F;
    private static final float PARTICLE_SIZE = 0.5F;
    
    private long cooldownEndTime = 0;
    private Vec3 activePosition = null;              // 激活的领域中心位置
    private Vec3 pushDirection = null;               // 推动方向
    private int remainingDuration = 0;              // 剩余持续时间
    private final Random random = new Random();

    public JinFengEcho() {
        super(PRESET.name().toLowerCase(), PRESET.getDisplayName(), PRESET.getType(), SANITY_COST, 0);
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...劲风呼啸，听风而动..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        if (activePosition == null || remainingDuration <= 0) return;
        
        activePosition = player.position();
        ServerLevel level = (ServerLevel) player.level();
        
        // 根据信念等级确定范围和推力
        int faith = SanityManager.getFaith(player);
        int pushRange = faith >= MIN_BELIEF ? HIGH_PUSH_RANGE : 
                       (faith >= MID_BELIEF ? MID_PUSH_RANGE : BASE_PUSH_RANGE);
        float pushStrength = faith >= MIN_BELIEF ? HIGH_PUSH_STRENGTH : 
                           (faith >= MID_BELIEF ? MID_PUSH_STRENGTH : BASE_PUSH_STRENGTH);
        
        // 生成领域边缘粒子
        spawnDomainParticles(level, activePosition, pushRange);
        
        // 获取领域内的生物并推动
        AABB box = new AABB(
            activePosition.x - pushRange, activePosition.y - pushRange, activePosition.z - pushRange,
            activePosition.x + pushRange, activePosition.y + pushRange, activePosition.z + pushRange
        );
        
        List<LivingEntity> entities = level.getEntitiesOfClass(
            LivingEntity.class,
            box,
            entity -> entity != player
        );
        
        // 对领域内的生物应用推力
        for (LivingEntity entity : entities) {
            // 计算到中心的方向向量
            Vec3 toCenter = entity.position().subtract(activePosition);
            double distance = toCenter.length();
            
            if (distance > pushRange) {
                continue;
            }
            
            // 计算推力方向和强度
            double strengthMultiplier = 1.0 - (distance / pushRange); // 距离越近推力越大
            Vec3 pushVec = toCenter.normalize().scale(pushStrength * strengthMultiplier);
            
            // 增强水平方向的推力
            pushVec = new Vec3(pushVec.x * 1.5, pushVec.y, pushVec.z * 1.5);
            
            // 设置实体运动
            entity.setDeltaMovement(
                pushVec.x,
                entity.getDeltaMovement().y + pushVec.y * 0.5, // 减弱垂直方向的推力
                pushVec.z
            );
            
            // 如果实体在地面上，给一个向上的初速度使其离地
            if (entity.onGround()) {
                entity.setDeltaMovement(
                    entity.getDeltaMovement().x,
                    0.4,
                    entity.getDeltaMovement().z
                );
            }
            
            // 同步运动数据包
            if (!entity.level().isClientSide) {
                entity.level().broadcastEntityEvent(entity, (byte) 38);
                for (ServerPlayer players : ((ServerLevel)entity.level()).players()) {
                    players.connection.send(new ClientboundSetEntityMotionPacket(entity));
                }
            }
            
            // 生成推动效果的粒子
            spawnPushParticles(level, entity.position(), pushVec);
        }
        
        remainingDuration--;
        if (remainingDuration <= 0) {
            activePosition = null;
            pushDirection = null;
        }
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        if (System.currentTimeMillis() < cooldownEndTime) {
            long remainingSeconds = (cooldownEndTime - System.currentTimeMillis()) / 1000;
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...劲风未息，需等待" + remainingSeconds + "秒..."));
            return false;
        }

        // 检查信念和理智，判断是否免费释放
        int currentSanity = SanityManager.getSanity(player);
        
        if (SanityManager.getFaith(player) >= MIN_BELIEF && currentSanity < FREE_COST_THRESHOLD) {
            return true;
        }
        
        // 检查理智是否足够
        if (currentSanity < SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以引动劲风..."));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 检查是否免费释放
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean isFree = faith >= MIN_BELIEF && currentSanity < FREE_COST_THRESHOLD;
        
        // 消耗理智
        if (!isFree) {
            int actualCost = faith >= MID_BELIEF ? SANITY_COST / 2 : SANITY_COST;
            SanityManager.modifySanity(player, -actualCost);
        }

        // 设置领域中心和推动方向
        activePosition = player.position();
        pushDirection = player.getLookAngle();
        // 根据信念等级调整持续时间
        remainingDuration = faith >= MIN_BELIEF ? EFFECT_DURATION * 2 : 
                          (faith >= MID_BELIEF ? (int)(EFFECT_DURATION * 1.5) : EFFECT_DURATION);

        // 生成初始粒子效果
        if (player.level() instanceof ServerLevel serverLevel) {
            spawnInitialParticles(serverLevel, activePosition, pushDirection,faith >= MIN_BELIEF ? HIGH_PUSH_RANGE :
                    (faith >= MID_BELIEF ? MID_PUSH_RANGE : BASE_PUSH_RANGE));
        }

        // 设置冷却
        long cooldown = faith >= MID_BELIEF ? COOLDOWN / 2 : COOLDOWN;
        cooldownEndTime = System.currentTimeMillis() + cooldown * 50;
        
        // 发送消息
        String faithLevel = faith >= MIN_BELIEF ? "坚定" : (faith >= MID_BELIEF ? "稳固" : "微弱");
        if (isFree) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念引风，劲风(" + faithLevel + ")涌动..."));
        } else {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...心随风动，劲风(" + faithLevel + ")涌出..."));
        }
        notifyEchoClocks(player);
        updateState(player);
    }

    private void spawnDomainParticles(ServerLevel level, Vec3 center, int range) {
        DustParticleOptions windParticle = new DustParticleOptions(
            new Vector3f(WIND_RED, WIND_GREEN, WIND_BLUE),
            PARTICLE_SIZE
        );
        
        // 生成圆形领域边缘粒子
        for (int i = 0; i < 16; i++) {
            double angle = 2.0 * Math.PI * i / 16;
            double x = center.x + range * Math.cos(angle);
            double z = center.z + range * Math.sin(angle);
            
            level.sendParticles(windParticle,
                x, center.y + 0.1, z,
                1, 0, 0.1, 0, 0.05);
        }
    }

    private void spawnInitialParticles(ServerLevel level, Vec3 pos, Vec3 direction, int pushRange) {
        DustParticleOptions windParticle = new DustParticleOptions(
            new Vector3f(WIND_RED, WIND_GREEN, WIND_BLUE),
            PARTICLE_SIZE
        );
        
        // 在玩家前方生成锥形粒子效果
        double spread = Math.PI / 4; // 45度扩散角
        for (int i = 0; i < 50; i++) {
            double distance = 1 + random.nextDouble() * pushRange;
            double angle = random.nextDouble() * 2 * Math.PI;
            double pitch = random.nextDouble() * spread - spread/2;
            
            double dx = Math.cos(angle) * Math.cos(pitch);
            double dy = Math.sin(pitch);
            double dz = Math.sin(angle) * Math.cos(pitch);
            
            Vec3 particlePos = pos.add(
                direction.x * distance + dx,
                direction.y * distance + dy + 1,
                direction.z * distance + dz
            );
            
            level.sendParticles(windParticle,
                particlePos.x, particlePos.y, particlePos.z,
                1, 0, 0, 0, 0.1);
        }
    }

    private void spawnPushParticles(ServerLevel level, Vec3 pos, Vec3 direction) {
        DustParticleOptions windParticle = new DustParticleOptions(
            new Vector3f(WIND_RED, WIND_GREEN, WIND_BLUE),
            PARTICLE_SIZE * 0.5F
        );
        
        // 在推动路径上生成粒子
        for (int i = 0; i < 120; i++) {
            double offset = random.nextDouble() * 2 - 1;
            Vec3 particlePos = pos.add(
                direction.x + offset * 0.3,
                direction.y + offset * 0.3 + 1,
                direction.z + offset * 0.3
            );
            
            level.sendParticles(windParticle,
                particlePos.x, particlePos.y, particlePos.z,
                1, 0, 0, 0, 0.05);
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        activePosition = null;
        pushDirection = null;
        remainingDuration = 0;
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...劲风消散..."));
        updateState(player);
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 这是一个主动技能，不需要切换持续状态
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