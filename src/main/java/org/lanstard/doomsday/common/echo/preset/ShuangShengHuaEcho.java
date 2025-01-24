package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.joml.Vector3f;

import java.util.*;

public class ShuangShengHuaEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.SHUANGSHENGHUA;
    private static final int PASSIVE_RANGE = 2;                    // 被动效果范围
    private static final int PASSIVE_SYNC_CD = 20;                // 被动同步CD（1秒）
    private static final int CONTINUOUS_SANITY_COST = 2;          // 每秒持续消耗
    private static final int TOGGLE_SANITY_COST = 30;             // 开启消耗
    private static final int ACTIVE_SANITY_COST = 50;             // 主动技能消耗
    private static final int ACTIVE_DURATION = 12000;             // 主动效果持续时间（10分钟）
    private static final int ACTIVE_COOLDOWN = 12000;             // 主动技能冷却时间（10分钟）
    private static final int DAMAGE_SHARE_CD = 10;                // 伤害分摊CD（0.5秒）
    private static final int LOW_SANITY_THRESHOLD = 200;          // 低理智阈值
    private static final int FREE_COST_THRESHOLD = 300;          // 免费释放阈值
    
    // 粒子效果相关
    private static final float PINK_RED = 1.0F;
    private static final float PINK_GREEN = 0.5F;
    private static final float PINK_BLUE = 0.8F;
    private static final float PARTICLE_SIZE = 1.0F;
    
    private int tickCounter = 0;                                  // tick计数器
    private int passiveCooldown = 0;                             // 被动效果CD
    private int activeCooldown = 0;                              // 主动技能CD
    private int damageShareCooldown = 0;                         // 伤害分摊CD
    private UUID linkedTarget = null;                            // 主动技能链接的目标
    private long linkEndTime = 0;                                // 链接结束时间
    
    public ShuangShengHuaEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            ACTIVE_SANITY_COST,
            CONTINUOUS_SANITY_COST
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...双生之花绽放，生命交织..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        
        // 更新CD
        if (passiveCooldown > 0) passiveCooldown--;
        if (activeCooldown > 0) activeCooldown--;
        if (damageShareCooldown > 0) damageShareCooldown--;
        
        if (!isActive()) return;
        // 每秒触发一次效果
        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            
            // 检查理智值和信念值
            int currentSanity = SanityManager.getSanity(player);
            int faith = SanityManager.getFaith(player);
            
            // 如果信念大于等于10点且理智低于300，则不消耗理智
            boolean freeCost = faith >= 10 && currentSanity < FREE_COST_THRESHOLD;
            
            // 如果不是免费释放且理智不足，则关闭效果
            if (!freeCost && currentSanity < CONTINUOUS_SANITY_COST) {
                setActiveAndUpdate(player, false);
                onDeactivate(player);
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神已竭，双花凋零..."));
                return;
            }
            
            // 如果不是免费释放，消耗理智值
            if (!freeCost) {
                SanityManager.modifySanity(player, -CONTINUOUS_SANITY_COST);
            }
            
            // 添加粒子效果
            if (player.level() instanceof ServerLevel serverLevel) {
                Vec3 pos = player.position();
                DustParticleOptions pinkParticle = new DustParticleOptions(
                    new Vector3f(PINK_RED, PINK_GREEN, PINK_BLUE), 
                    PARTICLE_SIZE
                );
                
                for (int i = 0; i < 8; i++) {
                    double offsetX = (player.getRandom().nextDouble() - 0.5) * 2;
                    double offsetY = player.getRandom().nextDouble() * 2;
                    double offsetZ = (player.getRandom().nextDouble() - 0.5) * 2;
                    
                    serverLevel.sendParticles(pinkParticle,
                        pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                        1, 0, 0, 0, 0);
                }
            }
            
            // 应用被动效果
            if (passiveCooldown <= 0) {
                applyPassiveEffect(player);
                passiveCooldown = PASSIVE_SYNC_CD;
            }
        }
        
        // 检查链接是否结束
        if (linkedTarget != null && System.currentTimeMillis() > linkEndTime) {
            unlinkTarget(player);
            updateState(player); // 更新状态
        }
    }

    private void applyPassiveEffect(ServerPlayer player) {
        AABB box = player.getBoundingBox().inflate(PASSIVE_RANGE);
        List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(
            LivingEntity.class, 
            box,
            entity -> entity != player && entity.isAlive()
        );
        
        float playerHealth = player.getHealth();
        for (LivingEntity target : nearbyEntities) {
            if (target == player) continue;
            target.setHealth(playerHealth);
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        unlinkTarget(player);
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...双花凋零，生命分离..."));
    }

    private void unlinkTarget(ServerPlayer player) {
        if (linkedTarget != null) {
            ServerPlayer target = player.getServer().getPlayerList().getPlayer(linkedTarget);
            if (target != null) {
                target.sendSystemMessage(Component.literal("§b[十日终焉] §f...双生之链断开，生命不再相连..."));
            }
            linkedTarget = null;
            linkEndTime = 0;
            updateState(player); // 更新状态
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...双生之链断开，生命不再相连..."));
        }
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        int currentSanity = SanityManager.getSanity(player);
        
        // 检查是否被禁用
        if (this.isDisabled()) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...仙法暂失其效，难以施为..."));
            return false;
        }
        
        // 检查理智值
        if (currentSanity < ACTIVE_SANITY_COST) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...心神不足，难以施展仙法..."));
            return false;
        }
        
        // 检查冷却
        if (activeCooldown > 0 && currentSanity >= LOW_SANITY_THRESHOLD) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...双生之力尚需积蓄..."));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 获取目标
        LivingEntity target = getTarget(player);
        if (target == null) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...双生之花无处可寄..."));
            return;
        }
        
        // 建立链接
        if (target instanceof ServerPlayer targetPlayer) {
            // 检查理智值和信念值
            int currentSanity = SanityManager.getSanity(player);
            int faith = SanityManager.getFaith(player);
            
            // 如果信念大于等于10点且理智低于300，则不消耗理智
            boolean freeCost = faith >= 10 && currentSanity < FREE_COST_THRESHOLD;
            
            // 如果不是免费释放，消耗理智值
            if (!freeCost) {
                SanityManager.modifySanity(player, -ACTIVE_SANITY_COST);
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + ACTIVE_SANITY_COST + "点心神，施展仙法..."));
            } else {
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...双生花开，同生共死..."));
            }
            
            linkedTarget = targetPlayer.getUUID();
            linkEndTime = System.currentTimeMillis() + ACTIVE_DURATION * 50;
            updateState(player); // 更新状态
            
            // 设置冷却（如果理智值不低）
            if (SanityManager.getSanity(player) >= LOW_SANITY_THRESHOLD) {
                activeCooldown = ACTIVE_COOLDOWN;
            }
            
            // 发送提示
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...双生之链建立，与")
                .append(targetPlayer.getDisplayName())
                .append(Component.literal("§f生命相连...")));
            targetPlayer.sendSystemMessage(Component.literal("§b[十日终焉] §f...你与")
                .append(player.getDisplayName())
                .append(Component.literal("§f建立了生命链接...")));
            
            // 生成链接粒子效果
            spawnLinkParticles(player, targetPlayer);
        } else {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...双生之花只能与玩家建立链接..."));
        }
    }
    
    private LivingEntity getTarget(ServerPlayer player) {
        // 获取玩家视线方向的射线
        double reachDistance = 20.0;
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        Vec3 endPos = eyePosition.add(
            lookVector.x * reachDistance,
            lookVector.y * reachDistance,
            lookVector.z * reachDistance
        );
        
        // 创建射线的碰撞箱
        AABB searchBox = player.getBoundingBox().inflate(reachDistance);
        
        // 搜索玩家
        List<ServerPlayer> players = player.level().getEntitiesOfClass(
            ServerPlayer.class,
            searchBox,
            target -> target != player && target.isAlive()
        );
        
        // 寻找最近的目标
        LivingEntity nearestTarget = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (ServerPlayer target : players) {
            AABB targetBox = target.getBoundingBox();
            Optional<Vec3> hitResult = targetBox.clip(eyePosition, endPos);
            
            if (hitResult.isPresent()) {
                double distance = eyePosition.distanceTo(hitResult.get());
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestTarget = target;
                }
            }
        }
        
        return nearestTarget;
    }
    
    private void spawnLinkParticles(ServerPlayer player, LivingEntity target) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        
        Vec3 start = player.getEyePosition();
        Vec3 end = target.position().add(0, target.getBbHeight() * 0.5, 0);
        Vec3 direction = end.subtract(start);
        double distance = direction.length();
        direction = direction.normalize();
        
        DustParticleOptions pinkParticle = new DustParticleOptions(
            new Vector3f(PINK_RED, PINK_GREEN, PINK_BLUE),
            PARTICLE_SIZE * 0.5F
        );
        
        // 在两点之间生成粒子线
        for (double d = 0; d < distance; d += 0.5) {
            double x = start.x + direction.x * d;
            double y = start.y + direction.y * d;
            double z = start.z + direction.z * d;
            
            serverLevel.sendParticles(pinkParticle, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        if (!isActive()) {
            // 检查理智值和信念值
            int currentSanity = SanityManager.getSanity(player);
            int faith = SanityManager.getFaith(player);
            
            // 如果信念大于等于10点且理智低于300，则不消耗理智
            boolean freeCost = faith >= 10 && currentSanity < FREE_COST_THRESHOLD;
            
            // 如果不是免费释放且理智不足，则无法开启
            if (!freeCost && currentSanity < TOGGLE_SANITY_COST) {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以施展双生之法..."));
                return;
            }
            
            // 如果不是免费释放，消耗理智
            if (!freeCost) {
                SanityManager.modifySanity(player, -TOGGLE_SANITY_COST);
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + TOGGLE_SANITY_COST + "点心神，施展双生之法..."));
            } else {
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念引导，双生花开..."));
            }
            
            setActiveAndUpdate(player, true);
            onActivate(player);
        } else {
            setActiveAndUpdate(player, false);
            onDeactivate(player);
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putInt("tickCounter", tickCounter);
        tag.putInt("passiveCooldown", passiveCooldown);
        tag.putInt("activeCooldown", activeCooldown);
        tag.putInt("damageShareCooldown", damageShareCooldown);
        if (linkedTarget != null) {
            tag.putUUID("linkedTarget", linkedTarget);
            tag.putLong("linkEndTime", linkEndTime);
        }
        return tag;
    }
    
    public static ShuangShengHuaEcho fromNBT(CompoundTag tag) {
        ShuangShengHuaEcho echo = new ShuangShengHuaEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.tickCounter = tag.getInt("tickCounter");
        echo.passiveCooldown = tag.getInt("passiveCooldown");
        echo.activeCooldown = tag.getInt("activeCooldown");
        echo.damageShareCooldown = tag.getInt("damageShareCooldown");
        if (tag.contains("linkedTarget")) {
            echo.linkedTarget = tag.getUUID("linkedTarget");
            echo.linkEndTime = tag.getLong("linkEndTime");
        }
        return echo;
    }
    
    // 检查是否与指定玩家建立链接
    public boolean isLinkedWith(ServerPlayer player) {
        return linkedTarget != null && linkedTarget.equals(player.getUUID());
    }
    
    // 处理伤害分摊
    public void onDamage(ServerPlayer player, float amount, boolean isDamageOwner) {
        if (linkedTarget == null) return;
        
        // 检查伤害分摊CD
        if (damageShareCooldown > 0) return;
        damageShareCooldown = DAMAGE_SHARE_CD;
        
        ServerPlayer target = player.getServer().getPlayerList().getPlayer(linkedTarget);
        if (target == null || !target.isAlive()) return;
        
        // 分摊伤害
        float sharedDamage = amount / 2;
        
        if (isDamageOwner) {
            // 如果是回响拥有者受伤
            player.hurt(player.damageSources().generic(), sharedDamage);
            target.hurt(player.damageSources().generic(), sharedDamage);
            
            // 发送提示
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...双生之链分摊了你受到的伤害..."));
            target.sendSystemMessage(Component.literal("§b[十日终焉] §f...双生之链与你分担了伤害..."));
        } else {
            // 如果是链接目标受伤
            target.hurt(target.damageSources().generic(), sharedDamage);
            player.hurt(target.damageSources().generic(), sharedDamage);
            
            // 发送提示
            target.sendSystemMessage(Component.literal("§b[十日终焉] §f...双生之链分摊了你受到的伤害..."));
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...双生之链与你分担了伤害..."));
        }
    }
} 