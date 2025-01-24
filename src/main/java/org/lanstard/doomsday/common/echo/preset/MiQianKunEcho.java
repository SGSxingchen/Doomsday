package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.phys.Vec3;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.joml.Vector3f;
import net.minecraft.nbt.CompoundTag;

public class MiQianKunEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.MIQIANKUN;
    private static final int TOGGLE_SANITY_COST = 0;              // 开启不消耗理智
    private static final int CONTINUOUS_SANITY_COST = 1;          // 每5秒消耗1点理智
    private static final int ACTIVE_SANITY_GAIN = 500;           // 主动增加500点理智
    private static final int FAITH_GAIN = 10;                    // 条件触发获得10点信念
    private static final int SANITY_THRESHOLD = 200;             // 信念获取阈值
    private static final int FREE_COST_THRESHOLD = 300;          // 免费释放阈值
    private static final int DAY_REVERSE_DURATION = 12000;       // 昼夜反转持续时间（10分钟）
    private static final int COOLDOWN = 288000;                  // 冷却时间（240分钟）
    
    // 粒子效果相关
    private static final float PURPLE_RED = 0.5F;
    private static final float PURPLE_GREEN = 0.0F;
    private static final float PURPLE_BLUE = 0.5F;
    private static final float PARTICLE_SIZE = 1.0F;
    
    private int tickCounter = 0;
    private int cooldownTicks = 0;
    private boolean faithTriggered = false;
    private long dayReverseEndTime = 0;
    private boolean isDayReversed = false;
    
    public MiQianKunEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            0,  // 主动技能不消耗理智
            CONTINUOUS_SANITY_COST
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...覔乾坤，化阴阳..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        if (!isActive()) return;
        
        // 更新冷却时间
        if (cooldownTicks > 0) {
            cooldownTicks--;
        }
        
        // 每5秒触发一次效果
        tickCounter++;
        if (tickCounter >= 100) {  // 5秒 = 100刻
            tickCounter = 0;
            
            // 检查理智值和信念值
            int currentSanity = SanityManager.getSanity(player);
            int faith = SanityManager.getFaith(player);
            
            // 如果信念大于等于10点且理智低于300，则不消耗理智
            boolean freeCost = faith >= 10 && currentSanity < FREE_COST_THRESHOLD;
            
            // 如果不是免费释放且理智不足，则关闭效果
            if (!freeCost && currentSanity < CONTINUOUS_SANITY_COST) {
                setActive(false);
                onDeactivate(player);
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神已竭，乾坤难觅..."));
                return;
            }
            
            // 如果不是免费释放，消耗理智
            if (!freeCost) {
                SanityManager.modifySanity(player, -CONTINUOUS_SANITY_COST);
            }
            
            // 清除所有buff效果
            clearAllBuffs(player);
            
            // 添加粒子效果
            if (player.level() instanceof ServerLevel serverLevel) {
                spawnAuraParticles(serverLevel, player);
            }
            
            // 检查信念触发条件
            if (!faithTriggered && currentSanity < SANITY_THRESHOLD) {
                SanityManager.modifyFaith(player, FAITH_GAIN);
                faithTriggered = true;
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...心神衰竭之际，信念愈发坚定..."));
            }
        }
        
        // 检查昼夜反转是否结束
        if (isDayReversed && System.currentTimeMillis() > dayReverseEndTime) {
            isDayReversed = false;
            if (player.level() instanceof ServerLevel serverLevel) {
                restoreTimeAndWeather(serverLevel);
            }
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...阴阳归位，乾坤复常..."));
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...乾坤难觅，阴阳已散..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查是否被禁用
        if (this.isDisabled()) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...乾坤之法暂失其效..."));
            return false;
        }
        
        // 检查冷却时间
        if (cooldownTicks > 0) {
            int remainingMinutes = cooldownTicks / 1200;  // 1分钟 = 1200刻
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...乾坤之力尚需积蓄，剩余" + remainingMinutes + "分钟..."));
            return false;
        }

        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        
        // 如果信念大于等于10点且理智低于300，则不消耗理智
        if (faith >= 10 && currentSanity < FREE_COST_THRESHOLD) {
            return true;
        }
        
        // 否则检查理智是否足够
        if (currentSanity < CONTINUOUS_SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以施展..."));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 增加理智值
        SanityManager.modifySanity(player, ACTIVE_SANITY_GAIN);
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...心神大增，获得" + ACTIVE_SANITY_GAIN + "点理智..."));
        
        // 反转昼夜
        if (player.level() instanceof ServerLevel serverLevel) {
            reverseTimeAndWeather(serverLevel);
            isDayReversed = true;
            dayReverseEndTime = System.currentTimeMillis() + DAY_REVERSE_DURATION * 50;  // 转换为毫秒
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...阴阳逆转，乾坤倒置..."));
        }
        
        // 设置冷却
        cooldownTicks = COOLDOWN;
    }

    private void clearAllBuffs(ServerPlayer player) {
        for (MobEffectInstance effect : player.getActiveEffects()) {
            MobEffect mobEffect = effect.getEffect();
            // 只清除正面效果
            if (!mobEffect.isBeneficial()) continue;
            player.removeEffect(mobEffect);
        }
    }

    private void reverseTimeAndWeather(ServerLevel level) {
        // 保存当前时间
        long currentTime = level.getDayTime();
        // 设置为相反的时间
        level.setDayTime((currentTime + 12000) % 24000);
        // 如果是晴天则下雨，如果下雨则放晴
        if (level.isRaining()) {
            level.setWeatherParameters(0, 0, false, false);
        } else {
            level.setWeatherParameters(0, 6000, true, false);
        }
    }

    private void restoreTimeAndWeather(ServerLevel level) {
        // 恢复正常时间
        long currentTime = level.getDayTime();
        level.setDayTime((currentTime + 12000) % 24000);
        // 恢复正常天气
        level.setWeatherParameters(6000, 0, false, false);
    }

    private void spawnAuraParticles(ServerLevel level, ServerPlayer player) {
        DustParticleOptions purpleParticle = new DustParticleOptions(
            new Vector3f(PURPLE_RED, PURPLE_GREEN, PURPLE_BLUE),
            PARTICLE_SIZE * 0.5F
        );
        
        Vec3 pos = player.position();
        // 生成阴阳形状的粒子效果
        for (int i = 0; i < 16; i++) {
            double angle = 2.0 * Math.PI * i / 16;
            double radius = 1.0;
            double x = pos.x + radius * Math.cos(angle);
            double z = pos.z + radius * Math.sin(angle);
            
            // 阴阳两侧使用不同的高度
            double y = pos.y + (i < 8 ? 2.0 : 1.0);
            
            level.sendParticles(purpleParticle,
                x, y, z,
                1, 0, 0, 0, 0);
        }
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        
        if (!isActive()) {
            // 如果信念大于等于10点且理智低于300，则不消耗理智
            boolean freeCost = faith >= 10 && currentSanity < FREE_COST_THRESHOLD;
            
            // 如果不是免费释放且理智不足，则无法开启
            if (!freeCost && currentSanity < TOGGLE_SANITY_COST) {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以施展..."));
                return;
            }
            
            // 如果不是免费释放，消耗理智
            if (!freeCost) {
                SanityManager.modifySanity(player, -TOGGLE_SANITY_COST);
            }
            
            setActive(true);
            onActivate(player);
        } else {
            setActive(false);
            onDeactivate(player);
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putInt("tickCounter", tickCounter);
        tag.putInt("cooldownTicks", cooldownTicks);
        tag.putBoolean("faithTriggered", faithTriggered);
        tag.putLong("dayReverseEndTime", dayReverseEndTime);
        tag.putBoolean("isDayReversed", isDayReversed);
        return tag;
    }
    
    public static MiQianKunEcho fromNBT(CompoundTag tag) {
        MiQianKunEcho echo = new MiQianKunEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.tickCounter = tag.getInt("tickCounter");
        echo.cooldownTicks = tag.getInt("cooldownTicks");
        echo.faithTriggered = tag.getBoolean("faithTriggered");
        echo.dayReverseEndTime = tag.getLong("dayReverseEndTime");
        echo.isDayReversed = tag.getBoolean("isDayReversed");
        return echo;
    }
} 