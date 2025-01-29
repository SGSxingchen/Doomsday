package org.lanstard.doomsday.common.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.lanstard.doomsday.common.items.ModItem;
import org.lanstard.doomsday.config.EchoConfig;

import java.util.List;

public class FireBombEntity extends ThrowableItemProjectile {
    private int enhancedLevel = 0;    // 0=普通, 1=中等强化, 2=高等强化

    public FireBombEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public FireBombEntity(Level level, LivingEntity owner) {
        super(ModEntities.FIRE_BOMB.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItem.FIRE_BOMB.get();
    }
    
    public void setEnhanced(int level) {
        this.enhancedLevel = level;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        if (!level().isClientSide) {
            explode(hitResult.getLocation());
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        if (!level().isClientSide) {
            explode(hitResult.getLocation());
        }
    }

    private void explode(Vec3 location) {
        // 生成爆炸粒子效果
        if (level() instanceof ServerLevel serverLevel) {
            // 根据强化等级调整粒子效果
            int explosionParticles = enhancedLevel == 2 ? 40 : (enhancedLevel == 1 ? 30 : 20);
            int cloudParticles = enhancedLevel == 2 ? 100 : (enhancedLevel == 1 ? 75 : 50);
            float radius = getRadius();
            
            // 爆炸粒子
            serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                location.x, location.y, location.z,
                explosionParticles,
                radius/2, radius/2, radius/2,
                0.1
            );
            
            // 白色粒子
            serverLevel.sendParticles(ParticleTypes.CLOUD,
                location.x, location.y, location.z,
                cloudParticles,
                radius/2, radius/2, radius/2,
                0.1
            );
            
            // 强化等级≥1时添加火焰粒子
            if (enhancedLevel >= 1) {
                serverLevel.sendParticles(ParticleTypes.FLAME,
                    location.x, location.y, location.z,
                    enhancedLevel == 2 ? 60 : 40,
                    radius/2, radius/2, radius/2,
                    0.1
                );
            }
        }

        // 对范围内的生物造成伤害
        float radius = getRadius();
        List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class,
            getBoundingBox().inflate(radius));

        float maxDamage = getMaxDamage();
        float minDamage = getMinDamage();

        for (LivingEntity entity : entities) {
            // 计算与爆炸中心的距离
            double distance = entity.position().distanceTo(location);
            
            if (distance <= radius) {
                // 根据距离计算伤害值
                float damage = maxDamage - (maxDamage - minDamage) * (float)(distance / radius);
                
                // 造成魔法伤害
                entity.hurt(level().damageSources().magic(), damage);
                
                // 根据强化等级添加额外效果
                if (enhancedLevel >= 1) {
                    // 中等强化及以上造成燃烧
                    int fireDuration = enhancedLevel == 2 ? 
                        EchoConfig.FIRE_BOMB_HIGH_BURN_DURATION.get() : 
                        EchoConfig.FIRE_BOMB_MID_BURN_DURATION.get();
                    entity.setSecondsOnFire(fireDuration);
                    
                    if (enhancedLevel == 2) {
                        // 高等强化额外造成虚弱效果
                        entity.addEffect(new MobEffectInstance(
                            MobEffects.WEAKNESS,
                            EchoConfig.FIRE_BOMB_WEAKNESS_DURATION.get(),  // 虚弱持续时间
                            0    // 等级I
                        ));
                    }
                }
            }
        }

        // 移除实体
        discard();
    }

    private float getRadius() {
        return switch (enhancedLevel) {
            case 2 -> EchoConfig.FIRE_BOMB_HIGH_RADIUS.get().floatValue();
            case 1 -> EchoConfig.FIRE_BOMB_MID_RADIUS.get().floatValue();
            default -> EchoConfig.FIRE_BOMB_BASE_RADIUS.get().floatValue();
        };
    }

    private float getMaxDamage() {
        return switch (enhancedLevel) {
            case 2 -> EchoConfig.FIRE_BOMB_HIGH_MAX_DAMAGE.get().floatValue();
            case 1 -> EchoConfig.FIRE_BOMB_MID_MAX_DAMAGE.get().floatValue();
            default -> EchoConfig.FIRE_BOMB_BASE_MAX_DAMAGE.get().floatValue();
        };
    }

    private float getMinDamage() {
        return switch (enhancedLevel) {
            case 2 -> EchoConfig.FIRE_BOMB_HIGH_MIN_DAMAGE.get().floatValue();
            case 1 -> EchoConfig.FIRE_BOMB_MID_MIN_DAMAGE.get().floatValue();
            default -> EchoConfig.FIRE_BOMB_BASE_MIN_DAMAGE.get().floatValue();
        };
    }

    // 让实体始终面向玩家视角
    @Override
    public void tick() {
        super.tick();
        
        // 如果在客户端，更新实体的旋转
        if (level().isClientSide) {
            setYRot(-level().getGameTime() * 20); // 让实体旋转
        }
        
        // 在服务端产生飞行粒子效果
        if (!level().isClientSide && enhancedLevel > 0) {
            if (level() instanceof ServerLevel serverLevel) {
                int particleCount = enhancedLevel == 2 ? 2 : 1;
                for (int i = 0; i < particleCount; i++) {
                    serverLevel.sendParticles(ParticleTypes.FLAME,
                        this.getX() + (random.nextDouble() - 0.5) * 0.2,
                        this.getY() + (random.nextDouble() - 0.5) * 0.2,
                        this.getZ() + (random.nextDouble() - 0.5) * 0.2,
                        1, 0.0, 0.0, 0.0, 0.0);
                }
            }
        }
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("EnhancedLevel", this.enhancedLevel);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.enhancedLevel = tag.getInt("EnhancedLevel");
    }
} 