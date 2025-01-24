package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.phys.Vec3;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import net.minecraft.nbt.CompoundTag;
import org.joml.Vector3f;

import java.util.List;

public class ShengShengBuXiEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.SHENGSHENGBUXI;
    private static final int REGENERATION_RANGE = 5;
    private static final int MIN_SANITY_REQUIREMENT = 200;
    private static final int MIN_BELIEF_REQUIREMENT = 1;
    private static final int FREE_COST_THRESHOLD = 300;          // 免费释放阈值
    
    // 粒子效果相关常量
    private static final float RED = 0.8F;
    private static final float GREEN = 0.2F;
    private static final float BLUE = 0.2F;
    private static final float PARTICLE_SIZE = 0.7F;
    private static final float HEAL_PARTICLE_SIZE = 0.3F;
    
    private int tickCounter = 0;
    private int particleCounter = 0;
    
    public ShengShengBuXiEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            PRESET.getSanityConsumption(),
            PRESET.getContinuousSanityConsumption()
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...生生不息，万物复苏..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        if (!isActive()) return;
        
        // 每3秒(60刻)消耗理智并检查是否需要停用
        tickCounter++;
        if (tickCounter >= 60) {
            tickCounter = 0;    
            updateState(player);
            
            // 检查理智值和信念值
            int currentSanity = SanityManager.getSanity(player);
            int faith = SanityManager.getFaith(player);
            
            boolean freeCost = faith >= 10 && currentSanity < FREE_COST_THRESHOLD;
            
            if (!freeCost && currentSanity < MIN_SANITY_REQUIREMENT) {
                setActiveAndUpdate(player, false);
                onDeactivate(player);
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...生生之力已竭，难以为继..."));
                return;
            }
            
            if (!freeCost) {
                SanityManager.modifySanity(player, -getContinuousSanityConsumption());
            }
        }

        // 应用治疗效果和粒子效果
        Level level = player.level();
        if (!(level instanceof ServerLevel serverLevel)) return;
        
        AABB box = player.getBoundingBox().inflate(REGENERATION_RANGE);
        List<ServerPlayer> nearbyPlayers = level.getEntitiesOfClass(ServerPlayer.class, box);
        
        // 生成领域边缘粒子
        particleCounter++;
        if (particleCounter >= 5) { // 每5tick生成一次边缘粒子
            particleCounter = 0;
            spawnDomainParticles(serverLevel, player);
        }
        
        // 为每个受影响的玩家生成治疗粒子和效果
        for (ServerPlayer nearbyPlayer : nearbyPlayers) {
            nearbyPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 1, false, true));
            spawnHealParticles(serverLevel, nearbyPlayer);
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...生生之力暂歇，待时而发..."));
        tickCounter = 0;
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查理智值是否足够激活
        int currentSanity = SanityManager.getSanity(player);
        if (currentSanity < MIN_SANITY_REQUIREMENT) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神过低，难以引动生生之力..."));
            return false;
        }

        // 检查信念等级
        int beliefLevel = SanityManager.getBeliefLevel(player);
        if (beliefLevel < MIN_BELIEF_REQUIREMENT) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...信念不足，无法引动生生之力..."));
            return false;
        }

        if (currentSanity < getSanityConsumption()) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以激活生生不息..."));
            return false;
        }
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 生生不息是纯被动技能，不需要主动使用逻辑
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        
        if (!isActive()) {
            boolean freeCost = faith >= 10 && currentSanity < FREE_COST_THRESHOLD;
            
            if (!freeCost && currentSanity < MIN_SANITY_REQUIREMENT) {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神过低，难以引动生生之力..."));
                return;
            }
            
            if (!doCanUse(player)) return;
            
            if (!freeCost) {
                SanityManager.modifySanity(player, -getSanityConsumption());
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + getSanityConsumption() + "点心神，引动生生之力..."));
            } else {
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...生生不息，轮回不止...！"));
            }
            
            setActiveAndUpdate(player, true);
            onActivate(player);
        } else {
            setActiveAndUpdate(player, false);
            onDeactivate(player);
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putInt("tick_counter", tickCounter);
        return tag;
    }
    
    public static ShengShengBuXiEcho fromNBT(CompoundTag tag) {
        ShengShengBuXiEcho echo = new ShengShengBuXiEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.tickCounter = tag.getInt("tick_counter");
        return echo;
    }
    
    private void spawnDomainParticles(ServerLevel level, ServerPlayer player) {
        Vec3 center = player.position();
        DustParticleOptions redParticle = new DustParticleOptions(new Vector3f(RED, GREEN, BLUE), PARTICLE_SIZE);
        
        // 在圆形范围边缘生成粒子
        double radius = REGENERATION_RANGE;
        int particleCount = 8; // 每次生成8个粒子
        for (int i = 0; i < particleCount; i++) {
            double angle = 2.0 * Math.PI * i / particleCount;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            
            // 在边缘位置生成粒子
            level.sendParticles(redParticle,
                x, center.y + 0.1, z,
                1, 0, 0, 0, 0);
        }
    }
    
    private void spawnHealParticles(ServerLevel level, ServerPlayer target) {
        Vec3 pos = target.position();
        DustParticleOptions healParticle = new DustParticleOptions(new Vector3f(RED, GREEN, BLUE), HEAL_PARTICLE_SIZE);
        
        // 在玩家周围生成上升的治疗粒子
        double offsetX = (target.getRandom().nextDouble() - 0.5) * 0.5;
        double offsetZ = (target.getRandom().nextDouble() - 0.5) * 0.5;
        
        level.sendParticles(healParticle,
            pos.x + offsetX, pos.y + 0.1, pos.z + offsetZ,
            1, 0, 0.05, 0, 0);
    }
} 