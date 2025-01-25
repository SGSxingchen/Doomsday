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
    private static final int RANGE = 10;                       // 影响范围
    private static final int FIRE_COUNT = 20;                  // 每次生成的火焰数量
    private static final int FIRE_DURATION = 100;              // 火焰生成持续时间(5秒)
    private static final int SANITY_REDUCTION = 50;           // 理智降低量
    private static final int NORMAL_COOLDOWN = 72000;         // 60分钟冷却
    private static final int LOW_SANITY_COOLDOWN = 12000;     // 10分钟冷却
    private static final int LOW_SANITY_THRESHOLD = 200;      // 低理智阈值
    private static final long NIGHT_TIME = 18000;             // 夜晚时间点
    private static final long DAY_TIME = 6000;                // 白天时间点
    
    // 粒子效果相关
    private static final float DARK_RED = 0.8F;
    private static final float DARK_GREEN = 0.2F;
    private static final float DARK_BLUE = 0.2F;
    private static final float PARTICLE_SIZE = 1.0F;
    
    private static final int SANITY_COST = 0;
    private static final int MIN_BELIEF = 10;
    private static final int FREE_COST_THRESHOLD = 300;
    
    private long lastDayTime = 0;
    private long cooldownEndTime = 0;
    private int fireGenerationDuration = 0;                   // 火焰生成剩余时间
    private final Random random = new Random();

    public HuoShuiEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
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

        // 获取当前时间
        long currentDayTime = player.level().getDayTime() % 24000;
        
        // 检查是否是夜晚时分（18000）或白天时分（6000）
        boolean isNightTime = Math.abs(currentDayTime - 18000) < 100;
        boolean isDayTime = Math.abs(currentDayTime - 6000) < 100;
        
        // 只有当lastDayTime不在同一时间段时才触发效果
        if ((isNightTime || isDayTime) && !isInSameTimePhase(lastDayTime, currentDayTime)) {
            if (isNightTime) {
                // 开始火焰生成周期
                fireGenerationDuration = FIRE_DURATION;
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...祸水之力涌动，火焰四起..."));
            } else {
                // 白天效果：降低理智
                reduceSanityForNearbyPlayers(player);
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...祸水之力涌动，心神动荡..."));
            }
            // 生成粒子效果
            if (player.level() instanceof ServerLevel serverLevel) {
                spawnEffectParticles(serverLevel, player.position());
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
        
        // 每tick生成较少的火焰，但持续更长时间
        int firesPerTick = FIRE_COUNT ;
        
        for (int i = 0; i < firesPerTick; i++) {
            int x = player.getBlockX() + random.nextInt(RANGE * 2) - RANGE;
            int z = player.getBlockZ() + random.nextInt(RANGE * 2) - RANGE;
            for(int j = -5; j < 5; j++){
                int y = player.getBlockY() + j;
                // 找到一个合适的位置放置火焰
                BlockPos pos = new BlockPos(x, y, z);
                if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isSolidRender(level, pos)) {
                    level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                }
            }

        }
        
        // 生成粒子效果
        spawnEffectParticles(level, player.position());
        
        updateState(player);
    }

    // 降低附近玩家的理智
    private void reduceSanityForNearbyPlayers(ServerPlayer player) {
        AABB box = player.getBoundingBox().inflate(RANGE);
        List<ServerPlayer> nearbyPlayers = player.level().getEntitiesOfClass(
            ServerPlayer.class,
            box,
            p -> p != player
        );
        
        for (ServerPlayer target : nearbyPlayers) {
            SanityManager.modifySanity(target, -SANITY_REDUCTION);
            target.sendSystemMessage(Component.literal("§c[十日终焉] §f...祸水之力侵蚀，心神受损..."));
        }
        
        // 生成粒子效果
        if (player.level() instanceof ServerLevel serverLevel) {
            spawnEffectParticles(serverLevel, player.position());
        }
        
        updateState(player);
    }

    private void spawnEffectParticles(ServerLevel level, Vec3 pos) {
        DustParticleOptions darkParticle = new DustParticleOptions(
            new Vector3f(DARK_RED, DARK_GREEN, DARK_BLUE),
            PARTICLE_SIZE
        );
        
        // 生成环形粒子效果
        for (int i = 0; i < 36; i++) {
            double angle = 2.0 * Math.PI * i / 36;
            double radius = RANGE;
            double x = pos.x + radius * Math.cos(angle);
            double z = pos.z + radius * Math.sin(angle);
            
            level.sendParticles(darkParticle,
                x, pos.y + 0.1, z,
                1, 0, 0.1, 0, 0);
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
        double reach = RANGE;
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
        boolean isFree = SanityManager.getFaith(player) >= MIN_BELIEF && currentSanity < FREE_COST_THRESHOLD;
        
        // 消耗理智
        if (!isFree) {
            SanityManager.modifySanity(player, -SANITY_COST);
        }

        // 恢复目标理智
        SanityManager.modifySanity(target, targetMaxSanity - targetSanity);
        
        // 设置冷却时间
        int currentSanityAfterUse = SanityManager.getSanity(player);
        cooldownEndTime = System.currentTimeMillis() + 
            (currentSanityAfterUse < LOW_SANITY_THRESHOLD ? LOW_SANITY_COOLDOWN : NORMAL_COOLDOWN) * 50; // 转换为毫秒
        
        // 发送信息
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...已为" + target.getName().getString() + "恢复心神..."));
        target.sendSystemMessage(Component.literal("§b[十日终焉] §f...你的心神被" + player.getName().getString() + "恢复了..."));
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