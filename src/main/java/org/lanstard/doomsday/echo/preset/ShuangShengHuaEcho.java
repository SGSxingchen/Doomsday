package org.lanstard.doomsday.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.lanstard.doomsday.echo.*;
import org.lanstard.doomsday.sanity.SanityManager;

import java.util.*;

public class ShuangShengHuaEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.SHUANGSHENGHUA;
    private static final int RANGE = 2; // 影响范围
    private static final int LINK_DURATION = 10 * 60 * 20; // 10分钟的tick数
    private static final int COOLDOWN_DURATION = 10 * 60 * 20; // 10分钟冷却
    private static final int MIN_SANITY_NO_COOLDOWN = 200; // 无冷却理智阈值
    
    private int tickCounter = 0;
    private UUID linkedTarget = null;
    private long linkEndTime = 0;
    private long cooldownEndTime = 0;
    
    public ShuangShengHuaEcho() {
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
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f双生花的回响在你体内共鸣..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        if (!isActive()) return;
        
        // 每秒触发一次效果
        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            
            // 检查理智值是否足够继续维持效果
            int currentSanity = SanityManager.getSanity(player);
            if (currentSanity < getContinuousSanityConsumption()) {
                // 理智不足，自动关闭效果
                setActive(false);
                onDeactivate(player);
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f理智不足，双生花的共鸣被迫停止..."));
                return;
            }
            
            // 消耗理智值
            SanityManager.modifySanity(player, -getContinuousSanityConsumption());
            
            // 应用生命值同步效果
            applyHealthSync(player);
        }
        
        // 检查链接状态
        checkLinkStatus(player);
    }
    
    private void applyHealthSync(ServerPlayer player) {
        // 获取玩家当前生命值
        float playerHealth = player.getHealth();
        
        // 获取范围内的所有生物
        AABB box = player.getBoundingBox().inflate(RANGE);
        List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(LivingEntity.class, box);
        
        for (LivingEntity target : nearbyEntities) {
            if (target == player) continue;
            
            // 同步生命值
            if (target.getHealth() != playerHealth) {
                target.setHealth(playerHealth);
            }
        }
    }
    
    private void checkLinkStatus(ServerPlayer player) {
        if (linkedTarget != null && System.currentTimeMillis() > linkEndTime) {
            // 链接时间结束
            ServerPlayer target = player.getServer().getPlayerList().getPlayer(linkedTarget);
            if (target != null) {
                target.sendSystemMessage(Component.literal("§b[十日终焉] §f与双生花的链接已经断开..."));
            }
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f与目标的双生花链接已经断开..."));
            linkedTarget = null;
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        // 清除链接
        linkedTarget = null;
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f双生花的回响暂时消散了..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查理智值是否足够
        int currentSanity = SanityManager.getSanity(player);
        if (currentSanity < getSanityConsumption()) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f你的理智不足以使用双生花！"));
            return false;
        }
        
        // 检查冷却
        if (System.currentTimeMillis() < cooldownEndTime && currentSanity >= MIN_SANITY_NO_COOLDOWN) {
            long remainingSeconds = (cooldownEndTime - System.currentTimeMillis()) / 1000;
            player.sendSystemMessage(Component.literal(String.format(
                "§c[十日终焉] §f双生花还在冷却中！剩余时间：%d秒", remainingSeconds
            )));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 获取玩家视线方向的目标
        LivingEntity target = getTarget(player);
        if (target == null) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f未找到有效目标！"));
            return;
        }
        
        // 建立链接
        if (target instanceof ServerPlayer targetPlayer) {
            linkedTarget = targetPlayer.getUUID();
            linkEndTime = System.currentTimeMillis() + LINK_DURATION * 50;
            
            // 设置冷却
            int currentSanity = SanityManager.getSanity(player);
            if (currentSanity >= MIN_SANITY_NO_COOLDOWN) {
                cooldownEndTime = System.currentTimeMillis() + COOLDOWN_DURATION * 50;
            }
            
            // 消耗理智值
            SanityManager.modifySanity(player, -getSanityConsumption());
            
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f你与玩家 ")
                .append(targetPlayer.getDisplayName())
                .append(Component.literal(" §f建立了双生花的链接！")));
            targetPlayer.sendSystemMessage(Component.literal("§b[十日终焉] §f玩家 ")
                .append(player.getDisplayName())
                .append(Component.literal(" §f与你建立了双生花的链接！")));
        }
    }
    
    private LivingEntity getTarget(ServerPlayer player) {
        // 获取玩家视线方向的实体
        AABB box = player.getBoundingBox().inflate(10); // 10格范围内
        List<LivingEntity> entities = player.level().getEntitiesOfClass(
            LivingEntity.class,
            box,
            entity -> entity != player && entity.isAlive()
        );
        
        LivingEntity nearestTarget = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (LivingEntity entity : entities) {
            // 检查是否在玩家视线方向
            if (player.hasLineOfSight(entity)) {
                double distance = player.distanceTo(entity);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestTarget = entity;
                }
            }
        }
        
        return nearestTarget;
    }
    
    // 检查是否与指定玩家建立链接
    public boolean isLinkedWith(ServerPlayer player) {
        return linkedTarget != null && linkedTarget.equals(player.getUUID());
    }
    
    // 处理伤害分摊
    public void onDamage(ServerPlayer player, float amount, boolean isDamageOwner) {
        if (linkedTarget == null) return;
        
        ServerPlayer target = player.getServer().getPlayerList().getPlayer(linkedTarget);
        if (target == null || !target.isAlive()) return;
        
        // 分摊伤害
        float sharedDamage = amount / 2;
        
        if (isDamageOwner) {
            // 如果是回响拥有者受伤
            player.hurt(player.damageSources().generic(), sharedDamage);
            target.hurt(player.damageSources().generic(), sharedDamage);
        } else {
            // 如果是链接目标受伤
            target.hurt(target.damageSources().generic(), sharedDamage);
            player.hurt(target.damageSources().generic(), sharedDamage);
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        
        tag.putInt("tick_counter", tickCounter);
        if (linkedTarget != null) {
            tag.putUUID("linked_target", linkedTarget);
            tag.putLong("link_end_time", linkEndTime);
        }
        tag.putLong("cooldown_end_time", cooldownEndTime);
        
        return tag;
    }
    
    public static ShuangShengHuaEcho fromNBT(CompoundTag tag) {
        ShuangShengHuaEcho echo = new ShuangShengHuaEcho();
        echo.setActive(tag.getBoolean("isActive"));
        
        echo.tickCounter = tag.getInt("tick_counter");
        if (tag.contains("linked_target")) {
            echo.linkedTarget = tag.getUUID("linked_target");
            echo.linkEndTime = tag.getLong("link_end_time");
        }
        echo.cooldownEndTime = tag.getLong("cooldown_end_time");
        
        return echo;
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        if (!doCanUse(player)) return;
        
        if (!isActive()) {
            // 激活持续效果
            SanityManager.modifySanity(player, -getSanityConsumption());
            setActive(true);
            onActivate(player);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f你激活了双生花的持续效果..."));
        } else {
            // 关闭持续效果
            setActive(false);
            onDeactivate(player);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f你关闭了双生花的持续效果..."));
        }
    }
} 