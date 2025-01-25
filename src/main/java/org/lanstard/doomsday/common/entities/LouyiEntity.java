package org.lanstard.doomsday.common.entities;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;
import org.lanstard.doomsday.common.items.ModItem;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.server.TickTask;
import java.util.EnumSet;
import java.util.Set;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import top.theillusivec4.curios.api.CuriosApi;
import org.lanstard.doomsday.common.items.DaoItem;
import org.lanstard.doomsday.common.items.EyeItem;
import net.minecraft.world.damagesource.DamageTypes;

public class LouyiEntity extends Monster implements GeoEntity, PlayerRideableJumping, Saddleable{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int jumpCooldown = 0;
    private int dashCooldown = 0;
    private boolean isAtJumpPeak = false;
    private Vec3 dashTarget = null;
    private boolean isAttacking = false;

    public LouyiEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)  // 较快的移动速度
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(Attributes.ATTACK_SPEED, 0.5D);    // 较慢的攻击速度
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new CollectEyeGoal(this));  // 提高回收眼球的优先级
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        
        // 追踪带有眼球的玩家或装备了道/眼球的玩家
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                player -> hasEyeItem((Player) player) || hasEquippedEyeOrDao((Player) player)));
        // 反击逻辑
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    private boolean hasEyeItem(Player player) {
        return player.getInventory().hasAnyOf(Set.of(
            ModItem.EYE.get()  // 只检测普通眼球
        ));
    }

    private boolean hasEquippedEyeOrDao(Player player) {
        return CuriosApi.getCuriosInventory(player)
            .map(handler -> handler.findFirstCurio(stack -> 
                !stack.isEmpty() && (stack.getItem() instanceof DaoItem || stack.getItem() instanceof EyeItem))
                .isPresent())
            .orElse(false);
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide) {
            // 更新冷却时间
            if (jumpCooldown > 0) jumpCooldown--;
            if (dashCooldown > 0) dashCooldown--;
            
            // 始终面向目标
            if (this.getTarget() != null && !this.isVehicle()) {  // 不被骑乘时才自动面向目标
                double dx = this.getTarget().getX() - this.getX();
                double dz = this.getTarget().getZ() - this.getZ();
                double dy = this.getTarget().getY() - this.getY();
                
                // 更新水平旋转
                this.setYRot((float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F);
                this.yRotO = this.getYRot();
                
                // 更新垂直旋转
                double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
                this.setXRot((float) -(Math.atan2(dy, horizontalDistance) * 180.0D / Math.PI));
                
                // 设置头部朝向
                this.setRot(this.getYRot(), this.getXRot());
            }
            
            // 检测是否到达跳跃最高点
            if (!this.onGround() && this.getDeltaMovement().y < 0 && !isAtJumpPeak && dashTarget != null) {
                // 在最高点时进行冲刺
                Vec3 dashVec = dashTarget.subtract(this.position()).normalize();
                this.setDeltaMovement(dashVec.x * 1.2, dashVec.y * 0.5, dashVec.z * 1.2);
                isAtJumpPeak = true;
            }
            
            // 随机冲刺
            if (this.getTarget() != null && dashCooldown <= 0 && this.random.nextInt(100) < 5) {
                Vec3 toTarget = this.getTarget().position().subtract(this.position()).normalize();
                this.setDeltaMovement(toTarget.x * 1.2, this.getDeltaMovement().y, toTarget.z * 1.2);
                dashCooldown = 40; // 2秒冷却
            }
            
            // 检测是否需要跳跃
            if (this.onGround() && jumpCooldown <= 0) {
                boolean shouldJump = false;
                Vec3 jumpDirection = Vec3.ZERO;
                double jumpHeight = 1.0;
                
                // 如果有目标，检查目标是否在高处
                if (this.getTarget() != null) {
                    double heightDiff = this.getTarget().getY() - this.getY();
                    double horizontalDist = Math.sqrt(this.distanceToSqr(this.getTarget())) - Math.abs(heightDiff);
                    
                    if (heightDiff > 1.0 && horizontalDist < 16.0) {  // 扩大检测范围
                        shouldJump = true;
                        // 根据高度差决定跳跃高度
                        if (heightDiff > 30.0) {
                            jumpHeight = 2.5; // 跳35格左右
                        } else {
                            jumpHeight = Math.min(2.0, heightDiff / 5.0 + 0.4); // 比目标高2-3格
                        }
                        
                        // 计算跳跃方向
                        jumpDirection = new Vec3(
                            this.getTarget().getX() - this.getX(),
                            0,
                            this.getTarget().getZ() - this.getZ()
                        ).normalize();
                        
                        // 保存目标位置用于冲刺
                        dashTarget = this.getTarget().position();
                    }
                }
                
                // 检查前方障碍物
                if (this.horizontalCollision) {
                    Vec3 lookVec = Vec3.directionFromRotation(0, this.getYRot());
                    boolean frontBlock = this.level().getBlockState(this.blockPosition().relative(getDirection())).isSolid();
                    
                    if (frontBlock) {
                        shouldJump = true;
                        jumpDirection = lookVec;
                        jumpHeight = 1.2;
                        dashTarget = null;
                    }
                }
                
                // 执行跳跃
                if (shouldJump && jumpDirection != Vec3.ZERO) {
                    this.setDeltaMovement(this.getDeltaMovement().add(
                        jumpDirection.x * 0.6,    // 更大的水平推力
                        jumpHeight,               // 动态跳跃高度
                        jumpDirection.z * 0.6
                    ));
                    jumpCooldown = 20;  // 1秒跳跃冷却
                    isAtJumpPeak = false;
                }
            }
            
            // 重置跳跃相关状态
            if (this.onGround()) {
                isAtJumpPeak = false;
                dashTarget = null;
            }
        }
    }

    // 获取实体面朝的方向
    public @NotNull Direction getDirection() {
        float yaw = this.getYRot() % 360;
        if (yaw < 0) yaw += 360;
        
        if (yaw >= 315 || yaw < 45) return Direction.SOUTH;
        if (yaw >= 45 && yaw < 135) return Direction.WEST;
        if (yaw >= 135 && yaw < 225) return Direction.NORTH;
        return Direction.EAST;
    }

    @Override
    public boolean onClimbable() {
        return super.onClimbable() || this.horizontalCollision || 
               this.level().getBlockState(this.blockPosition().above()).isFaceSturdy(this.level(), this.blockPosition().above(), Direction.DOWN);
    }

    public boolean canBeControlledByRider() {
        return true;
    }

    @Override
    public void travel(Vec3 pos) {
        if (this.isAlive()) {
            if (this.isVehicle() && this.canBeControlledByRider()) {
                LivingEntity passenger = (LivingEntity) this.getControllingPassenger();
                if (passenger != null) {
                    this.setYRot(passenger.getYRot());
                    this.yRotO = this.getYRot();
                    this.setXRot(passenger.getXRot() * 0.5F);
                    this.setRot(this.getYRot(), this.getXRot());
                    
                    float forward = passenger.zza;
                    float strafe = passenger.xxa;
                    
                    if (forward <= 0.0F) {
                        forward *= 0.25F;
                    }

                    this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.1F);
                    super.travel(new Vec3(strafe, pos.y, forward));
                    return;
                }
            }
            super.travel(pos);
        }
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        return entity instanceof LivingEntity ? (LivingEntity)entity : null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 0, this::moveController));
        controllers.add(new AnimationController<>(this, "attack", 0, this::attackController));
    }

    private PlayState moveController(AnimationState<LouyiEntity> event) {
        if (this.isVehicle()) {
            return PlayState.STOP;
        }
        
        if (event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().then("WALK", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    private PlayState attackController(AnimationState<LouyiEntity> event) {
        if (this.isAttacking) {
            event.getController().setAnimation(RawAnimation.begin().then("gongji", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (super.doHurtTarget(target)) {
            this.isAttacking = true;
            // 0.28秒后重置攻击状态（与动画时长匹配）
            this.level().getServer().tell(new TickTask(6, () -> this.isAttacking = false));
            return true;
        }
        return false;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void onPlayerJump(int jumpPower) {
        if (jumpPower > 0) {
            Vec3 motion = this.getDeltaMovement();
            // 增加骑乘时的跳跃高度
            this.setDeltaMovement(motion.x, 0.8D * (jumpPower / 100.0D), motion.z);
        }
    }

    @Override
    public boolean canJump() {
        return true;
    }

    @Override
    public void handleStartJump(int jumpPower) {
        // 处理开始跳跃
    }

    @Override
    public void handleStopJump() {
        // 处理结束跳跃
    }

    // 回收眼球的AI目标
    private static class CollectEyeGoal extends Goal {
        private final LouyiEntity louyi;
        private ItemEntity targetItem;
        private final double speed;
        private final float searchRange = 8.0F;

        public CollectEyeGoal(LouyiEntity entity) {
            this.louyi = entity;
            this.speed = 1.2D;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            AABB searchBox = this.louyi.getBoundingBox().inflate(searchRange, 3.0D, searchRange);
            List<ItemEntity> items = this.louyi.level().getEntitiesOfClass(
                ItemEntity.class,
                searchBox,
                item -> !item.isRemoved() && (item.getItem().is(ModItem.EYE.get()) || item.getItem().is(ModItem.MOLDY_EYE.get()))
            );
            
            if (!items.isEmpty()) {
                targetItem = items.get(0);
                // 找到眼球后清除所有仇恨目标
                this.louyi.setTarget(null);
                return true;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return targetItem != null && !targetItem.isRemoved() && 
                   this.louyi.distanceToSqr(targetItem) > 1.0D;
        }

        @Override
        public void tick() {
            if (targetItem != null && !targetItem.isRemoved()) {
                this.louyi.getNavigation().moveTo(targetItem, this.speed);
                
                // 如果足够近，回收物品
                if (this.louyi.distanceToSqr(targetItem) < 2.0D) {
                    targetItem.discard();  // 移除物品
                    // 清除仇恨目标
                    this.louyi.setTarget(null);
                }
            }
        }

        @Override
        public void stop() {
            targetItem = null;
            this.louyi.getNavigation().stop();
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int lootingLevel, boolean hitByPlayer) {
        super.dropCustomDeathLoot(damageSource, lootingLevel, hitByPlayer);
        
        // 掉落2-4个道
        Random random = new Random();
        int count = 2 + random.nextInt(3); // 生成2到4的随机数
        
        for (int i = 0; i < count; i++) {
            this.spawnAtLocation(ModItem.DAO.get());
        }
    }

    @Override
    public boolean isSaddleable() {
        return true;
    }

    @Override
    public void equipSaddle(@Nullable SoundSource sound) {
        // 不需要鞍具
    }

    @Override
    public boolean isSaddled() {
        return true;
    }

    @Override
    public void setDeltaMovement(@NotNull Vec3 motion) {
        super.setDeltaMovement(motion);
        // 同步运动数据包
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 38);  // 发送运动更新事件
            for (ServerPlayer player : ((ServerLevel)this.level()).players()) {
                player.connection.send(new ClientboundSetEntityMotionPacket(this));
            }
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource.is(DamageTypes.FALL) || super.isInvulnerableTo(damageSource);
    }
} 