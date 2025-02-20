package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;

import java.util.List;

public class ZhiYuEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.ZHIYU;
    private static final int SANITY_COST = 15;
    private static final int MIN_FAITH = 8;
    private static final int FREE_COST_THRESHOLD = 200;
    private static final int CONTINUOUS_SANITY_COST = 1;
    private static final int HEAL_RANGE = 8;
    private static final int MAX_SANITY_HEAL = 500;
    private static final int MAX_SANITY_HEAL_HIGH_FAITH = 800;
    private static final int ACTIVE_DURATION = 2400; // 120秒 = 2400tick
    private static final int COOLDOWN = 6000; // 5分钟 = 6000tick
    
    private int tickCounter = 0;
    private int activeTime = 0;
    private long lastUseTime = 0;

    public ZhiYuEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            SANITY_COST,
            CONTINUOUS_SANITY_COST
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...治愈之力涌动，生命与心神将得到抚慰..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        if (isActive()) {

            tickCounter++;
            activeTime++;
            
            // 每秒处理一次治疗效果
            if (tickCounter >= 20) {
                tickCounter = 0;


                boolean freeCost = currentSanity < FREE_COST_THRESHOLD || faith >= MIN_FAITH;

                if (!freeCost) SanityManager.modifySanity(player, -CONTINUOUS_SANITY_COST);
                
                // 获取范围内的其他玩家
                AABB box = player.getBoundingBox().inflate(HEAL_RANGE);
                List<ServerPlayer> nearbyPlayers = player.level().getEntitiesOfClass(
                    ServerPlayer.class,
                    box,
                    p -> p != player
                );
                
                // 为每个玩家恢复生命和理智
                for (ServerPlayer target : nearbyPlayers) {
                    // 恢复生命值，增加恢复量
                    float currentHealth = target.getHealth();
                    float maxHealth = target.getMaxHealth();

                    if(faith >= 5){
                        if (currentHealth < maxHealth) {
                            target.heal(3.0f);
                        }

                        // 恢复理智值，增加恢复量
                        int targetSanity = SanityManager.getSanity(target);
                        if (targetSanity < (faith >= 5 ? MAX_SANITY_HEAL_HIGH_FAITH : MAX_SANITY_HEAL)) {
                            SanityManager.modifySanity(target, 4);
                        }
                    }
                    else{
                        if (currentHealth < maxHealth) {
                            target.heal(1.5f);
                        }
                        // 恢复理智值，增加恢复量
                        int targetSanity = SanityManager.getSanity(target);
                        if (targetSanity < (faith >= 5 ? MAX_SANITY_HEAL_HIGH_FAITH : MAX_SANITY_HEAL)) {
                            SanityManager.modifySanity(target, 2);
                        }
                    }
                    // 生成治愈粒子效果
                    spawnEffectParticles((ServerLevel) player.level(), target.position(), HEAL_RANGE);
                }

                // 施法者自身也获得治疗效果
                float currentHealth = player.getHealth();
                float maxHealth = player.getMaxHealth();
                if (currentHealth < maxHealth) {
                    if(faith >= 5){
                        player.heal(6.0f);
                    }
                    else{
                        player.heal(3.0f);
                    }
                }
            }
            
            // 检查是否达到持续时间
            if (activeTime >= ACTIVE_DURATION) {
                setActiveAndUpdate(player, false);
                lastUseTime = player.level().getGameTime();
                activeTime = 0;
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...治愈之力已耗尽，需要时间恢复..."));
            }
        } else {

            // 在非激活状态下，如果不在冷却中且activeTime > 0，则逐渐恢复
            if (player.level().getGameTime() - lastUseTime >= COOLDOWN && activeTime > 0) {
                tickCounter++;
                if (tickCounter >= 40) { // 每2秒减少1点activeTime
                    tickCounter = 0;
                    if(faith >= 5){
                        activeTime = Math.max(0, activeTime - 2);
                    }
                    else{
                        activeTime = Math.max(0, activeTime - 1);
                    }
                }
            }
        }
        updateState(player);
    }
    // 粒子效果相关
    private static final float DARK_RED = 0.0F;
    private static final float DARK_GREEN = 0.8F;
    private static final float DARK_BLUE = 0.2F;
    private static final float PARTICLE_SIZE = 1.2F;
    private void spawnEffectParticles(ServerLevel level, Vec3 pos, double RANGE) {
        DustParticleOptions darkParticle = new DustParticleOptions(
                new Vector3f(DARK_RED, DARK_GREEN, DARK_BLUE),
                PARTICLE_SIZE
        );

        // 生成双层环形粒子效果
        for (int i = 0; i < 36; i++) {
            double angle = 2.0 * Math.PI * i / 36;
            double radius = RANGE;
            double x = pos.x + radius * Math.cos(angle);
            double z = pos.z + radius * Math.sin(angle);

            // 外环
            level.sendParticles(darkParticle,
                    x, pos.y + 0.1, z,
                    1, 0, 0.1, 0, 0);
                    
            // 内环
            level.sendParticles(ParticleTypes.HEART,
                    x * 0.5 + pos.x * 0.5, 
                    pos.y + 0.5, 
                    z * 0.5 + pos.z * 0.5,
                    1, 0, 0.1, 0, 0.01);
        }
    }
    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...治愈之力消散..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查冷却时间
        long currentTime = player.level().getGameTime();
        if (lastUseTime > 0) {
            long timeDiff = currentTime - lastUseTime;
            if (timeDiff < COOLDOWN) {
                long remainingSeconds = (COOLDOWN - timeDiff) / 20;
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...治愈之力尚需" + remainingSeconds + "秒恢复..."));
                return false;
            }
        }

        // 检查理智值是否足够
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = currentSanity < FREE_COST_THRESHOLD || faith >= MIN_FAITH;

        if (!freeCost && currentSanity < SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不宁，难以施展治愈之法..."));
            return false;
        }

        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 不需要额外操作
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        if (!isActive()) {
            // 开启时才需要判断是否可用
            if (doCanUse(player)) {
                onActivate(player);
                setActiveAndUpdate(player, true);
                notifyEchoClocks(player);
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...治愈之力已开启..."));
            }
        } else {
            // 关闭时直接关闭，但不重置activeTime
            onDeactivate(player);
            setActiveAndUpdate(player, false);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...治愈之力已关闭..."));
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putInt("tickCounter", tickCounter);
        tag.putInt("activeTime", activeTime);
        tag.putLong("lastUseTime", lastUseTime);
        return tag;
    }

    public static ZhiYuEcho fromNBT(CompoundTag tag) {
        ZhiYuEcho echo = new ZhiYuEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.tickCounter = tag.getInt("tickCounter");
        echo.activeTime = tag.getInt("activeTime");
        echo.lastUseTime = tag.getLong("lastUseTime");
        return echo;
    }
} 