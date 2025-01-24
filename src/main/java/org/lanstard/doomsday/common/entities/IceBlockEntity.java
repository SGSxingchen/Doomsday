package org.lanstard.doomsday.common.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;

public class IceBlockEntity extends ThrowableItemProjectile {
    private static final float DAMAGE = 8.0f;
    private static final float SPLIT_DAMAGE = 4.0f;
    private static final float SPLIT_SPEED_MULTIPLIER = 1.3f;
    private int splitCount = 0;  // 分裂次数计数
    private static final int MAX_SPLIT = 2;  // 最大分裂次数

    public IceBlockEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
        this.noPhysics = true;  // 设置无重力
    }

    public IceBlockEntity(Level level, LivingEntity owner) {
        super(ModEntities.ICE_BLOCK.get(), owner, level);
        this.noPhysics = true;  // 设置无重力
    }

    public IceBlockEntity(Level level, LivingEntity owner, int splitCount) {
        this(level, owner);
        this.splitCount = splitCount;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.BLUE_ICE;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide && result.getEntity() instanceof LivingEntity target) {
            // 根据是否为分裂箭决定伤害
            float damage = splitCount > 0 ? SPLIT_DAMAGE : DAMAGE;
            target.hurt(damageSources().freeze(), damage);
            
            // 生成冰冻粒子效果
            if (level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                    getX(), getY(), getZ(),
                    20,  // 粒子数量
                    0.5, 0.5, 0.5,  // 扩散范围
                    0.1  // 速度
                );
            }
            
            discard();  // 命中后消失
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!level().isClientSide && splitCount < MAX_SPLIT) {
            Direction hitFace = result.getDirection();
            Vec3 motion = getDeltaMovement();
            Vec3 normal = Vec3.atLowerCornerOf(hitFace.getNormal());
            
            // 计算反射向量
            Vec3 reflection = motion.subtract(normal.scale(2.0D * motion.dot(normal)));
            
            // 创建一个与法线垂直的平面上的基向量
            Vec3 tangent;
            if (hitFace.getAxis() == Direction.Axis.Y) {
                tangent = new Vec3(1, 0, 0);
            } else {
                tangent = new Vec3(0, 1, 0);
            }
            Vec3 bitangent = normal.cross(tangent);
            tangent = bitangent.cross(normal).normalize();
            
            // 在切平面上创建三个分裂箭
            for (int i = 0; i < 3; i++) {
                IceBlockEntity splitArrow = new IceBlockEntity(level(), getOwner() instanceof LivingEntity ? (LivingEntity)getOwner() : null, splitCount + 1);
                splitArrow.setPos(getX(), getY(), getZ());
                
                // 计算分散角度（-60度、0度、60度）
                double angle = Math.PI * 2 * (i - 1) / 3;  // 将360度均分为3份
                
                // 计算在切平面上的分裂方向
                Vec3 splitDirection = tangent.scale(Math.cos(angle)).add(bitangent.scale(Math.sin(angle)));
                
                // 将反射向量投影到切平面上
                Vec3 projectedReflection = reflection.subtract(normal.scale(reflection.dot(normal)));
                
                // 混合反射方向和分裂方向
                double reflectionWeight = Math.abs(motion.dot(normal)) / motion.length();  // 入射角的余弦值
                Vec3 finalDirection = projectedReflection.normalize()
                    .scale(reflectionWeight)
                    .add(splitDirection.scale(1 - reflectionWeight))
                    .normalize();
                
                // 设置新的速度（增加1.3倍）
                Vec3 newMotion = finalDirection.scale(motion.length() * SPLIT_SPEED_MULTIPLIER);
                splitArrow.setDeltaMovement(newMotion);
                
                level().addFreshEntity(splitArrow);
            }
            
            // 生成冰冻粒子效果
            if (level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                    getX(), getY(), getZ(),
                    30,  // 粒子数量
                    0.5, 0.5, 0.5,  // 扩散范围
                    0.1  // 速度
                );
            }
        }
        
        discard();  // 原箭消失
    }
} 