package org.lanstard.doomsday.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.EnumSet;

public class PuppetEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    public PuppetEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.4D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        
        // 只有在没有攻击目标时才会跟随主人
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false) {
            @Override
            public boolean canUse() {
                return PuppetEntity.this.getTarget() == null && super.canUse();
            }
        });
        
        // 添加反击目标，优先级为2
        this.targetSelector.addGoal(2, new PuppetRevengeGoal(this));
        // 同步主人的攻击目标，最高优先级
        this.targetSelector.addGoal(1, new CopyOwnerTargetGoal(this));
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
            // 如果找不到实体，返回null但不清除UUID
            return null;
        }
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        // 只有在ownerUUID为null时才消失，这样即使主人离线，只要有UUID记录就不会消失
        if (!this.level().isClientSide && this.ownerUUID == null) {
            this.discard();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 0, this::moveController));
    }

    private PlayState moveController(software.bernie.geckolib.core.animation.AnimationState<PuppetEntity> event) {
        if (event.isMoving()) {
            // 移动时播放行走动画
            event.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        
        // 站立时不播放动画
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // 跟随主人的AI目标
    private static class FollowOwnerGoal extends Goal {
        private final PuppetEntity puppet;
        private final double speedModifier;
        private final float stopDistance;
        private final float startDistance;
        private final boolean canFly;
        private LivingEntity owner;
        private int timeToRecalcPath;

        public FollowOwnerGoal(PuppetEntity puppet, double speed, float startDist, float stopDist, boolean canFly) {
            this.puppet = puppet;
            this.speedModifier = speed;
            this.startDistance = startDist;
            this.stopDistance = stopDist;
            this.canFly = canFly;
        }

        @Override
        public boolean canUse() {
            LivingEntity owner = this.puppet.getOwner();
            if (owner == null) {
                return false;
            } else if (owner.isSpectator()) {
                return false;
            } else if (this.puppet.distanceToSqr(owner) < (double)(this.startDistance * this.startDistance)) {
                return false;
            } else {
                this.owner = owner;
                return true;
            }
        }

        @Override
        public boolean canContinueToUse() {
            if (this.puppet.getNavigation().isDone()) {
                return false;
            } else {
                return !(this.puppet.distanceToSqr(this.owner) <= (double)(this.stopDistance * this.stopDistance));
            }
        }

        @Override
        public void start() {
            this.timeToRecalcPath = 0;
        }

        @Override
        public void stop() {
            this.owner = null;
            this.puppet.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.puppet.getLookControl().setLookAt(this.owner, 10.0F, (float)this.puppet.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                this.puppet.getNavigation().moveTo(this.owner, this.speedModifier);
            }
        }
    }

    // 复制主人的目标的AI目标
    private static class CopyOwnerTargetGoal extends TargetGoal {
        private final PuppetEntity puppet;
        private LivingEntity ownerLastHurt;
        private int timestamp;

        public CopyOwnerTargetGoal(PuppetEntity puppet) {
            super(puppet, false);
            this.puppet = puppet;
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            LivingEntity owner = this.puppet.getOwner();
            if (owner == null) {
                return false;
            }

            this.ownerLastHurt = owner.getLastHurtMob();
            int i = owner.getLastHurtMobTimestamp();
            
            // 如果时间戳相同，说明还是同一个目标，继续追击
            if (i == this.timestamp) {
                return false;
            }
            
            // 如果没有新目标，保持当前目标
            if (this.ownerLastHurt == null) {
                return false;
            }

            // 检查新目标是否有效
            if (!this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT)) {
                return false;
            }

            // 更新时间戳并设置新目标
            this.timestamp = i;
            return true;
        }

        @Override
        public void start() {
            this.mob.setTarget(this.ownerLastHurt);
            super.start();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.mob.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            
            // 如果主人有了新的攻击目标，停止当前追击
            LivingEntity owner = this.puppet.getOwner();
            if (owner != null && owner.getLastHurtMob() != null && owner.getLastHurtMob() != target) {
                return false;
            }
            
            return true;
        }
    }

    // 傀儡的反击AI目标
    private static class PuppetRevengeGoal extends TargetGoal {
        private final PuppetEntity puppet;
        private int lastHurtByMobTimestamp;

        public PuppetRevengeGoal(PuppetEntity puppet) {
            super(puppet, true);
            this.puppet = puppet;
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            int timestamp = this.puppet.getLastHurtByMobTimestamp();
            LivingEntity attacker = this.puppet.getLastHurtByMob();

            // 如果时间戳相同或没有攻击者，不启用反击
            if (timestamp == this.lastHurtByMobTimestamp || attacker == null) {
                return false;
            }

            // 不对主人进行反击
            if (attacker == this.puppet.getOwner()) {
                return false;
            }

            // 如果主人已经有了攻击目标，优先同步主人的目标而不是反击
            LivingEntity owner = this.puppet.getOwner();
            if (owner != null && owner.getLastHurtMob() != null) {
                return false;
            }

            this.lastHurtByMobTimestamp = timestamp;
            return this.canAttack(attacker, TargetingConditions.DEFAULT);
        }

        @Override
        public void start() {
            this.mob.setTarget(this.puppet.getLastHurtByMob());
            super.start();
        }
    }
} 