package org.lanstard.doomsday.common.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ShenJunEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private float scaleMultiplier = 1.0f;
    private boolean isWarCrying = false;

    public ShenJunEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!this.level().isClientSide) {
            startWarCry();
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 15.0D)
                .add(Attributes.ARMOR, 4.0D);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 0, this::moveController));
        controllers.add(new AnimationController<>(this, "warcry", 0, this::warCryController));
    }

    private PlayState moveController(AnimationState<ShenJunEntity> event) {
        // 默认播放悬浮动画
        event.getController().setAnimation(RawAnimation.begin().then("Stand", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private PlayState warCryController(AnimationState<ShenJunEntity> event) {
        if (this.isWarCrying) {
            event.getController().setAnimation(RawAnimation.begin().then("Warcry", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    // 触发战吼动画
    public void startWarCry() {
        this.isWarCrying = true;
        // 2.08秒后重置状态（与动画时长匹配）
        if (!this.level().isClientSide) {
            this.level().getServer().tell(new net.minecraft.server.TickTask(42, () -> this.isWarCrying = false));
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // 设置实体缩放比例
    public void setScale(float scale) {
        this.scaleMultiplier = scale;
        this.refreshDimensions();
    }

    // 获取当前缩放比例并更新实体碰撞箱大小
    @Override
    public float getScale() {
        return this.scaleMultiplier;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("scale", scaleMultiplier);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("scale")) {
            setScale(tag.getFloat("scale"));
        }
    }
} 