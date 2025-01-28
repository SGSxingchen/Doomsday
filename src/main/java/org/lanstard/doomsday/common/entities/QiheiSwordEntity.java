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
import net.minecraft.core.BlockPos;

import java.util.EnumSet;
import java.util.List;
import java.util.Comparator;

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
        this.moveControl = new FlyingMoveControl(this, 10, false);
        this.setNoGravity(true);
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
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 1.0D)     // 从0.8提高到1.0
                .add(Attributes.FLYING_SPEED, 1.5D)       // 从1.2提高到1.5
                .add(Attributes.FOLLOW_RANGE, 48.0D);     // 从32提高到48，增加追踪范围
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new QiheiSwordAttackGoal(this));
        this.goalSelector.addGoal(2, new QiheiSwordRandomStrollGoal(this));
        this.goalSelector.addGoal(2, new QiheiSwordFloatGoal(this));
        
        // 设置无重力
        this.setNoGravity(true);
        
        // 添加目标选择器
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, 
            (entity) -> !(entity instanceof QiheiSwordEntity))); // 避免攻击同类
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
                Vec3 knockback = this.position().subtract(target.position()).normalize().scale(2.0);
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
        // 优化同步逻辑，只在变化较大时同步
        if (!this.level().isClientSide && motion.lengthSqr() > 0.001D) {
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
        
        // 应用更强的阻力
        motion = motion.scale(0.9D); // 从0.8提高到0.9，减少阻力
        
        // 限制最大速度但提高限制
        double speed = motion.length();
        if (speed > MAX_SPEED) {
            motion = motion.scale(MAX_SPEED / speed);
        }
        
        // 确保最小速度，防止完全停止
        if (motion.lengthSqr() < 0.003D) {
            motion = Vec3.ZERO;
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
        // 添加安全高度检测
        int groundY = this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, this.blockPosition()).getY();
        if (this.getY() < groundY + 5 && this.getY() > groundY - 10) {
            this.addDeltaMovement(new Vec3(0, 0.2, 0)); // 降低上升力度
        }
    }

    public QiheiSwordModel.AnimationState getAnimationState() {
        return currentAnimationState;
    }

    // 自定义AI目标：悬浮行为
    private static class QiheiSwordFloatGoal extends Goal {
        private final QiheiSwordEntity sword;
        private static final double VERTICAL_DAMPING = 0.8D;
        private static final double HEIGHT_CHANGE_THRESHOLD = 0.1D;

        public QiheiSwordFloatGoal(QiheiSwordEntity sword) {
            this.sword = sword;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            // 只在没有目标时启用悬浮行为
            return sword.getTarget() == null;
        }

        @Override
        public void tick() {
            // 增加更频繁的高度调整
            if (sword.random.nextInt(50) == 0) { // 从200改为50
                sword.targetHeight = 5.0f + sword.random.nextFloat() * 5.0f;
            }

            double currentHeight = sword.getY() - sword.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, sword.blockPosition()).getY();
            double heightDiff = sword.targetHeight - currentHeight;
            
            // 降低高度调整阈值
            if (Math.abs(heightDiff) > 0.05) { // 从0.1改为0.05
                // 增加垂直力度
                double verticalForce = Math.signum(heightDiff) * 0.03; // 从0.015增加到0.03
                
                Vec3 currentMotion = sword.getDeltaMovement();
                double dampedVerticalSpeed = currentMotion.y * 0.9; // 减少阻尼
                double finalVerticalForce = verticalForce + dampedVerticalSpeed;
                
                // 增加水平漂移
                double driftX = (sword.random.nextDouble() - 0.5) * 0.02;
                double driftZ = (sword.random.nextDouble() - 0.5) * 0.02;
                
                sword.addDeltaMovement(new Vec3(driftX, finalVerticalForce - currentMotion.y, driftZ));
            }
        }
    }

    // 自定义AI目标：攻击行为
    private static class QiheiSwordAttackGoal extends Goal {
        private final QiheiSwordEntity sword;
        private int attackCooldown = 0;
        private static final int ATTACK_COOLDOWN = 40;
        private static final double ATTACK_RANGE = 3.5D;
        private static final double DETECTION_RANGE = 48.0D; // 降低检测范围，使其在狭小空间更有效
        private static final double IDEAL_DISTANCE = 4.0D;   // 减小理想距离，适应狭小空间
        private static final double DASH_SPEED = 1.0D;      // 降低冲刺速度，提高控制性
        private static final double MIN_ATTACK_DISTANCE = 2.0D; // 添加最小攻击距离
        
        private enum AttackPhase {
            POSITIONING, // 保持距离
            AIMING,     // 瞄准
            DASHING,    // 冲刺
            RETREATING  // 后撤
        }
        
        private AttackPhase currentPhase = AttackPhase.POSITIONING;
        private int phaseTimer = 0;
        private Vec3 dashDirection = null;

        public QiheiSwordAttackGoal(QiheiSwordEntity sword) {
            this.sword = sword;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            // 如果没有目标，尝试寻找新目标
            if (sword.getTarget() == null) {
                findNewTarget();
            }
            
            LivingEntity target = sword.getTarget();
            if (target != null) {
                // 如果目标太远或者失去视线，寻找新目标
                if (sword.distanceTo(target) > DETECTION_RANGE || !sword.hasLineOfSight(target)) {
                    findNewTarget();
                }
            }
            
            return sword.getTarget() != null;
        }

        @Override
        public void tick() {
            LivingEntity target = sword.getTarget();
            if (target == null || !target.isAlive()) {
                findNewTarget();
                return;
            }

            // 减少攻击冷却时间
            if (attackCooldown > 0) {
                attackCooldown--;
                // 在冷却期间有机会切换目标
                if (sword.getRandom().nextInt(60) == 0) { // 每3秒约有一次机会切换目标
                    findNewTarget();
                    return;
                }
            }

            double distance = sword.distanceTo(target);
            
            switch (currentPhase) {
                case POSITIONING:
                    // 调整到理想距离，但要考虑周围环境
                    Vec3 toTarget = target.position().subtract(sword.position());
                    double targetDistance = distance - IDEAL_DISTANCE;
                    
                    // 检查周围是否有足够空间
                    AABB boundingBox = sword.getBoundingBox().inflate(1.0);
                    boolean hasSpace = sword.level().noCollision(boundingBox);
                    
                    // 如果空间受限，减小移动幅度
                    double movementScale = hasSpace ? 0.15 : 0.08;
                    Vec3 movement = toTarget.normalize().scale(targetDistance * movementScale);
                    
                    // 添加垂直移动以避开障碍
                    if (!hasSpace) {
                        movement = movement.add(0, 0.1, 0);
                    }
                    
                    sword.addDeltaMovement(movement);
                    
                    // 调整进入瞄准阶段的条件
                    if ((Math.abs(distance - IDEAL_DISTANCE) < 3.0 || !hasSpace) && attackCooldown <= 0) {
                        currentPhase = AttackPhase.AIMING;
                        phaseTimer = 0;
                    }
                    break;
                    
                case AIMING:
                    // 瞄准阶段，保持相对静止并对准目标
                    phaseTimer++;
                    
                    // 计算目标当前速度
                    Vec3 targetVelocity = target.getDeltaMovement();
                    // 预计冲刺需要的时间（以tick为单位）
                    double distanceToTarget = sword.distanceTo(target);
                    int predictedTicks = (int)(distanceToTarget / DASH_SPEED);
                    predictedTicks = Math.min(predictedTicks, 10);
                    
                    // 计算预判位置
                    Vec3 predictedPos = target.position().add(
                        targetVelocity.x * predictedTicks,
                        targetVelocity.y * predictedTicks,
                        targetVelocity.z * predictedTicks
                    );
                    
                    // 计算冲刺方向
                    dashDirection = predictedPos.subtract(sword.position()).normalize();
                    
                    // 瞄准时间缩短
                    if (phaseTimer >= 5) {
                        currentPhase = AttackPhase.DASHING;
                        phaseTimer = 0;
                    }
                    break;
                    
                case DASHING:
                    if (dashDirection != null) {
                        // 检查前方是否有障碍物
                        Vec3 nextPos = sword.position().add(dashDirection.scale(1.0));
                        AABB nextBounds = sword.getBoundingBox().move(dashDirection.scale(1.0));
                        
                        if (sword.level().noCollision(nextBounds)) {
                            // 无障碍时正常冲刺
                            sword.addDeltaMovement(dashDirection.scale(0.8D));
                        } else {
                            // 有障碍时尝试调整方向
                            Vec3 adjustedDir = new Vec3(
                                dashDirection.x,
                                dashDirection.y + 0.2, // 稍微向上倾斜
                                dashDirection.z
                            ).normalize();
                            sword.addDeltaMovement(adjustedDir.scale(0.6D));
                        }
                    }
                    
                    // 检查攻击距离
                    if (distance <= ATTACK_RANGE) {
                        if (sword.doHurtTarget(target)) {
                            currentPhase = AttackPhase.RETREATING;
                            phaseTimer = 0;
                            // 添加击退后的反作用力
                            sword.addDeltaMovement(dashDirection.reverse().scale(0.8));
                        }
                    }
                    
                    // 如果冲刺时间过长，也进入后撤阶段
                    if (++phaseTimer >= 25) {
                        currentPhase = AttackPhase.RETREATING;
                        phaseTimer = 0;
                    }
                    break;
                    
                case RETREATING:
                    Vec3 retreatDir = sword.position().subtract(target.position()).normalize();
                    
                    // 检查后撤空间
                    AABB retreatBox = sword.getBoundingBox().move(retreatDir.scale(1.0));
                    if (!sword.level().noCollision(retreatBox)) {
                        // 如果后撤方向有障碍，尝试向上后撤
                        retreatDir = new Vec3(
                            retreatDir.x * 0.5,
                            0.3,
                            retreatDir.z * 0.5
                        ).normalize();
                    }
                    
                    sword.addDeltaMovement(retreatDir.scale(0.2));
                    
                    // 缩短后撤时间，从20降到10
                    if (++phaseTimer >= 15) {
                        // 后撤结束后，优先寻找新目标
                        attackCooldown = ATTACK_COOLDOWN;
                        findNewTarget();
                        // 如果没找到新目标，则保持在当前目标附近
                        if (sword.getTarget() == null) {
                            sword.setTarget(target);
                            currentPhase = AttackPhase.POSITIONING;
                        }
                        phaseTimer = 0;
                    }
                    break;
            }
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = sword.getTarget();
            return target != null && target.isAlive() && sword.hasLineOfSight(target);
        }

        @Override
        public void stop() {
            currentPhase = AttackPhase.POSITIONING;
            phaseTimer = 0;
            dashDirection = null;
        }

        private void findNewTarget() {
            // 修改搜索范围为球形，而不是立方体
            double searchRadius = DETECTION_RANGE;
            Vec3 swordPos = sword.position();
            List<LivingEntity> possibleTargets = sword.level().getEntitiesOfClass(
                LivingEntity.class,
                sword.getBoundingBox().inflate(searchRadius, searchRadius, searchRadius), // 使用相同的半径
                e -> e != sword.getTarget() && // 不选择当前目标
                    e.isAlive() && 
                    (!(e instanceof Player) || (!((Player) e).isCreative() && !((Player) e).isSpectator())) &&
                    !(e instanceof QiheiSwordEntity) &&
                    // 使用真实的3D距离检查，而不是平面距离
                    e.position().distanceTo(swordPos) <= searchRadius &&
                    sword.hasLineOfSight(e)
            );

            if (!possibleTargets.isEmpty()) {
                // 随机选择一个目标，而不是最近的
                int randomIndex = sword.getRandom().nextInt(possibleTargets.size());
                sword.setTarget(possibleTargets.get(randomIndex));
                currentPhase = AttackPhase.POSITIONING;
                phaseTimer = 0;
            }
        }
    }

    // 自定义AI目标：随机巡逻
    private static class QiheiSwordRandomStrollGoal extends Goal {
        private final QiheiSwordEntity sword;
        private Vec3 target;
        private static final double STROLL_FORCE = 0.3D;
        private int strollTime = 0;
        private static final int MAX_STROLL_TIME = 200;
        private int targetSearchCooldown = 0;
        private static final int TARGET_SEARCH_INTERVAL = 20;
        private Vec3 currentVelocity = Vec3.ZERO;
        private boolean isStrolling = false;
        private int targetCheckCooldown = 0; // 新增目标检查冷却
        private static final int TARGET_CHECK_INTERVAL = 10; // 目标检查间隔
        private static final double SEARCH_RANGE = 24.0D; // 缩小搜索范围到24格
        private static final double STROLL_RANGE = 12.0D; // 限制游荡范围到12格

        public QiheiSwordRandomStrollGoal(QiheiSwordEntity sword) {
            this.sword = sword;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (sword.getTarget() != null) {
                isStrolling = false;
                return false;
            }

            // 在游荡时随机检查是否有可攻击目标
            if (targetSearchCooldown > 0) {
                targetSearchCooldown--;
            } else {
                targetSearchCooldown = TARGET_SEARCH_INTERVAL;
                findPotentialTarget();
            }

            // 如果没有目标，并且没有在巡逻，开始巡逻
            if (!isStrolling && sword.getTarget() == null) {
                isStrolling = true;
                selectNewTarget();
            }

            return sword.getTarget() == null;
        }

        @Override
        public void tick() {
            // 增加目标检查频率
            if (targetCheckCooldown <= 0) {
                findPotentialTarget();
                targetCheckCooldown = TARGET_CHECK_INTERVAL;
            } else {
                targetCheckCooldown--;
            }

            // 如果找到目标，立即停止游荡
            if (sword.getTarget() != null) {
                this.stop();
                return;
            }

            // 原有巡逻逻辑保持不变
            if (target == null) {
                selectNewTarget();
                return;
            }

            strollTime++;
            if (strollTime >= MAX_STROLL_TIME) {
                selectNewTarget();
                strollTime = 0;
                return;
            }

            Vec3 toTarget = target.subtract(sword.position());
            double distSqr = toTarget.lengthSqr();
            
            if (distSqr > 1.0) {
                Vec3 idealVelocity = toTarget.normalize().scale(STROLL_FORCE);
                // 减小随机漂移
                currentVelocity = currentVelocity.scale(0.9) // 增加惯性
                    .add(idealVelocity.scale(0.1));
                
                // 减小随机移动幅度
                currentVelocity = currentVelocity.add(
                    new Vec3(
                        (sword.random.nextDouble() - 0.5) * 0.01, // 从0.02减小到0.01
                        (sword.random.nextDouble() - 0.5) * 0.01,
                        (sword.random.nextDouble() - 0.5) * 0.01
                    )
                );
                
                sword.addDeltaMovement(currentVelocity);
            } else {
                selectNewTarget();
            }
        }

        @Override
        public void start() {
            strollTime = 0;
            currentVelocity = Vec3.ZERO;
            isStrolling = true;
            targetCheckCooldown = 0; // 立即开始第一次目标检查
            selectNewTarget();
        }

        @Override
        public void stop() {
            target = null;
            currentVelocity = Vec3.ZERO;
            strollTime = 0;
            isStrolling = false;
        }

        @Override
        public boolean canContinueToUse() {
            return sword.getTarget() == null;
        }

        private void selectNewTarget() {
            Vec3 currentPos = sword.position();
            
            // 在多个方向尝试找到可行的目标点
            for (int attempts = 0; attempts < 5; attempts++) {
                double distance = 2 + sword.random.nextDouble() * 4; // 减小游荡范围
                double angle = sword.random.nextDouble() * Math.PI * 2;
                double heightVariation = (sword.random.nextDouble() - 0.5) * 2; // 减小高度变化
                
                double x = currentPos.x + Math.cos(angle) * distance;
                double z = currentPos.z + Math.sin(angle) * distance;
                
                int groundY = sword.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos((int)x, 0, (int)z)).getY();
                double y = Mth.clamp(currentPos.y + heightVariation, groundY + 3, groundY + 10);
                
                Vec3 potentialTarget = new Vec3(x, y, z);
                
                // 检查目标点是否可达
                if (sword.level().noCollision(sword.getBoundingBox().move(
                    potentialTarget.subtract(currentPos).normalize()))) {
                    target = potentialTarget;
                    return;
                }
            }
            
            // 如果找不到合适的点，就稍微向上移动
            target = currentPos.add(0, 1, 0);
        }

        private void findPotentialTarget() {
            // 使用球形搜索范围
            double searchRadius = SEARCH_RANGE;
            Vec3 swordPos = sword.position();
            List<LivingEntity> possibleTargets = sword.level().getEntitiesOfClass(
                LivingEntity.class,
                sword.getBoundingBox().inflate(searchRadius, searchRadius, searchRadius),
                e -> e.isAlive() 
                    && (!(e instanceof Player) || (!((Player) e).isCreative() && !((Player) e).isSpectator()))
                    && !(e instanceof QiheiSwordEntity)
                    // 使用真实的3D距离检查
                    && e.position().distanceTo(swordPos) <= searchRadius
                    && sword.hasLineOfSight(e)
            );
            
            if (!possibleTargets.isEmpty()) {
                // 随机选择目标，不再按距离排序
                int randomIndex = sword.getRandom().nextInt(possibleTargets.size());
                sword.setTarget(possibleTargets.get(randomIndex));
            }
        }
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }
} 