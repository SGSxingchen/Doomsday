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
import net.minecraft.nbt.CompoundTag;
import org.lanstard.doomsday.common.effects.ModEffects;
import org.lanstard.doomsday.common.items.ModItem;

public class BombsEntity extends ThrowableItemProjectile {
    private static final float BASE_DAMAGE = 8.0F;           // 基础伤害
    private static final float MID_DAMAGE = 12.0F;          // 中等强化伤害
    private static final float HIGH_DAMAGE = 16.0F;         // 高等强化伤害
    private static final int BASE_DURATION = 20 * 11;       // 基础持续时间
    private static final int MID_DURATION = 20 * 15;        // 中等强化持续时间
    private static final int HIGH_DURATION = 20 * 20;       // 高等强化持续时间
    
    private int ticksToExplode = 100; // 5秒 = 100刻
    private int enhancedLevel = 0;    // 0=普通, 1=中等强化, 2=高等强化

    public BombsEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public BombsEntity(Level level, LivingEntity owner) {
        super(ModEntities.BOMBS.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItem.BOMBS.get();
    }

    public void setEnhanced(int level) {
        this.enhancedLevel = level;
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!level().isClientSide) {
            ticksToExplode--;
            if (ticksToExplode <= 0) {
                explode();
            }
            
            // 根据强化等级产生不同的粒子效果
            if (level() instanceof ServerLevel serverLevel) {
                int particleCount = enhancedLevel == 2 ? 3 : (enhancedLevel == 1 ? 2 : 1);
                for (int i = 0; i < particleCount; i++) {
                    serverLevel.sendParticles(ParticleTypes.END_ROD,
                        this.getX() + (random.nextDouble() - 0.5) * 0.5,
                        this.getY() + (random.nextDouble() - 0.5) * 0.5,
                        this.getZ() + (random.nextDouble() - 0.5) * 0.5,
                        1, 0.0, 0.0, 0.0, 0.0);
                }
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
            // 根据强化等级调整粒子效果
            int particleCount = enhancedLevel == 2 ? 100 : (enhancedLevel == 1 ? 75 : 50);
            float spread = enhancedLevel == 2 ? 0.7F : (enhancedLevel == 1 ? 0.6F : 0.5F);
            
            serverLevel.sendParticles(ParticleTypes.FLASH, 
                this.getX(), this.getY(), this.getZ(),
                particleCount, spread, spread, spread, 0.1);
                
            // 对范围内的实体造成效果
            double range = enhancedLevel == 2 ? 40.0D : (enhancedLevel == 1 ? 36.0D : 32.0D);
            level().getEntities(this, getBoundingBox().inflate(range), 
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
                                // 根据强化等级调整效果
                                int duration = enhancedLevel == 2 ? HIGH_DURATION : 
                                             (enhancedLevel == 1 ? MID_DURATION : BASE_DURATION);
                                float damage = enhancedLevel == 2 ? HIGH_DAMAGE :
                                             (enhancedLevel == 1 ? MID_DAMAGE : BASE_DAMAGE);
                                
                                living.addEffect(new MobEffectInstance(ModEffects.FLASH.get(), duration, 4));
                                if (enhancedLevel >= 1) {
                                    // 中等强化及以上增加缓慢效果
                                    living.addEffect(new MobEffectInstance(
                                        net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN,
                                        duration / 2,
                                        enhancedLevel - 1
                                    ));
                                }
                                living.hurt(level().damageSources().thrown(this, this.getOwner()), damage);
                            }
                        }
                    }
                });
        }
        this.discard();
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("EnhancedLevel", this.enhancedLevel);
        tag.putInt("TicksToExplode", this.ticksToExplode);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.enhancedLevel = tag.getInt("EnhancedLevel");
        this.ticksToExplode = tag.getInt("TicksToExplode");
    }
} 