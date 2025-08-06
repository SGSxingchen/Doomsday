package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import net.minecraft.nbt.CompoundTag;
import org.joml.Vector3f;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.entity.projectile.ProjectileUtil;

import java.util.List;
import java.util.Random;

public class HuoShuiEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.HUOSHUI;
    private static final int BASE_RANGE = 10;                 // 基础影响范围
    private static final int MID_RANGE = 15;                 // 中等信念影响范围
    private static final int BASE_FIRE_COUNT = 20;           // 基础火焰数量
    private static final int MID_FIRE_COUNT = 30;            // 中等信念火焰数量
    private static final int BASE_FIRE_DURATION = 100;       // 基础持续时间(5秒)
    private static final int MID_FIRE_DURATION = 150;        // 中等信念持续时间(7.5秒)
    private static final int BASE_SANITY_REDUCTION = 50;     // 基础理智降低量
    private static final int MID_SANITY_REDUCTION = 75;      // 中等信念理智降低量
    private static final int NORMAL_COOLDOWN = 72000;        // 60分钟冷却
    private static final int LOW_SANITY_COOLDOWN = 24000;    // 20分钟冷却
    private static final int LOW_SANITY_THRESHOLD = 200;     // 低理智阈值
    private static final long NIGHT_TIME = 18000;            // 夜晚时间点
    private static final long DAY_TIME = 6000;               // 白天时间点
    private static final int MID_BELIEF = 5;                 // 中等信念要求
    private static final int MIN_BELIEF = 10;                // 最小信念要求
    private static final int FREE_COST_THRESHOLD = 300;      // 免费释放阈值
    
    // 粒子效果相关
    private static final float BASE_RED = 0.8F;
    private static final float BASE_GREEN = 0.2F;
    private static final float BASE_BLUE = 0.2F;
    private static final float MID_RED = 1.0F;
    private static final float MID_GREEN = 0.3F;
    private static final float MID_BLUE = 0.3F;
    private static final float PARTICLE_SIZE = 1.0F;
    
    private static final int SANITY_COST = 0;
    
    private long lastDayTime = 0;
    private long cooldownEndTime = 0;
    private int fireGenerationDuration = 0;                   // 火焰生成剩余时间
    private final Random random = new Random();

    public HuoShuiEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            
            0,  // 无主动技能消耗
            0   // 无被动消耗
        );
        setActive(true); // 默认激活
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...祸水之力涌动，灾厄将至..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        if (!isActive()) return;

        // 应用永久抗火效果
        int faith = SanityManager.getFaith(player);
        if (faith >= MID_BELIEF) {
            player.setRemainingFireTicks(0);
            // 在火中获得生命恢复
            if (faith >= MIN_BELIEF && player.isOnFire()) {
                player.heal(0.1F);  // 每tick恢复0.1生命值
            }
        }

        // 获取当前时间
        long currentDayTime = player.level().getDayTime() % 24000;
        
        // 检查是否是夜晚时分（18000）或白天时分（6000）
        boolean isNightTime = Math.abs(currentDayTime - NIGHT_TIME) < 100;
        boolean isDayTime = Math.abs(currentDayTime - DAY_TIME) < 100;
        
        // 只有当lastDayTime不在同一时间段时才触发效果
        if ((isNightTime || isDayTime) && !isInSameTimePhase(lastDayTime, currentDayTime)) {
            notifyEchoClocks(player);
            if (isNightTime) {
                // 开始火焰生成周期
                fireGenerationDuration = faith >= MID_BELIEF ? MID_FIRE_DURATION : BASE_FIRE_DURATION;
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...祸水之力涌动，火焰四起..."));
            } else {
                // 白天效果：降低理智
                reduceSanityForNearbyPlayers(player);
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...祸水之力涌动，心神动荡..."));
            }
            // 生成粒子效果
            if (player.level() instanceof ServerLevel serverLevel) {
                spawnEffectParticles(serverLevel, player.position(), faith);
            }
            // 更新上次触发时间
            lastDayTime = currentDayTime;
            updateState(player);
        }
        
        // 处理持续火焰生成
        if (fireGenerationDuration > 0) {
            spawnRandomFires(player);
            fireGenerationDuration--;
        }
    }

    // 检查两个时间点是否在同一个时间相位（白天或夜晚）
    private boolean isInSameTimePhase(long time1, long time2) {
        // 检查是否都在夜晚时分（18000）附近
        boolean bothNight = Math.abs(time1 - 18000) < 100 && Math.abs(time2 - 18000) < 100;
        // 检查是否都在白天时分（6000）附近
        boolean bothDay = Math.abs(time1 - 6000) < 100 && Math.abs(time2 - 6000) < 100;
        return bothNight || bothDay;
    }

    // 生成随机火焰
    private void spawnRandomFires(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();
        int faith = SanityManager.getFaith(player);
        
        // 根据信念等级确定范围和数量
        int range = faith >= MID_BELIEF ? MID_RANGE : BASE_RANGE;
        int fireCount = faith >= MID_BELIEF ? MID_FIRE_COUNT : BASE_FIRE_COUNT;
        
        for (int i = 0; i < fireCount; i++) {
            int x = player.getBlockX() + random.nextInt(range * 2) - range;
            int z = player.getBlockZ() + random.nextInt(range * 2) - range;
            for(int j = -5; j < 5; j++){
                int y = player.getBlockY() + j;
                BlockPos pos = new BlockPos(x, y, z);
                if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isSolidRender(level, pos)) {
                    level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                    break;
                }
            }
        }
        
        // 生成粒子效果
        spawnEffectParticles(level, player.position(), faith);
    }

    // 降低附近玩家的理智
    private void reduceSanityForNearbyPlayers(ServerPlayer player) {
        int faith = SanityManager.getFaith(player);
        int range = faith >= MID_BELIEF ? MID_RANGE : BASE_RANGE;
        int reduction = faith >= MID_BELIEF ? MID_SANITY_REDUCTION : BASE_SANITY_REDUCTION;
        
        AABB box = player.getBoundingBox().inflate(range);
        List<ServerPlayer> nearbyPlayers = player.level().getEntitiesOfClass(
            ServerPlayer.class,
            box,
            p -> p != player
        );
        
        for (ServerPlayer target : nearbyPlayers) {
            SanityManager.modifySanity(target, -reduction);
            target.sendSystemMessage(Component.literal("§c[十日终焉] §f...祸水之力侵蚀，心神受损..."));
            
            // 信念≥5时添加缓慢效果
            if (faith >= MID_BELIEF) {
                target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 40, 0));
            }
        }
        
        // 生成粒子效果
        if (player.level() instanceof ServerLevel serverLevel) {
            spawnEffectParticles(serverLevel, player.position(), faith);
        }
    }

    private void spawnEffectParticles(ServerLevel level, Vec3 pos, int faith) {
        // 根据信念等级选择粒子颜色
        float red = faith >= MID_BELIEF ? MID_RED : BASE_RED;
        float green = faith >= MID_BELIEF ? MID_GREEN : BASE_GREEN;
        float blue = faith >= MID_BELIEF ? MID_BLUE : BASE_BLUE;
        
        DustParticleOptions darkParticle = new DustParticleOptions(
            new Vector3f(red, green, blue),
            PARTICLE_SIZE
        );
        
        int range = faith >= MID_BELIEF ? MID_RANGE : BASE_RANGE;
        int particleCount = faith >= MID_BELIEF ? 48 : 36;
        
        // 生成环形粒子效果
        for (int i = 0; i < particleCount; i++) {
            double angle = 2.0 * Math.PI * i / particleCount;
            double x = pos.x + range * Math.cos(angle);
            double z = pos.z + range * Math.sin(angle);
            
            level.sendParticles(darkParticle,
                x, pos.y + 0.1, z,
                1, 0, 0.1, 0, 0);
        }
        
        // 信念≥5时添加额外的火焰粒子环绕
        if (faith >= MID_BELIEF) {
            for (int i = 0; i < 8; i++) {
                double angle = 2.0 * Math.PI * i / 8;
                double x = pos.x + 0.8 * Math.cos(angle);
                double z = pos.z + 0.8 * Math.sin(angle);
                
                level.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.FLAME,
                    x, pos.y + 1, z,
                    1, 0, 0, 0, 0.02
                );
            }
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...祸水之力暂歇..."));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 这是一个纯被动技能，不需要手动开关
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...祸水之力无法人为控制..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        long timeMs = cooldownEndTime - System.currentTimeMillis();
        if (timeMs > 0) {
            long remainingSeconds = timeMs / 20 / 50;
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...祸水之力尚需" + remainingSeconds + "秒恢复..."));
            return false;
        }
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 获取玩家视线中的目标
        int faith = SanityManager.getFaith(player);
        double reach = faith >= MID_BELIEF ? MID_RANGE : BASE_RANGE;
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        Vec3 endPos = eyePosition.add(lookVector.x * reach, lookVector.y * reach, lookVector.z * reach);
        
        EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(
            player.level(),
            player,
            eyePosition,
            endPos,
            new AABB(eyePosition, endPos).inflate(1.0),
            entity -> entity instanceof ServerPlayer && entity != player,
            0.0f
        );

        if (hitResult == null || !(hitResult.getEntity() instanceof ServerPlayer target)) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...视线中无可救助之人..."));
            return;
        }
        
        // 检查目标理智是否已满
        int targetSanity = SanityManager.getSanity(target);
        int targetMaxSanity = SanityManager.getMaxSanity(target);
        if (targetSanity >= targetMaxSanity) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f..." + target.getName().getString() + "的心神已然充盈..."));
            return;
        }

        // 检查是否可以免费释放
        int currentSanity = SanityManager.getSanity(player);
        boolean isFree = faith >= MIN_BELIEF && currentSanity < FREE_COST_THRESHOLD;

        // 恢复目标理智
        SanityManager.modifySanity(target, targetMaxSanity - targetSanity);
        
        // 信念≥5时给予抗性提升效果
        if (faith >= MID_BELIEF) {
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE, 200, 0));
        }
        
        // 设置冷却时间
        int currentSanityAfterUse = SanityManager.getSanity(player);
        long baseCooldown = currentSanityAfterUse < LOW_SANITY_THRESHOLD ? LOW_SANITY_COOLDOWN : NORMAL_COOLDOWN;
        long actualCooldown = faith >= MID_BELIEF ? baseCooldown / 2 : baseCooldown;
        cooldownEndTime = System.currentTimeMillis() + actualCooldown * 50;
        
        // 发送信息
        String faithLevel = faith >= MIN_BELIEF ? "坚定" : (faith >= MID_BELIEF ? "稳固" : "微弱");
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...祸水(" + faithLevel + ")之力已为" + target.getName().getString() + "恢复心神..."));
        target.sendSystemMessage(Component.literal("§b[十日终焉] §f...你的心神被" + player.getName().getString() + "恢复了..."));
        updateState(player);
        notifyEchoClocks(player);
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("lastDayTime", lastDayTime);
        tag.putLong("cooldownEndTime", cooldownEndTime);
        tag.putInt("fireGenerationDuration", fireGenerationDuration);
        return tag;
    }
    
    public static HuoShuiEcho fromNBT(CompoundTag tag) {
        HuoShuiEcho echo = new HuoShuiEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.lastDayTime = tag.getLong("lastDayTime");
        echo.cooldownEndTime = tag.getLong("cooldownEndTime");
        echo.fireGenerationDuration = tag.getInt("fireGenerationDuration");
        return echo;
    }
} 