package org.lanstard.doomsday.common.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PuppetEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private LivingEntity owner;
    private LivingEntity target;

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
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, net.minecraft.world.entity.player.Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
        this.setTarget(target);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.target != null && !this.target.isAlive()) {
            this.setTarget(null);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<PuppetEntity> state) {
        if (state.isMoving()) {
            state.setAnimation(RawAnimation.begin().thenLoop("animation.zombie.walk"));
            return PlayState.CONTINUE;
        }

        state.setAnimation(RawAnimation.begin().thenLoop("animation.zombie.idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
} 