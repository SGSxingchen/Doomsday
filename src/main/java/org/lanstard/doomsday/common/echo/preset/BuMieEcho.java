package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoType;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.config.EchoConfig;

public class BuMieEcho extends Echo {
    
    private long lastUseTime = 0;
    private long immortalEndTime = 0;
    private int tickCounter = 0;
    private float storedDamage = 0;                         // 储存的伤害值

    public BuMieEcho() {
        super("bumie", "不灭", EchoType.ACTIVE, EchoConfig.BUMIE_SANITY_COST.get());
    }

    @Override
    public void onActivate(ServerPlayer player) {
        // 主动技能，不需要激活逻辑
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        if (isImmortal(player)) {
            tickCounter++;
            
            // 每秒检查一次
            if (tickCounter >= 20) {
                tickCounter = 0;
                // 检查是否到达结束时间
                if (player.level().getGameTime() >= immortalEndTime) {
                    int faith = SanityManager.getFaith(player);
                    float finalDamage;
                    
                    if (faith >= EchoConfig.BUMIE_MIN_FAITH.get()) {
                        finalDamage = EchoConfig.BUMIE_HIGH_RETALIATION_DAMAGE.get().floatValue();
                        // 高等信念下储存的伤害减免25%
                        storedDamage *= 0.25f;
                    } else if (faith >= EchoConfig.BUMIE_MID_FAITH.get()) {
                        finalDamage = EchoConfig.BUMIE_MID_RETALIATION_DAMAGE.get().floatValue();
                        // 中等信念下储存的伤害减免50%
                        storedDamage *= 0.5f;
                    } else {
                        finalDamage = EchoConfig.BUMIE_BASE_RETALIATION_DAMAGE.get().floatValue();
                    }
                    
                    // 结束不灭状态并造成伤害
                    player.hurt(player.damageSources().magic(), finalDamage + storedDamage);
                    
                    // 根据信念等级发送不同的消息
                    String faithLevel = faith >= EchoConfig.BUMIE_MIN_FAITH.get() ? "坚定" : (faith >= EchoConfig.BUMIE_MID_FAITH.get() ? "稳固" : "微弱");
                    player.sendSystemMessage(Component.literal("§c[十日终焉] §f...不灭(" + faithLevel + ")之法已然消散，反噬之力涌现..."));
                    
                    // 重置状态
                    immortalEndTime = 0;
                    storedDamage = 0;
                    notifyEchoClocks(player);
                    updateState(player);
                }
            }
            
            // 保持生命值不低于1并累积伤害
            if (player.getHealth() < 1.0f) {
                float damageTaken = 1.0f - player.getHealth();
                storedDamage += damageTaken;
                player.setHealth(1.0f);
                
                // 信念≥5时获得短暂的抗性提升
                int faith = SanityManager.getFaith(player);
                if (faith >= EchoConfig.BUMIE_MID_FAITH.get()) {
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE,
                        60,  // 3秒
                        faith >= EchoConfig.BUMIE_MIN_FAITH.get() ? 2 : 1  // 信念≥10时获得抗性III，否则抗性II
                    ));
                }
            }
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        // 主动技能，不需要停用逻辑
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 主动技能，不支持持续施展
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...不灭之法不支持持续施展..."));
    }

    @Override
    public boolean doCanUse(ServerPlayer player) {
        // 检查是否已经处于不灭状态
        if (isImmortal(player)) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...不灭之法正在生效中..."));
            return false;
        }
        // 检查冷却时间
        long currentTime = player.level().getGameTime();
        if (currentTime - lastUseTime < EchoConfig.BUMIE_COOLDOWN_TICKS.get()) {
            int remainingMinutes = (int)((EchoConfig.BUMIE_COOLDOWN_TICKS.get() - (currentTime - lastUseTime)) / 1200);
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...不灭之法尚需" + remainingMinutes + "分钟冷却..."));
            return false;
        }

        return true;
    }

    @Override
    public void doUse(ServerPlayer player) {
        // 检查信仰值和理智值，决定是否免费释放
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = currentSanity < EchoConfig.BUMIE_FREE_COST_THRESHOLD.get() || faith >= EchoConfig.BUMIE_MIN_FAITH.get();

        // 如果不是免费释放，检查理智值是否足够
        if (!freeCost && currentSanity < EchoConfig.BUMIE_SANITY_COST.get()) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不宁，难以施展不灭之法..."));
            return;
        }

        // 更新最后使用时间和不灭状态结束时间
        lastUseTime = player.level().getGameTime();
        immortalEndTime = lastUseTime + EchoConfig.BUMIE_DURATION_TICKS.get();
        storedDamage = 0;  // 重置储存的伤害值

        // 如果不是免费释放，消耗理智值
        if (!freeCost) {
            // 信念≥5时消耗减半
            int actualCost = faith >= EchoConfig.BUMIE_MID_FAITH.get() ? EchoConfig.BUMIE_SANITY_COST.get() / 2 : EchoConfig.BUMIE_SANITY_COST.get();
            SanityManager.modifySanity(player, -actualCost);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗了" + actualCost + "点心神之力..."));
        }

        // 发送激活消息
        String faithLevel = faith >= EchoConfig.BUMIE_MIN_FAITH.get() ? "坚定" : (faith >= EchoConfig.BUMIE_MID_FAITH.get() ? "稳固" : "微弱");
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...不灭(" + faithLevel + ")之法已然生效，五分钟内将不会死亡..."));
        
        // 信念≥5时获得初始增益效果
        if (faith >= EchoConfig.BUMIE_MID_FAITH.get()) {
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE,
                200,  // 10秒
                faith >= EchoConfig.BUMIE_MIN_FAITH.get() ? 2 : 1  // 信念≥10时获得抗性III，否则抗性II
            ));
            
            if (faith >= EchoConfig.BUMIE_MIN_FAITH.get()) {
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.REGENERATION,
                    200,  // 10秒
                    1    // 生命恢复II
                ));
            }
        }
        
        // 更新状态
        updateState(player);
    }

    public boolean isImmortal(ServerPlayer player) {
        return immortalEndTime > 0 && player.level().getGameTime() < immortalEndTime;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("lastUseTime", lastUseTime);
        tag.putLong("immortalEndTime", immortalEndTime);
        tag.putInt("tickCounter", tickCounter);
        tag.putFloat("storedDamage", storedDamage);
        return tag;
    }

    public static BuMieEcho fromNBT(CompoundTag tag) {
        BuMieEcho echo = new BuMieEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.lastUseTime = tag.getLong("lastUseTime");
        echo.immortalEndTime = tag.getLong("immortalEndTime");
        echo.tickCounter = tag.getInt("tickCounter");
        echo.storedDamage = tag.getFloat("storedDamage");
        return echo;
    }
} 