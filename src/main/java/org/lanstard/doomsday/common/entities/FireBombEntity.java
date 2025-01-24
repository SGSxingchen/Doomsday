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
import org.lanstard.doomsday.common.items.ModItem;

import java.util.List;

public class FireBombEntity extends ThrowableItemProjectile {
    private static final float MAX_DAMAGE = 10.0f;
    private static final float MIN_DAMAGE = 4.0f;
    private static final float EXPLOSION_RADIUS = 4.0f;

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
            serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                location.x, location.y, location.z,
                20, // 粒子数量
                EXPLOSION_RADIUS/2, EXPLOSION_RADIUS/2, EXPLOSION_RADIUS/2, // 扩散范围
                0.1 // 速度
            );
            
            // 生成白色粒子
            serverLevel.sendParticles(ParticleTypes.CLOUD,
                location.x, location.y, location.z,
                50, // 粒子数量
                EXPLOSION_RADIUS/2, EXPLOSION_RADIUS/2, EXPLOSION_RADIUS/2, // 扩散范围
                0.1 // 速度
            );
        }

        // 对范围内的生物造成伤害
        List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class,
            getBoundingBox().inflate(EXPLOSION_RADIUS));

        for (LivingEntity entity : entities) {
            // 计算与爆炸中心的距离
            double distance = entity.position().distanceTo(location);
            
            if (distance <= EXPLOSION_RADIUS) {
                // 根据距离计算伤害值
                float damage = MAX_DAMAGE - (MAX_DAMAGE - MIN_DAMAGE) * (float)(distance / EXPLOSION_RADIUS);
                
                // 造成魔法伤害
                entity.hurt(level().damageSources().magic(), damage);
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
    }
} 