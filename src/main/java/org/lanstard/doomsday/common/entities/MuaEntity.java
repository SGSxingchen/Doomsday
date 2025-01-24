package org.lanstard.doomsday.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class MuaEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    public MuaEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setNoAi(true); // 禁用AI，使其不会移动
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1.0D) // 1点生命值
                .add(Attributes.MOVEMENT_SPEED, 0.0D) // 不移动
                .add(Attributes.ATTACK_DAMAGE, 0.0D); // 无伤害
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 只受远程伤害和指令伤害
        if (source.isIndirect() || source.getMsgId().equals("outOfWorld")) {
            return super.hurt(source, amount);
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide) {
            // 检查owner是否存在
            if (this.ownerUUID == null) {
                this.discard();
                return;
            }

            // 获取3x3范围内的所有生物
            AABB box = this.getBoundingBox().inflate(3.0D);
            List<LivingEntity> nearbyEntities = this.level().getEntitiesOfClass(LivingEntity.class, box);

            for (LivingEntity entity : nearbyEntities) {
                // 跳过自己和主人
                if (entity == this || (this.ownerUUID != null && this.ownerUUID.equals(entity.getUUID()))) {
                    continue;
                }

                // 给予缓慢X效果
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 9, false, false));
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
        }
    }

    public void setOwner(LivingEntity owner) {
        this.cachedOwner = owner;
        this.ownerUUID = owner != null ? owner.getUUID() : null;
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        }

        if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.cachedOwner = (LivingEntity) entity;
                return this.cachedOwner;
            }
        }
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 由于是静止的，不需要添加动画控制器
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
} 