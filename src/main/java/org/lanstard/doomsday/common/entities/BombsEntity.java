package org.lanstard.doomsday.common.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.ClipContext;
import org.lanstard.doomsday.common.effects.ModEffects;
import org.lanstard.doomsday.common.items.ItemRegister;

public class BombsEntity extends ThrowableItemProjectile {
    private int ticksToExplode = 100; // 5秒 = 100刻

    public BombsEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public BombsEntity(Level level, LivingEntity owner) {
        super(ModEntities.BOMBS.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ItemRegister.BOMBS.get();
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!level().isClientSide) {
            ticksToExplode--;
            if (ticksToExplode <= 0) {
                explode();
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            explode();
        }
    }

    private void explode() {
        // 创建爆炸和粒子效果
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLASH, 
                this.getX(), this.getY(), this.getZ(),
                50, 0.5, 0.5, 0.5, 0.1);
                
            // 对范围内的实体造成效果
            level().getEntities(this, getBoundingBox().inflate(32.0D), 
                entity -> entity instanceof LivingEntity)
                .forEach(entity -> {
                    if (entity instanceof LivingEntity living) {
                        // 检查是否在视野中
                        Vec3 viewVector = living.getViewVector(1.0F);
                        Vec3 toEntity = new Vec3(
                            this.getX() - living.getX(),
                            this.getY() - living.getEyeY(),
                            this.getZ() - living.getZ()
                        ).normalize();
                        
                        // 如果点积大于0，说明在视野中
                        if (viewVector.dot(toEntity) > 0) {
                            // 检查是否有方块遮挡
                            Vec3 entityEyes = new Vec3(living.getX(), living.getEyeY(), living.getZ());
                            Vec3 bombPos = this.position();
                            
                            // 进行射线检测
                            BlockHitResult clip = level().clip(new ClipContext(
                                entityEyes,
                                bombPos,
                                ClipContext.Block.COLLIDER,
                                ClipContext.Fluid.NONE,
                                living
                            ));
                            
                            // 如果射线没有被方块阻挡（击中了爆炸点）
                            if (clip.getType() == HitResult.Type.MISS || 
                                (clip.getLocation().distanceToSqr(bombPos) < 0.1)) {
                                living.addEffect(new MobEffectInstance(ModEffects.FLASH.get(), 20*11, 0));
                                living.hurt(level().damageSources().thrown(this, this.getOwner()), 2.0F);
                            }
                        }
                    }
                });
        }
        this.discard();
    }
} 