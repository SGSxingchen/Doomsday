package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;

public class LiXiEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.LIXI;
    private static final int SANITY_COST = 10;               // 理智消耗
    private static final int BASE_COOL_DOWN = 5 * 1;         // 基础冷却0.25秒
    private static final int DAMAGE_COOL_DOWN = 8 * 20;      // 伤害冷却8秒
    private static final int FREE_COST_THRESHOLD = 300;      // 免费释放阈值
    private static final int MIN_FAITH_REQUIREMENT = 10;     // 最低信念要求
    private static final int MID_FAITH = 5;                  // 中等信念要求
    private static final int DAMAGE_FAITH = 8;               // 伤害所需信念
    private static final double BASE_REACH = 32.0D;          // 基础距离
    private static final double MID_REACH = 30.0D;           // 中等信念距离
    private static final double HIGH_REACH = 64.0D;          // 高等信念距离
    private static final float BASE_DAMAGE = 8.0f;           // 基础伤害
    private static final float DAMAGE_PER_FAITH = 1.0f;      // 每点信念增加的伤害
    private static final double DAMAGE_RADIUS = 5.0D;        // 伤害范围
    private static final float DIRECT_TARGET_DAMAGE_MULTIPLIER = 1.2f; // 直接目标伤害倍率
    
    private long cooldownEndTime = 0;

    public LiXiEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            SANITY_COST,  // 主动技能消耗
            0            // 无被动消耗
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...我听到了，离析的回响..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 这是一个主动技能，不需要更新逻辑
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
        boolean freeCost = faith >= MIN_FAITH_REQUIREMENT && currentSanity < FREE_COST_THRESHOLD;

        // 如果不是免费释放，检查理智是否足够
        if (!freeCost && currentSanity < SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以引动离析之力..."));
            return false;
        }

        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 检查玩家指向的目标
        int faith = SanityManager.getFaith(player);
        double reach = faith >= MIN_FAITH_REQUIREMENT ? HIGH_REACH : 
                      (faith >= MID_FAITH ? MID_REACH : BASE_REACH);
        
        HitResult hitResult = player.pick(reach, 0.0F, false);
        
        // 计算基础伤害
        float damage = 0;
        if (faith >= DAMAGE_FAITH) {
            damage = BASE_DAMAGE + faith * DAMAGE_PER_FAITH;
        }

        ServerLevel level = (ServerLevel) player.level();
        boolean targetHit = false;
        boolean causedDamage = false;

        // 检查是否击中实体
        if (hitResult instanceof EntityHitResult entityHit) {
            if (entityHit.getEntity() instanceof LivingEntity target && target != player) {
                targetHit = true;
                // 对直接目标造成伤害
                if (damage > 0) {
                    target.hurt(target.damageSources().magic(), damage * DIRECT_TARGET_DAMAGE_MULTIPLIER);
                    causedDamage = true;
                    
                    // 如果目标是玩家，发送消息提示
                    if (target instanceof ServerPlayer targetPlayer) {
                        targetPlayer.sendSystemMessage(Component.literal("§c[十日终焉] §f...你被离析之力击中..."));
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
                }
            }
        } 
        // 如果没有击中实体，检查是否击中方块
        else if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
            
            // 检查方块是否可以被破坏
            if (level.getBlockState(pos).getBlock() != Blocks.BEDROCK) {
                // 移除方块
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                targetHit = true;

                // 对范围内的生物造成伤害
                if (damage > 0) {
                    AABB damageBox = new AABB(pos).inflate(DAMAGE_RADIUS);
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

                // 生成粒子效果
                for (int i = 0; i < 20; i++) {
                    double offsetX = player.getRandom().nextGaussian() * 0.5;
                    double offsetY = player.getRandom().nextGaussian() * 0.5;
                    double offsetZ = player.getRandom().nextGaussian() * 0.5;
                    
                    level.sendParticles(
                        net.minecraft.core.particles.ParticleTypes.REVERSE_PORTAL,
                        pos.getX() + 0.5 + offsetX,
                        pos.getY() + 0.5 + offsetY,
                        pos.getZ() + 0.5 + offsetZ,
                        1, 0, 0, 0, 0.1
                    );
                }
            } else {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...此方块无法被离析之力影响..."));
                return;
            }
        }

        if (!targetHit) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...未找到目标..."));
            return;
        }

        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        boolean freeCost = faith >= MIN_FAITH_REQUIREMENT && currentSanity < FREE_COST_THRESHOLD;

        // 消耗理智
        if (!freeCost) {
            int actualCost = faith >= MID_FAITH ? SANITY_COST / 2 : SANITY_COST;
            SanityManager.modifySanity(player, -actualCost);
            String faithLevel = faith >= MIN_FAITH_REQUIREMENT ? "坚定" : (faith >= MID_FAITH ? "稳固" : "微弱");
            String damageText = damage > 0 ? String.format("(%.1f伤害)", damage) : "";
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + actualCost + "点心神，离析之力" + damageText + "(" + faithLevel + ")已生效..."));
        } else {
            String faithLevel = faith >= MIN_FAITH_REQUIREMENT ? "坚定" : (faith >= MID_FAITH ? "稳固" : "微弱");
            String damageText = damage > 0 ? String.format("(%.1f伤害)", damage) : "";
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念引导，离析之力" + damageText + "(" + faithLevel + ")已生效..."));
        }

        // 设置冷却
        long cooldown = causedDamage ? DAMAGE_COOL_DOWN : BASE_COOL_DOWN;
        if (faith >= MID_FAITH) {
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
        return tag;
    }

    public static LiXiEcho fromNBT(CompoundTag tag) {
        LiXiEcho echo = new LiXiEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.cooldownEndTime = tag.getLong("cooldownEndTime");
        return echo;
    }
} 