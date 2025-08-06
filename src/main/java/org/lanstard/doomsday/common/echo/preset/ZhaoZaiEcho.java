package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.phys.Vec3;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.common.entities.QiheiSwordEntity;
import org.lanstard.doomsday.common.entities.ModEntities;
import net.minecraft.nbt.CompoundTag;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;

public class ZhaoZaiEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.ZHAOZAI;
    private static final int RANGE = 10;                       // 影响范围
    private static final int WITHER_DURATION = 15 * 20;       // 凋零效果持续时间（15秒）
    private static final int WITHER_AMPLIFIER = 0;            // 凋零效果等级（1级）
    private static final int GLOWING_DURATION = 30 * 20;      // 发光效果持续时间（30秒）
    private static final int CHECK_INTERVAL = 10 * 20;        // 检查间隔（10秒）
    private static final float BASE_SUCCESS_RATE = 0.05f;     // 基础成功率（5%）
    private static final float HIGH_FAITH_SUCCESS_RATE = 0.08f; // 高信念基础成功率（8%）
    private static final float SUCCESS_RATE_INCREMENT = 0.01f; // 失败后成功率增加（1%）
    private static final float HIGH_FAITH_RATE_INCREMENT = 0.015f; // 高信念失败后成功率增加（1.5%）
    private static final int HIGH_FAITH_RANGE = 15;           // 高信念时的影响范围（15格）
    private static final int SUMMON_SANITY_COST = 100;        // 召唤消耗的理智值
    private static final int COOLDOWN_TICKS = 36000;          // 冷却时间（30分钟 = 36000刻）
    private static final int MIN_FAITH = 10;                  // 最低信念要求
    private static final int FREE_COST_THRESHOLD = 300;       // 免费释放的理智阈值
    
    // 粒子效果相关
    private static final float DARK_RED = 0.5F;
    private static final float DARK_GREEN = 0.0F;
    private static final float DARK_BLUE = 0.0F;
    private static final float PARTICLE_SIZE = 1.0F;
    
    private int tickCounter = 0;
    private float currentSuccessRate = BASE_SUCCESS_RATE;
    private final Random random = new Random();
    private int summonCooldown = 0;                          // 召唤冷却计时器

    public ZhaoZaiEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            
            SUMMON_SANITY_COST,  // 主动技能消耗100理智
            0   // 无被动消耗
        );
        setActive(true); // 默认激活
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...招灾之力缠绕，灾厄将至..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 更新召唤冷却
        if (summonCooldown > 0) {
            summonCooldown--;
        }
        if(!isActive()) return;
        // 原有的被动效果逻辑
        tickCounter++;
        if (tickCounter >= CHECK_INTERVAL) {
            tickCounter = 0;
            
            // 检查昼夜状态
            Level level = player.level();
            long dayTime = level.getDayTime() % 24000;
            boolean currentIsNight = dayTime >= 13000 && dayTime < 23000;
            
            // 获取当前信念值
            int faith = SanityManager.getFaith(player);
            boolean isHighFaith = faith >= 5;
            
            // 尝试触发效果
            if (random.nextFloat() < currentSuccessRate) {
                // 成功触发效果
                AABB box = player.getBoundingBox().inflate(isHighFaith ? HIGH_FAITH_RANGE : RANGE);
                List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(
                    LivingEntity.class,
                    box,
                    entity -> {
                        if (entity == player) return false;
                        if (entity instanceof ServerPlayer targetPlayer) {
                            // 排除OP、创造模式和旁观模式的玩家
                            if (targetPlayer.hasPermissions(2) || targetPlayer.isCreative() || targetPlayer.isSpectator()) {
                                return false;
                            }
                            // 高信念时，检查Team系统
                            if (isHighFaith) {
                                return !player.isAlliedTo(targetPlayer);
                            }
                        }
                        // 如果不是玩家或者信念不够，则认为是敌对生物
                        return true;
                    }
                );
                
                for (LivingEntity target : nearbyEntities) {
                    if (currentIsNight) {
                        // 夜晚：凋零效果
                        target.addEffect(new MobEffectInstance(MobEffects.WITHER, 
                            WITHER_DURATION, 
                            isHighFaith ? 1 : WITHER_AMPLIFIER,
                            false, true));
                        if (isHighFaith) {
                            // 高信念时额外给予失明效果，降低敌人视野
                            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 
                                WITHER_DURATION, 0, false, true));
                        }
                        target.sendSystemMessage(Component.literal("§c[十日终焉] §f...夜色降临，灾厄缠身..."));
                    } else {
                        // 白天：发光效果
                        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 
                            GLOWING_DURATION,
                            0, false, true));
                        if (isHighFaith) {
                            // 高信念时额外给予中毒效果
                            target.addEffect(new MobEffectInstance(MobEffects.POISON, 
                                GLOWING_DURATION / 2, 0, false, true));
                        }
                        target.sendSystemMessage(Component.literal("§b[十日终焉] §f...白昼将至，灾厄显形..."));
                    }
                }
                
                // 生成粒子效果
                if (level instanceof ServerLevel serverLevel) {
                    spawnEffectParticles(serverLevel, player.position());
                }
                
                // 重置成功率
                currentSuccessRate = isHighFaith ? HIGH_FAITH_SUCCESS_RATE : BASE_SUCCESS_RATE;
                
                // 显示触发信息
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...灾厄显现..."));
            } else {
                // 失败，增加下次成功率
                currentSuccessRate += isHighFaith ? HIGH_FAITH_RATE_INCREMENT : SUCCESS_RATE_INCREMENT;
                
                // 显示当前成功率
                // int percentage = Math.round(currentSuccessRate * 100);
                // player.sendSystemMessage(Component.literal("§7[十日终焉] §f...灾厄积蓄中，当前触发概率：" + percentage + "%..."));
            }
            updateState(player);
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...招灾之力被压制，暂时消散..."));
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
    public void toggleContinuous(ServerPlayer player) {
        // 这是一个纯被动技能，不需要手动开关
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...招灾之力无法人为控制..."));
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putInt("tickCounter", tickCounter);
        tag.putFloat("currentSuccessRate", currentSuccessRate);
        tag.putInt("summonCooldown", summonCooldown);
        return tag;
    }
    
    public static ZhaoZaiEcho fromNBT(CompoundTag tag) {
        ZhaoZaiEcho echo = new ZhaoZaiEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.tickCounter = tag.getInt("tickCounter");
        echo.currentSuccessRate = tag.getFloat("currentSuccessRate");
        echo.summonCooldown = tag.getInt("summonCooldown");
        return echo;
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查冷却时间
        if (summonCooldown > 0) {
            int remainingMinutes = summonCooldown / 1200; // 转换为分钟
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...七黑剑尚未准备就绪，需等待" + remainingMinutes + "分钟..."));
            return false;
        }

        // 检查信念点数和理智值，判断是否免费释放
        int faith = SanityManager.getFaith(player);
        int currentSanity = SanityManager.getSanity(player);
        boolean isFree = faith >= MIN_FAITH && currentSanity < FREE_COST_THRESHOLD;

        // 如果不是免费释放，检查理智值是否足够
        if (!isFree && currentSanity < SUMMON_SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...理智不足，无法召唤七黑剑..."));
            return false;
        }

        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 检查是否免费释放
        int faith = SanityManager.getFaith(player);
        int currentSanity = SanityManager.getSanity(player);
        boolean isFree = faith >= MIN_FAITH && currentSanity < FREE_COST_THRESHOLD;

        // 只有在不免费时才消耗理智值
        if (!isFree) {
            SanityManager.modifySanity(player, -SUMMON_SANITY_COST);
        }
        
        // 在玩家前方3格处召唤七黑剑
        ServerLevel level = (ServerLevel) player.level();
        Vec3 lookVec = player.getLookAngle();
        Vec3 spawnPos = player.position().add(lookVec.scale(3));
        
        // 创建七黑剑实体
        QiheiSwordEntity sword = new QiheiSwordEntity(
            ModEntities.QIHEI_SWORD.get(),
            level
        );
        sword.setPos(spawnPos.x, spawnPos.y + 1, spawnPos.z);
        sword.setInvulnerable(true); // 设置为无敌状态
        
        // 生成粒子效果
        spawnEffectParticles(level, spawnPos);
        
        // 将剑添加到世界
        level.addFreshEntity(sword);
        
        // 设置冷却时间
        summonCooldown = COOLDOWN_TICKS;
        updateState(player);
        notifyEchoClocks(player);
        // 发送消息
        if (isFree) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念之力引导，七黑剑无偿现世..."));
        } else {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...七黑剑应召而至..."));
        }
    }
} 