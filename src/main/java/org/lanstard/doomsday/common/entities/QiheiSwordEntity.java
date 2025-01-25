package org.lanstard.doomsday.common.entities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import org.jetbrains.annotations.NotNull;
import org.lanstard.doomsday.client.model.QiheiSwordModel;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.world.level.levelgen.Heightmap;
import software.bernie.geckolib.core.object.PlayState;
import net.minecraft.util.Mth;

import java.util.EnumSet;
import java.util.List;

public class QiheiSwordEntity extends FlyingMob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean isInvulnerable = false; // 默认为无敌状态
    private QiheiSwordModel.AnimationState currentAnimationState = QiheiSwordModel.AnimationState.IDLE_FLOAT;
    private int lifetimeCounter = 0;
    private static final int MAX_LIFETIME = 12000; // 10分钟 = 12000刻
    private float targetHeight = 7.5f; // 目标悬浮高度（5-10格之间）
    private Vec3 acceleration = Vec3.ZERO;
    private static final double MAX_SPEED = 1.2D;
    private static final double DRAG_FACTOR = 0.2D;
    private float targetYRot = 0.0f;
    private float targetXRot = 0.0f;

    public QiheiSwordEntity(EntityType<? extends FlyingMob> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 20, true); // 允许垂直移动
        this.setNoGravity(true); // 无重力
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingPathNavigation = new FlyingPathNavigation(this, level);
        flyingPathNavigation.setCanOpenDoors(false);
        flyingPathNavigation.setCanFloat(true);
        flyingPathNavigation.setCanPassDoors(true);
        return flyingPathNavigation;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return FlyingMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.8D)  // 提高基础移动速度
                .add(Attributes.FLYING_SPEED, 1.2D)    // 提高飞行速度
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new QiheiSwordAttackGoal(this));
        this.goalSelector.addGoal(2, new QiheiSwordRandomStrollGoal(this)); // 提高游荡优先级到最高
        this.goalSelector.addGoal(2, new QiheiSwordFloatGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true));
        
        // 设置无重力
        this.setNoGravity(true);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            // 根据当前状态更新动画
            if (getTarget() != null) {
                currentAnimationState = QiheiSwordModel.AnimationState.CHASE_FLOAT;
            } else {
                currentAnimationState = QiheiSwordModel.AnimationState.IDLE_FLOAT;
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public boolean isInvulnerable() {
        return this.isInvulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.isInvulnerable = invulnerable;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (super.doHurtTarget(target)) {
            // 击退效果
            if (target instanceof LivingEntity) {
                Vec3 knockback = this.position().subtract(target.position()).normalize().scale(1.0);
                target.push(knockback.x, 0.3, knockback.z);
            }
            return true;
        }
        return false;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void setDeltaMovement(@NotNull Vec3 motion) {
        super.setDeltaMovement(motion);
        // 同步运动数据包
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 38);
            for (ServerPlayer player : ((ServerLevel)this.level()).players()) {
                player.connection.send(new ClientboundSetEntityMotionPacket(this));
            }
        }
    }

    @Override
    public void addDeltaMovement(Vec3 acceleration) {
        this.acceleration = this.acceleration.add(acceleration);
    }

    private void updatePhysics() {
        // 应用当前加速度
        Vec3 motion = this.getDeltaMovement().add(this.acceleration);
        
        // 应用阻力
        motion = motion.scale(1.0D - DRAG_FACTOR);
        
        // 限制最大速度
        double speed = motion.length();
        if (speed > MAX_SPEED) {
            motion = motion.scale(MAX_SPEED / speed);
        }
        
        // 设置新的运动状态
        this.setDeltaMovement(motion);
        
        // 重置加速度
        this.acceleration = Vec3.ZERO;
    }

    @Override
    public void tick() {
        super.tick();
        
        // 生命周期计数
        if (++lifetimeCounter >= MAX_LIFETIME) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }

        // 更新物理
        updatePhysics();

        // 更新视角朝向
        LivingEntity target = this.getTarget();
        if (target != null) {
            // 计算到目标的向量
            double dx = target.getX() - this.getX();
            double dy = (target.getY() + target.getEyeHeight() / 2) - this.getY();
            double dz = target.getZ() - this.getZ();
            
            // 计算目标旋转角度
            this.targetYRot = (float) (Mth.atan2(dx, dz) * (180F / Math.PI));
            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
            this.targetXRot = (float) (Mth.atan2(dy, horizontalDistance) * (180F / Math.PI));
        } else {
            // 没有目标时，根据运动方向确定朝向
            Vec3 motion = this.getDeltaMovement();
            if (motion.horizontalDistanceSqr() > 0.0001D) {
                this.targetYRot = (float) (Mth.atan2(motion.x, motion.z) * (180F / Math.PI));
                double horizontalSpeed = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
                this.targetXRot = (float) (Mth.atan2(motion.y, horizontalSpeed) * (180F / Math.PI));
            }
        }

        // 平滑插值旋转
        float yRotDiff = Mth.wrapDegrees(this.targetYRot - this.getYRot());
        float xRotDiff = Mth.wrapDegrees(this.targetXRot - this.getXRot());
        
        // 使用更小的旋转速度使运动更平滑
        float rotationSpeed = 5.0f;
        this.setYRot(this.getYRot() + Mth.clamp(yRotDiff, -rotationSpeed, rotationSpeed));
        this.setXRot(this.getXRot() + Mth.clamp(xRotDiff, -rotationSpeed, rotationSpeed));
        
        // 同步旋转
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();

        // 更新动画状态
        updateAnimationState();

    }

    private void updateAnimationState() {
        // 只在非攻击状态下保持最小高度
        if (this.getY() < this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, this.blockPosition()).getY() + 5) {
            this.addDeltaMovement(new Vec3(0, 0.4, 0));
        }
    }

    public QiheiSwordModel.AnimationState getAnimationState() {
        return currentAnimationState;
    }

    // 自定义AI目标：悬浮行为
    private static class QiheiSwordFloatGoal extends Goal {
        private final QiheiSwordEntity sword;
        private static final double VERTICAL_DAMPING = 0.8D; // 垂直阻尼系数
        private static final double HEIGHT_CHANGE_THRESHOLD = 0.1D; // 高度变化阈值
        public QiheiSwordFloatGoal(QiheiSwordEntity sword) {
            this.sword = sword;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            if (sword.random.nextInt(200) == 0) { // 降低高度改变频率
                sword.targetHeight = 5.0f + sword.random.nextFloat() * 5.0f;
            }

            double currentHeight = sword.getY() - sword.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, sword.blockPosition()).getY();
            double heightDiff = sword.targetHeight - currentHeight;
            
            // 只有当高度差超过阈值时才调整
            if (Math.abs(heightDiff) > HEIGHT_CHANGE_THRESHOLD) {
                // 计算理想速度，使用更平滑的插值
                double verticalForce = Math.signum(heightDiff) * 0.015 * Math.min(Math.abs(heightDiff), 0.5);
                
                // 获取当前垂直速度
                Vec3 currentMotion = sword.getDeltaMovement();
                
                // 应用阻尼
                double dampedVerticalSpeed = currentMotion.y * VERTICAL_DAMPING;
                
                // 合并力和阻尼后的速度
                double finalVerticalForce = verticalForce + dampedVerticalSpeed;
                
                // 添加轻微的随机漂移，但幅度更小
                double driftX = (sword.random.nextDouble() - 0.5) * 0.005;
                double driftZ = (sword.random.nextDouble() - 0.5) * 0.005;
                
                sword.addDeltaMovement(new Vec3(driftX, finalVerticalForce - currentMotion.y, driftZ));
            }
        }
    }

    // 自定义AI目标：攻击行为
    private static class QiheiSwordAttackGoal extends Goal {
        private final QiheiSwordEntity sword;
        private int attackCooldown = 0;
        private static final int ATTACK_COOLDOWN = 100; // 5秒冷却时间
        private static final double CHASE_FORCE = 0.6D;
        private static final double ATTACK_FORCE = 1.2D;
        private static final double ATTACK_RANGE = 3.0D;
        private static final double DETECTION_RANGE = 32.0D;

        public QiheiSwordAttackGoal(QiheiSwordEntity sword) {
            this.sword = sword;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            // 检查是否在冷却中
            if (attackCooldown > 0) {
                attackCooldown--;
                return false;
            }
            
            // 寻找新目标
            if (sword.getTarget() == null || !sword.getTarget().isAlive()) {
                findNewTarget();
            }
            
            return sword.getTarget() != null;
        }

        private void findNewTarget() {
            // 获取范围内的所有可能目标
            AABB searchBox = sword.getBoundingBox().inflate(DETECTION_RANGE);
            List<LivingEntity> possibleTargets = sword.level().getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                e -> e != sword && sword.hasLineOfSight(e) && 
                    (!(e instanceof Player) || !((Player) e).isCreative())
            );
            
            // 如果有可能的目标，随机选择一个
            if (!possibleTargets.isEmpty()) {
                sword.setTarget(possibleTargets.get(sword.random.nextInt(possibleTargets.size())));
            }
        }

        @Override
        public void tick() {
            LivingEntity target = sword.getTarget();
            if (target == null || !target.isAlive()) {
                findNewTarget();
                return;
            }

            double distance = sword.distanceTo(target);
            
            if (distance <= ATTACK_RANGE) {
                // 进行攻击
                if (sword.doHurtTarget(target)) {
                    // 攻击成功，进入冷却并寻找新目标
                    attackCooldown = ATTACK_COOLDOWN;
                    findNewTarget();
                    
                    // 击退效果
                    Vec3 knockback = sword.position().subtract(target.position()).normalize().scale(ATTACK_FORCE);
                    target.push(knockback.x, 0.3, knockback.z);
                }
            } else {
                // 追击目标
                Vec3 toTarget = target.position().subtract(sword.position()).normalize();
                sword.addDeltaMovement(toTarget.scale(CHASE_FORCE));
            }
        }

        @Override
        public boolean canContinueToUse() {
            return sword.getTarget() != null && sword.getTarget().isAlive() && attackCooldown <= 0;
        }
    }

    // 自定义AI目标：随机巡逻
    private static class QiheiSwordRandomStrollGoal extends Goal {
        private final QiheiSwordEntity sword;
        private Vec3 target;
        private static final double STROLL_FORCE = 0.2D;  // 降低游荡速度从0.8到0.2
        private int strollDelay = 0;
        private int strollTime = 0;
        private static final int MAX_STROLL_TIME = 100;

        public QiheiSwordRandomStrollGoal(QiheiSwordEntity sword) {
            this.sword = sword;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (sword.getTarget() != null) {
                return false;
            }
            
            if (strollDelay > 0) {
                strollDelay--;
                return false;
            }
            
            return sword.random.nextInt(10) == 0;  // 增加触发概率
        }

        @Override
        public void start() {
            strollTime = 0;
            selectNewTarget();
        }

        private void selectNewTarget() {
            // 在更大范围内选择目标点
            double angle = sword.random.nextDouble() * Math.PI * 2;
            double distance = 12 + sword.random.nextDouble() * 20;  // 增加游荡范围
            double x = sword.getX() + Math.cos(angle) * distance;
            double z = sword.getZ() + Math.sin(angle) * distance;
            double y = sword.getY() + (sword.random.nextDouble() - 0.5) * 8;  // 增加垂直游荡范围
            
            // 确保Y坐标不会太低或太高
            double minY = sword.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, sword.blockPosition()).getY() + 5;
            y = Math.max(minY, Math.min(minY + 15, y));
            
            target = new Vec3(x, y, z);
        }

        @Override
        public void tick() {
            strollTime++;
            
            // 定期更换目标点
            if (strollTime >= MAX_STROLL_TIME) {
                selectNewTarget();
                strollTime = 0;
            }

            if (target != null) {
                Vec3 toTarget = target.subtract(sword.position());
                double distSqr = toTarget.lengthSqr();
                
                if (distSqr > 2.0) {  // 增加停止距离阈值
                    Vec3 force = toTarget.normalize().scale(STROLL_FORCE);
                    
                    // 添加更温和的随机性
                    force = force.add(
                        (sword.random.nextDouble() - 0.5) * 0.02,  // 从0.1降低到0.02
                        (sword.random.nextDouble() - 0.5) * 0.02,  // 从0.1降低到0.02
                        (sword.random.nextDouble() - 0.5) * 0.02   // 从0.1降低到0.02
                    );
                    
                    sword.addDeltaMovement(force);
                } else {
                    // 到达目标点后立即选择新目标
                    selectNewTarget();
                }
            }
        }

        @Override
        public void stop() {
            target = null;
            strollDelay = 10 + sword.random.nextInt(20);  // 减少游荡间隔
            strollTime = 0;
        }

        @Override
        public boolean canContinueToUse() {
            return sword.getTarget() == null;  // 只要没有攻击目标就继续游荡
        }
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }
} 