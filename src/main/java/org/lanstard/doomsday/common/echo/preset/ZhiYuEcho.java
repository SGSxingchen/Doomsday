package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;

import java.util.List;

public class ZhiYuEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.ZHIYU;
    private static final int SANITY_COST = 20;
    private static final int MIN_FAITH = 10;
    private static final int FREE_COST_THRESHOLD = 300;
    private static final int CONTINUOUS_SANITY_COST = 2;
    private static final int HEAL_RANGE = 5;
    private static final int MAX_SANITY_HEAL = 500;
    private static final int ACTIVE_DURATION = 1200; // 60秒 = 1200tick
    private static final int COOLDOWN = 12000; // 10分钟 = 12000tick
    
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
        if (isActive()) {
            tickCounter++;
            activeTime++;
            
            // 每秒消耗2点理智并治疗附近玩家
            if (tickCounter >= 20) {
                tickCounter = 0;
                SanityManager.modifySanity(player, -CONTINUOUS_SANITY_COST);
                
                // 获取范围内的其他玩家
                AABB box = player.getBoundingBox().inflate(HEAL_RANGE);
                List<ServerPlayer> nearbyPlayers = player.level().getEntitiesOfClass(
                    ServerPlayer.class,
                    box,
                    p -> p != player
                );
                
                // 为每个玩家恢复生命和理智
                for (ServerPlayer target : nearbyPlayers) {
                    // 恢复生命值
                    float currentHealth = target.getHealth();
                    float maxHealth = target.getMaxHealth();
                    if (currentHealth < maxHealth) {
                        target.heal(1.0f);
                    }
                    
                    // 恢复理智值
                    int currentSanity = SanityManager.getSanity(target);
                    if (currentSanity < MAX_SANITY_HEAL) {
                        SanityManager.modifySanity(target, 1);
                    }
                    
                    // 生成治愈粒子效果
                    ((ServerLevel)target.level()).sendParticles(
                        ParticleTypes.HEART,
                        target.getX(),
                        target.getY() + 1,
                        target.getZ(),
                        1, 0.5, 0.5, 0.5, 0.1
                    );
                }
            }
            
            // 检查是否达到持续时间
            if (activeTime >= ACTIVE_DURATION) {
                setActiveAndUpdate(player, false);
                lastUseTime = player.level().getGameTime();
                activeTime = 0;
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...治愈之力已耗尽，需要时间恢复..."));
            }
            updateState(player);
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
                setActiveAndUpdate(player, true);
                activeTime = 0;
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...治愈之力已开启..."));
            }
        } else {
            // 关闭时直接关闭
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