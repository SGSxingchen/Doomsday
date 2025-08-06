package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.joml.Vector3f;
import net.minecraft.nbt.CompoundTag;

public class SuoQianShanEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.SUOQIANSHAN;
    private static final int TOGGLE_SANITY_COST = 50;             // 开启消耗
    private static final int CONTINUOUS_SANITY_COST = 1;          // 每秒持续消耗
    private static final int ACTIVE_SANITY_COST = 100;           // 主动技能消耗
    private static final int FREE_COST_THRESHOLD = 300;          // 免费释放阈值
    private static final int HIGH_FAITH = 10;                    // 高等信念要求
    private static final int COOL_DOWN_TICKS = 1200;            // 冷却时间1分钟
    
    // 效果等级
    private static final int SPEED_AMPLIFIER = 99;               // 速度100
    private static final int JUMP_AMPLIFIER = 4;                 // 跳跃5
    private static final int SLOW_FALLING_AMPLIFIER = 0;         // 缓降1
    
    // 粒子效果相关
    private static final float CYAN_RED = 0.0F;
    private static final float CYAN_GREEN = 0.8F;
    private static final float CYAN_BLUE = 0.8F;
    private static final float PARTICLE_SIZE = 1.0F;
    
    private int tickCounter = 0;
    private int cooldownTicks = 0;
    private Vec3 markedPosition = null;
    private String markedDimensionKey = null;
    
    public SuoQianShanEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            
            ACTIVE_SANITY_COST,
            CONTINUOUS_SANITY_COST
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...缩地成寸，千山可越..."));
        applyEffects(player);
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        if (!isActive()) return;
        
        // 更新冷却时间
        if (cooldownTicks > 0) {
            cooldownTicks--;
        }
        
        // 每秒触发一次效果
        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            
            // 检查理智值和信念值
            int currentSanity = SanityManager.getSanity(player);
            int faith = SanityManager.getFaith(player);
            
            // 如果信念大于等于10点且理智低于300，则不消耗理智
            boolean freeCost = faith >= HIGH_FAITH && currentSanity < FREE_COST_THRESHOLD;
            
            // 如果不是免费释放且理智不足，则关闭效果
            if (!freeCost && currentSanity < CONTINUOUS_SANITY_COST) {
                setActive(false);
                onDeactivate(player);
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神已竭，缩地之法难以为继..."));
                return;
            }
            
            // 如果不是免费释放，消耗理智值
            if (!freeCost) {
                SanityManager.modifySanity(player, -CONTINUOUS_SANITY_COST);
            }
            
            // 刷新效果
            applyEffects(player);
            
            // 添加粒子效果
            if (player.level() instanceof ServerLevel serverLevel) {
                spawnAuraParticles(serverLevel, player);
            }
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...缩地之法已解..."));
        removeEffects(player);
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查是否被禁用
        if (this.isDisabled()) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...缩地之法暂失其效..."));
            return false;
        }

        // 检查冷却时间
        if (cooldownTicks > 0) {
            int remainingSeconds = cooldownTicks / 20;
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...缩地之力尚需积蓄，剩余" + remainingSeconds + "秒..."));
            return false;
        }
        
        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        
        // 如果信念大于等于10点且理智低于300，则不消耗理智
        if (faith >= HIGH_FAITH && currentSanity < FREE_COST_THRESHOLD) {
            return true;
        }
        
        // 检查理智值
        if (currentSanity < ACTIVE_SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以施展缩地之法..."));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 检查玩家是否在潜行
        if (player.isShiftKeyDown() && markedPosition != null) {
            // 如果在潜行且有标记点，进行传送
            if (!player.level().dimension().location().toString().equals(markedDimensionKey)) {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...所处维度不同，无法传送..."));
                return;
            }
            
            // 检查理智值和信念值
            int currentSanity = SanityManager.getSanity(player);
            int faith = SanityManager.getFaith(player);
            
            // 如果信念大于等于10点且理智低于300，则不消耗理智
            boolean freeCost = faith >= HIGH_FAITH && currentSanity < FREE_COST_THRESHOLD;
            
            // 如果不是免费释放，消耗理智值
            if (!freeCost) {
                // 根据信念等级减少消耗
                int actualCost = faith >= HIGH_FAITH ? ACTIVE_SANITY_COST / 2 : ACTIVE_SANITY_COST;
                SanityManager.modifySanity(player, -actualCost);
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + actualCost + "点心神，施展缩地之法..."));
            } else {
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念引导，千山可达..."));
            }
            
            // 生成传送前粒子效果
            if (player.level() instanceof ServerLevel serverLevel) {
                spawnTeleportParticles(serverLevel, player.position());
            }
            
            // 传送玩家
            player.teleportTo(markedPosition.x, markedPosition.y, markedPosition.z);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...千山缩尽，刹那可至..."));
            
            // 生成传送后粒子效果
            if (player.level() instanceof ServerLevel serverLevel) {
                spawnTeleportParticles(serverLevel, markedPosition);
            }

            // 设置冷却时间
            cooldownTicks = faith >= HIGH_FAITH ? COOL_DOWN_TICKS / 2 : COOL_DOWN_TICKS;
            updateState(player);
            notifyEchoClocks(player);
        } else if (!player.isShiftKeyDown()) {
            // 如果不在潜行，标记当前位置
            markedPosition = player.position();
            markedDimensionKey = player.level().dimension().location().toString();
            
            // 保存状态
            updateState(player);
            
            // 生成标记点粒子效果
            if (player.level() instanceof ServerLevel serverLevel) {
                spawnMarkParticles(serverLevel, markedPosition);
            }
            
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...此处已记，伺机而返..."));
        }
    }

    private void applyEffects(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, SPEED_AMPLIFIER, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 40, JUMP_AMPLIFIER, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, SLOW_FALLING_AMPLIFIER, false, false));
    }

    private void removeEffects(ServerPlayer player) {
        player.removeEffect(MobEffects.MOVEMENT_SPEED);
        player.removeEffect(MobEffects.JUMP);
        player.removeEffect(MobEffects.SLOW_FALLING);
    }

    private void spawnAuraParticles(ServerLevel level, ServerPlayer player) {
        DustParticleOptions cyanParticle = new DustParticleOptions(
            new Vector3f(CYAN_RED, CYAN_GREEN, CYAN_BLUE),
            PARTICLE_SIZE * 0.5F
        );
        
        Vec3 pos = player.position();
        for (int i = 0; i < 5; i++) {
            double offsetX = (player.getRandom().nextDouble() - 0.5) * 0.5;
            double offsetY = player.getRandom().nextDouble() * 2;
            double offsetZ = (player.getRandom().nextDouble() - 0.5) * 0.5;
            
            level.sendParticles(cyanParticle,
                pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                1, 0, 0.05, 0, 0);
        }
    }

    private void spawnMarkParticles(ServerLevel level, Vec3 pos) {
        DustParticleOptions cyanParticle = new DustParticleOptions(
            new Vector3f(CYAN_RED, CYAN_GREEN, CYAN_BLUE),
            PARTICLE_SIZE
        );
        
        // 生成标记点的圆形粒子效果
        for (int i = 0; i < 16; i++) {
            double angle = 2.0 * Math.PI * i / 16;
            double radius = 0.5;
            double x = pos.x + radius * Math.cos(angle);
            double z = pos.z + radius * Math.sin(angle);
            
            level.sendParticles(cyanParticle,
                x, pos.y + 0.1, z,
                1, 0, 0.1, 0, 0);
        }
    }

    private void spawnTeleportParticles(ServerLevel level, Vec3 pos) {
        DustParticleOptions cyanParticle = new DustParticleOptions(
            new Vector3f(CYAN_RED, CYAN_GREEN, CYAN_BLUE),
            PARTICLE_SIZE
        );
        
        // 生成螺旋上升的粒子效果
        for (int i = 0; i < 20; i++) {
            double angle = i * Math.PI / 10;
            double radius = 0.5;
            double height = i * 0.1;
            double x = pos.x + radius * Math.cos(angle);
            double z = pos.z + radius * Math.sin(angle);
            
            level.sendParticles(cyanParticle,
                x, pos.y + height, z,
                1, 0, 0, 0, 0);
        }
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        if (!isActive()) {
            // 检查理智值和信念值
            int currentSanity = SanityManager.getSanity(player);
            int faith = SanityManager.getFaith(player);
            
            // 如果信念大于等于10点且理智低于300，则不消耗理智
            boolean freeCost = faith >= HIGH_FAITH && currentSanity < FREE_COST_THRESHOLD;
            
            // 如果不是免费释放且理智不足，则无法开启
            if (!freeCost && currentSanity < TOGGLE_SANITY_COST) {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以施展缩地之法..."));
                return;
            }
            
            // 如果不是免费释放，消耗理智
            if (!freeCost) {
                SanityManager.modifySanity(player, -TOGGLE_SANITY_COST);
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + TOGGLE_SANITY_COST + "点心神，施展缩地之法..."));
            } else {
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念引导，千山可达..."));
            }
            
            setActiveAndUpdate(player, true);
            onActivate(player);
        } else {
            // 直接关闭，不需要检查理智值
            setActiveAndUpdate(player, false);
            onDeactivate(player);
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putInt("tickCounter", tickCounter);
        tag.putInt("cooldownTicks", cooldownTicks);
        
        if (markedPosition != null) {
            tag.putDouble("markedX", markedPosition.x);
            tag.putDouble("markedY", markedPosition.y);
            tag.putDouble("markedZ", markedPosition.z);
            tag.putString("markedDimension", markedDimensionKey);
        }
        
        return tag;
    }
    
    public static SuoQianShanEcho fromNBT(CompoundTag tag) {
        SuoQianShanEcho echo = new SuoQianShanEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.tickCounter = tag.getInt("tickCounter");
        echo.cooldownTicks = tag.getInt("cooldownTicks");
        
        if (tag.contains("markedX")) {
            echo.markedPosition = new Vec3(
                tag.getDouble("markedX"),
                tag.getDouble("markedY"),
                tag.getDouble("markedZ")
            );
            echo.markedDimensionKey = tag.getString("markedDimension");
        }
        
        return echo;
    }
} 