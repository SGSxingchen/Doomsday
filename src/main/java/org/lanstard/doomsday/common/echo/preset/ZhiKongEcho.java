package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.chat.Component;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import net.minecraft.nbt.CompoundTag;

public class ZhiKongEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.ZHIKONG;
    private static final int TOGGLE_SANITY_COST = 30;        // 开启消耗
    private static final int CONTINUOUS_SANITY_COST = 1;     // 每秒消耗
    private static final int FREE_COST_THRESHOLD = 300;      // 免费释放阈值
    private static final int MIN_FAITH_REQUIREMENT = 10;     // 最低信念要求
    private static final int EFFECT_DURATION = 10;           // 效果持续时间（0.5秒）
    private static final int EFFECT_AMPLIFIER = 2;           // 效果等级（3级）
    
    private int tickCounter = 0;

    public ZhiKongEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            
            0,  // 无主动技能消耗
            CONTINUOUS_SANITY_COST
        );
        setActive(false);
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...滞空之力涌动，凌空而立..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        if (!isActive()) return;
        
        // 每秒检查一次
        tickCounter++;
        if (tickCounter >= 10) {
            tickCounter = 0;
            
            // 检查理智值和信念值
            int currentSanity = SanityManager.getSanity(player);
            int faith = SanityManager.getFaith(player);
            boolean freeCost = faith >= MIN_FAITH_REQUIREMENT && currentSanity < FREE_COST_THRESHOLD;
            
            // 如果不是免费释放且理智不足，则关闭效果
            if (!freeCost && currentSanity < CONTINUOUS_SANITY_COST) {
                setActiveAndUpdate(player, false);
                onDeactivate(player);
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神已竭，滞空之力消散..."));
                return;
            }
            
            // 如果不是免费释放，消耗理智值
            if (!freeCost) {
                SanityManager.modifySanity(player, -CONTINUOUS_SANITY_COST);
            }
        }
        if(SanityManager.getFaith(player) >= 5 && !player.isCreative() && !player.isSpectator() ) {
            player.getAbilities().mayfly = true;
            player.getAbilities().flying = true;
            player.onUpdateAbilities();
        }
        if (player.isShiftKeyDown()) {
            // 缓降效果
            player.removeEffect(MobEffects.LEVITATION);
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, EFFECT_DURATION, EFFECT_AMPLIFIER, false, true));
        } else {
            // 漂浮效果
            player.removeEffect(MobEffects.SLOW_FALLING);
            player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, EFFECT_DURATION, EFFECT_AMPLIFIER, false, true));
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...滞空之力消散，重返地面..."));
        // 移除效果
        player.removeEffect(MobEffects.LEVITATION);
        player.removeEffect(MobEffects.SLOW_FALLING);
        if(SanityManager.getFaith(player) >= 5 && !player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        if (!isActive()) {
            // 检查理智值和信念值
            int currentSanity = SanityManager.getSanity(player);
            int faith = SanityManager.getFaith(player);
            boolean freeCost = faith >= MIN_FAITH_REQUIREMENT && currentSanity < FREE_COST_THRESHOLD;
            
            // 如果不是免费释放且理智不足，则无法开启
            if (!freeCost && currentSanity < TOGGLE_SANITY_COST) {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以引动滞空之力..."));
                return;
            }
            
            // 如果不是免费释放，消耗理智
            if (!freeCost) {
                SanityManager.modifySanity(player, -TOGGLE_SANITY_COST);
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + TOGGLE_SANITY_COST + "点心神，滞空之力显现..."));
            } else {
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念引导，滞空之力显现..."));
            }
            setActiveAndUpdate(player, true);
            notifyEchoClocks(player);
            onActivate(player);
        } else {
            // 直接关闭，不需要检查理智值
            setActiveAndUpdate(player, false);
            onDeactivate(player);
        }
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 这是一个纯被动技能，不需要主动使用
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...滞空之力需要持续引导..."));
        return false;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 这是一个纯被动技能，不需要主动使用
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putInt("tickCounter", tickCounter);
        return tag;
    }
    
    public static ZhiKongEcho fromNBT(CompoundTag tag) {
        ZhiKongEcho echo = new ZhiKongEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.tickCounter = tag.getInt("tickCounter");
        return echo;
    }
} 