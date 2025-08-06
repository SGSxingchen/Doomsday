package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.config.EchoConfig;

public class LiXiEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.LIXI;
    
    private long cooldownEndTime = 0;
    private boolean isCharging = false;
    private int chargingTicks = 0;
    private BlockPos targetBlockPos = null;
    private long lastChargingTick = 0;

    public LiXiEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            EchoConfig.LIXI_SANITY_COST.get(),
            0
        );
    }

    private boolean isBlockUnbreakable(Block block) {
        String blockId = ForgeRegistries.BLOCKS.getKey(block).toString();
        return EchoConfig.LIXI_UNBREAKABLE_BLOCKS.get().contains(blockId);
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...我听到了，离析的回响..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 处理充能状态
        if (isCharging) {
            long currentTick = player.level().getGameTime();
            
            // 检查是否被打断（移动、受伤等）
            if (isChargingInterrupted(player, currentTick)) {
                resetCharging(player);
                return;
            }
            
            // 检查目标方块是否仍然有效
            if (targetBlockPos == null || !isTargetBlockValid(player)) {
                resetCharging(player);
                return;
            }
            
            chargingTicks++;
            lastChargingTick = currentTick;
            
            // 每秒显示一次进度提示
            if (chargingTicks % 20 == 0) {
                int secondsRemaining = (EchoConfig.LIXI_CHARGING_DURATION_TICKS.get() - chargingTicks) / 20;
                player.sendSystemMessage(Component.literal("§e[十日终焉] §f离析充能中... " + secondsRemaining + "秒"));
            }
            
            // 生成充能粒子效果
            if (chargingTicks % 5 == 0) {
                spawnChargingParticles(player);
            }
            
            // 充能完成
            if (chargingTicks >= EchoConfig.LIXI_CHARGING_DURATION_TICKS.get()) {
                executeDisintegration(player);
                resetCharging(player);
            }
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...离析的回响渐渐消散..."));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...离析之力只能主动使用..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        long timeMs = cooldownEndTime - System.currentTimeMillis();
        if (timeMs > 0) {
            long remainingSeconds = timeMs / 20 / 50;
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...离析之力尚需" + remainingSeconds + "秒恢复..."));
            return false;
        }

        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = faith >= EchoConfig.LIXI_MIN_FAITH_REQUIREMENT.get() && currentSanity < EchoConfig.LIXI_FREE_COST_THRESHOLD.get();

        // 如果不是免费释放，检查理智是否足够
        if (!freeCost && currentSanity < EchoConfig.LIXI_SANITY_COST.get()) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以引动离析之力..."));
            return false;
        }

        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        if (isCharging) {
            // 如果正在充能，停止充能
            resetCharging(player);
            return;
        }

        // 检查玩家指向的目标
        int faith = SanityManager.getFaith(player);
        double reach = faith >= EchoConfig.LIXI_MIN_FAITH_REQUIREMENT.get() ? EchoConfig.LIXI_HIGH_REACH.get() : 
                      (faith >= EchoConfig.LIXI_MID_FAITH.get() ? EchoConfig.LIXI_MID_REACH.get() : EchoConfig.LIXI_BASE_REACH.get());
        
        HitResult hitResult = player.pick(reach, 0.0F, false);
        
        // 如果击中生物，直接执行攻击
        if (hitResult instanceof EntityHitResult entityHit) {
            if (entityHit.getEntity() instanceof LivingEntity target && target != player) {
                executeEntityAttack(player, target);
                return;
            }
        }
        
        // 如果击中方块，开始充能
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = null;
            if (hitResult instanceof BlockHitResult) {
                pos = ((BlockHitResult) hitResult).getBlockPos();
            }
            ServerLevel level = (ServerLevel) player.level();
            
            // 检查方块是否可以被破坏
            Block targetBlock = null;
            if (pos != null) {
                targetBlock = level.getBlockState(pos).getBlock();
            }
            if (isBlockUnbreakable(targetBlock)) {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...此方块无法被离析之力影响..."));
                return;
            }

            // 开始充能
            startCharging(player, pos);
            return;
        }
        
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...未找到目标..."));
    }
    
    private void startCharging(ServerPlayer player, BlockPos targetPos) {
        isCharging = true;
        chargingTicks = 0;
        targetBlockPos = targetPos;
        lastChargingTick = player.level().getGameTime();
        int chargingSeconds = EchoConfig.LIXI_CHARGING_DURATION_TICKS.get() / 20;
        player.sendSystemMessage(Component.literal("§e[十日终焉] §f开始离析...保持" + chargingSeconds + "秒"));
    }
    
    private void resetCharging(ServerPlayer player) {
        if (isCharging) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f离析被打断"));
        }
        isCharging = false;
        chargingTicks = 0;
        targetBlockPos = null;
        lastChargingTick = 0;
    }
    
    // 公共方法供事件处理器调用来打断充能
    public void interruptCharging(ServerPlayer player) {
        resetCharging(player);
    }
    
    // 检查是否正在充能
    public boolean isCharging() {
        return isCharging;
    }
    
    private boolean isChargingInterrupted(ServerPlayer player, long currentTick) {
        // 伤害检测已移至事件处理器，此处仅保留其他打断条件
        return false;
    }
    
    private boolean isTargetBlockValid(ServerPlayer player) {
        if (targetBlockPos == null) return false;
        
        int faith = SanityManager.getFaith(player);
        double reach = faith >= EchoConfig.LIXI_MIN_FAITH_REQUIREMENT.get() ? EchoConfig.LIXI_HIGH_REACH.get() : 
                      (faith >= EchoConfig.LIXI_MID_FAITH.get() ? EchoConfig.LIXI_MID_REACH.get() : EchoConfig.LIXI_BASE_REACH.get());
        
        HitResult hitResult = player.pick(reach, 0.0F, false);
        
        // 检查是否仍然指向同一个方块
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos currentPos = ((BlockHitResult) hitResult).getBlockPos();
            return currentPos.equals(targetBlockPos);
        }
        
        return false;
    }
    
    private void spawnChargingParticles(ServerPlayer player) {
        if (targetBlockPos == null) return;
        
        ServerLevel level = (ServerLevel) player.level();
        
        // 在目标方块周围生成粒子
        for (int i = 0; i < 3; i++) {
            double offsetX = player.getRandom().nextGaussian() * 0.2;
            double offsetY = player.getRandom().nextGaussian() * 0.2;
            double offsetZ = player.getRandom().nextGaussian() * 0.2;
            
            level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.WITCH,
                targetBlockPos.getX() + 0.5 + offsetX,
                targetBlockPos.getY() + 0.5 + offsetY,
                targetBlockPos.getZ() + 0.5 + offsetZ,
                1, 0, 0, 0, 0.01
            );
        }
    }
    
    private void executeEntityAttack(ServerPlayer player, LivingEntity target) {
        int faith = SanityManager.getFaith(player);
        ServerLevel level = (ServerLevel) player.level();
        
        // 计算基础伤害
        float damage = 0;
        if (faith >= EchoConfig.LIXI_DAMAGE_FAITH.get()) {
            damage = EchoConfig.LIXI_BASE_DAMAGE.get().floatValue() + faith * EchoConfig.LIXI_DAMAGE_PER_FAITH.get().floatValue();
        }
        
        // 对直接目标造成伤害
        if (damage > 0) {
            target.hurt(target.damageSources().magic(), damage * EchoConfig.LIXI_DIRECT_TARGET_DAMAGE_MULTIPLIER.get().floatValue());
            
            // 如果目标是玩家，发送消息提示
            if (target instanceof ServerPlayer targetPlayer) {
                targetPlayer.sendSystemMessage(Component.literal("§c[十日终焉] §f...你被离析之力击中..."));
            }
        }
        
        // 生成集中的粒子效果
        for (int i = 0; i < 30; i++) {
            double offsetX = player.getRandom().nextGaussian() * 0.3;
            double offsetY = player.getRandom().nextGaussian() * 0.3;
            double offsetZ = player.getRandom().nextGaussian() * 0.3;
            
            level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.REVERSE_PORTAL,
                target.getX() + offsetX,
                target.getY() + 1.0 + offsetY,
                target.getZ() + offsetZ,
                1, 0, 0, 0, 0.1
            );
        }
        
        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        boolean freeCost = faith >= EchoConfig.LIXI_MIN_FAITH_REQUIREMENT.get() && currentSanity < EchoConfig.LIXI_FREE_COST_THRESHOLD.get();
        
        // 消耗理智
        if (!freeCost) {
            int actualCost = faith >= EchoConfig.LIXI_MID_FAITH.get() ? EchoConfig.LIXI_SANITY_COST.get() / 2 : EchoConfig.LIXI_SANITY_COST.get();
            SanityManager.modifySanity(player, -actualCost);
            String faithLevel = faith >= EchoConfig.LIXI_MIN_FAITH_REQUIREMENT.get() ? "坚定" : (faith >= EchoConfig.LIXI_MID_FAITH.get() ? "稳固" : "微弱");
            String damageText = damage > 0 ? String.format("(%.1f伤害)", damage * EchoConfig.LIXI_DIRECT_TARGET_DAMAGE_MULTIPLIER.get().floatValue()) : "";
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + actualCost + "点心神，离析之力" + damageText + "(" + faithLevel + ")已生效..."));
        } else {
            String faithLevel = faith >= EchoConfig.LIXI_MIN_FAITH_REQUIREMENT.get() ? "坚定" : (faith >= EchoConfig.LIXI_MID_FAITH.get() ? "稳固" : "微弱");
            String damageText = damage > 0 ? String.format("(%.1f伤害)", damage * EchoConfig.LIXI_DIRECT_TARGET_DAMAGE_MULTIPLIER.get().floatValue()) : "";
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念引导，离析之力" + damageText + "(" + faithLevel + ")已生效..."));
        }
        
        // 设置冷却
        long cooldown = damage > 0 ? EchoConfig.LIXI_DAMAGE_COOLDOWN_TICKS.get() : EchoConfig.LIXI_BASE_COOLDOWN_TICKS.get();
        if (faith >= EchoConfig.LIXI_MID_FAITH.get()) {
            cooldown = cooldown / 2;
        }
        cooldownEndTime = System.currentTimeMillis() + (cooldown * 50);
        updateState(player);
        notifyEchoClocks(player);
    }
    
    private void executeDisintegration(ServerPlayer player) {
        if (targetBlockPos == null) return;
        
        int faith = SanityManager.getFaith(player);
        ServerLevel level = (ServerLevel) player.level();
        
        // 计算基础伤害
        float damage = 0;
        if (faith >= EchoConfig.LIXI_DAMAGE_FAITH.get()) {
            damage = EchoConfig.LIXI_BASE_DAMAGE.get().floatValue() + faith * EchoConfig.LIXI_DAMAGE_PER_FAITH.get().floatValue();
        }
        
        // 移除方块
        level.setBlock(targetBlockPos, Blocks.AIR.defaultBlockState(), 3);
        
        // 对范围内的生物造成伤害
        boolean causedDamage = false;
        if (damage > 0) {
            AABB damageBox = new AABB(targetBlockPos).inflate(EchoConfig.LIXI_DAMAGE_RADIUS.get());
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, damageBox)) {
                if (entity != player) {
                    entity.hurt(entity.damageSources().magic(), damage);
                    causedDamage = true;
                    // 如果目标是玩家，发送消息提示
                    if (entity instanceof ServerPlayer targetPlayer) {
                        targetPlayer.sendSystemMessage(Component.literal("§c[十日终焉] §f...你被离析之力击中..."));
                    }
                }
            }
        }
        
        // 生成完成粒子效果
        for (int i = 0; i < 30; i++) {
            double offsetX = player.getRandom().nextGaussian() * 0.7;
            double offsetY = player.getRandom().nextGaussian() * 0.7;
            double offsetZ = player.getRandom().nextGaussian() * 0.7;
            
            level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.REVERSE_PORTAL,
                targetBlockPos.getX() + 0.5 + offsetX,
                targetBlockPos.getY() + 0.5 + offsetY,
                targetBlockPos.getZ() + 0.5 + offsetZ,
                1, 0, 0, 0, 0.1
            );
        }
        
        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        boolean freeCost = faith >= EchoConfig.LIXI_MIN_FAITH_REQUIREMENT.get() && currentSanity < EchoConfig.LIXI_FREE_COST_THRESHOLD.get();
        
        // 消耗理智
        if (!freeCost) {
            int actualCost = faith >= EchoConfig.LIXI_MID_FAITH.get() ? EchoConfig.LIXI_SANITY_COST.get() / 2 : EchoConfig.LIXI_SANITY_COST.get();
            SanityManager.modifySanity(player, -actualCost);
            String faithLevel = faith >= EchoConfig.LIXI_MIN_FAITH_REQUIREMENT.get() ? "坚定" : (faith >= EchoConfig.LIXI_MID_FAITH.get() ? "稳固" : "微弱");
            String damageText = damage > 0 ? String.format("(%.1f伤害)", damage) : "";
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + actualCost + "点心神，离析之力" + damageText + "(" + faithLevel + ")已生效..."));
        } else {
            String faithLevel = faith >= EchoConfig.LIXI_MIN_FAITH_REQUIREMENT.get() ? "坚定" : (faith >= EchoConfig.LIXI_MID_FAITH.get() ? "稳固" : "微弱");
            String damageText = damage > 0 ? String.format("(%.1f伤害)", damage) : "";
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念引导，离析之力" + damageText + "(" + faithLevel + ")已生效..."));
        }
        
        // 设置冷却
        long cooldown = causedDamage ? EchoConfig.LIXI_DAMAGE_COOLDOWN_TICKS.get() : EchoConfig.LIXI_BASE_COOLDOWN_TICKS.get();
        if (faith >= EchoConfig.LIXI_MID_FAITH.get()) {
            cooldown = cooldown / 2;
        }
        cooldownEndTime = System.currentTimeMillis() + (cooldown * 50);
        updateState(player);
        notifyEchoClocks(player);
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("cooldownEndTime", cooldownEndTime);
        tag.putBoolean("isCharging", isCharging);
        tag.putInt("chargingTicks", chargingTicks);
        if (targetBlockPos != null) {
            tag.putLong("targetBlockPos", targetBlockPos.asLong());
        }
        tag.putLong("lastChargingTick", lastChargingTick);
        return tag;
    }

    public static LiXiEcho fromNBT(CompoundTag tag) {
        LiXiEcho echo = new LiXiEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.cooldownEndTime = tag.getLong("cooldownEndTime");
        echo.isCharging = tag.getBoolean("isCharging");
        echo.chargingTicks = tag.getInt("chargingTicks");
        if (tag.contains("targetBlockPos")) {
            echo.targetBlockPos = BlockPos.of(tag.getLong("targetBlockPos"));
        }
        echo.lastChargingTick = tag.getLong("lastChargingTick");
        return echo;
    }
} 