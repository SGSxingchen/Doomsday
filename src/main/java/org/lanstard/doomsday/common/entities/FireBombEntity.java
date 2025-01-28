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

import java.util.List;

public class FireBombEntity extends ThrowableItemProjectile {
    private static final float BASE_MAX_DAMAGE = 10.0f;      // 基础最大伤害
    private static final float BASE_MIN_DAMAGE = 4.0f;       // 基础最小伤害
    private static final float MID_MAX_DAMAGE = 15.0f;       // 中等强化最大伤害
    private static final float MID_MIN_DAMAGE = 6.0f;        // 中等强化最小伤害
    private static final float HIGH_MAX_DAMAGE = 20.0f;      // 高等强化最大伤害
    private static final float HIGH_MIN_DAMAGE = 8.0f;       // 高等强化最小伤害
    private static final float BASE_RADIUS = 4.0f;           // 基础爆炸半径
    private static final float MID_RADIUS = 5.0f;            // 中等强化爆炸半径
    private static final float HIGH_RADIUS = 6.0f;           // 高等强化爆炸半径
    
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
            float radius = enhancedLevel == 2 ? HIGH_RADIUS : (enhancedLevel == 1 ? MID_RADIUS : BASE_RADIUS);
            
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
        float radius = enhancedLevel == 2 ? HIGH_RADIUS : (enhancedLevel == 1 ? MID_RADIUS : BASE_RADIUS);
        List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class,
            getBoundingBox().inflate(radius));

        float maxDamage = enhancedLevel == 2 ? HIGH_MAX_DAMAGE : (enhancedLevel == 1 ? MID_MAX_DAMAGE : BASE_MAX_DAMAGE);
        float minDamage = enhancedLevel == 2 ? HIGH_MIN_DAMAGE : (enhancedLevel == 1 ? MID_MIN_DAMAGE : BASE_MIN_DAMAGE);

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
                    int fireDuration = enhancedLevel == 2 ? 10 : 5; // 高等强化10秒,中等强化5秒
                    entity.setSecondsOnFire(fireDuration);
                    
                    if (enhancedLevel == 2) {
                        // 高等强化额外造成虚弱效果
                        entity.addEffect(new MobEffectInstance(
                            MobEffects.WEAKNESS,
                            100,  // 5秒
                            0    // 等级I
                        ));
                    }
                }
            }
        }

        // 移除实体
        discard();
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