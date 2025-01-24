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
    private static final int CHECK_INTERVAL = 10 * 20;         // 检查间隔（10秒）
    private static final float BASE_SUCCESS_RATE = 0.05f;     // 基础成功率（5%）
    private static final float SUCCESS_RATE_INCREMENT = 0.01f; // 失败后成功率增加（1%）
    
    // 粒子效果相关
    private static final float DARK_RED = 0.5F;
    private static final float DARK_GREEN = 0.0F;
    private static final float DARK_BLUE = 0.0F;
    private static final float PARTICLE_SIZE = 1.0F;
    
    private int tickCounter = 0;
    private float currentSuccessRate = BASE_SUCCESS_RATE;
    private final Random random = new Random();

    public ZhaoZaiEcho() {
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
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...招灾之力缠绕，灾厄将至..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 每5秒检查一次
        tickCounter++;
        if (tickCounter >= CHECK_INTERVAL) {
            tickCounter = 0;
            
            // 检查昼夜状态
            Level level = player.level();
            long dayTime = level.getDayTime() % 24000;
            boolean currentIsNight = dayTime >= 13000 && dayTime < 23000;
            
            // 尝试触发效果
            if (random.nextFloat() < currentSuccessRate) {
                // 成功触发效果
                AABB box = player.getBoundingBox().inflate(RANGE);
                List<LivingEntity> nearbyPlayers = level.getEntitiesOfClass(LivingEntity.class, box);
                
                for (LivingEntity target : nearbyPlayers) {
                    if (target == player) {
                        continue;
                    }
                    if (currentIsNight) {
                        // 夜晚：凋零效果
                        target.addEffect(new MobEffectInstance(MobEffects.WITHER, WITHER_DURATION, WITHER_AMPLIFIER, false, true));
                        target.sendSystemMessage(Component.literal("§c[十日终焉] §f...夜色降临，灾厄缠身..."));
                    } else {
                        // 白天：发光效果
                        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOWING_DURATION, 0, false, true));
                        target.sendSystemMessage(Component.literal("§b[十日终焉] §f...白昼将至，灾厄显形..."));
                    }
                }
                
                // 生成粒子效果
                if (level instanceof ServerLevel serverLevel) {
                    spawnEffectParticles(serverLevel, player.position());
                }
                
                // 重置成功率
                currentSuccessRate = BASE_SUCCESS_RATE;
                
                // 显示触发信息
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...灾厄显现..."));
            } else {
                // 失败，增加下次成功率
                currentSuccessRate += SUCCESS_RATE_INCREMENT;
                
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
        return tag;
    }
    
    public static ZhaoZaiEcho fromNBT(CompoundTag tag) {
        ZhaoZaiEcho echo = new ZhaoZaiEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.tickCounter = tag.getInt("tickCounter");
        echo.currentSuccessRate = tag.getFloat("currentSuccessRate");
        return echo;
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 这是一个纯被动技能，不需要主动使用
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...招灾并非人为..."));
        return false;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 这是一个纯被动技能，不需要主动使用
    }
} 