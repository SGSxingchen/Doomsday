package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.network.NetworkManager;
import net.minecraftforge.network.NetworkDirection;
import org.lanstard.doomsday.network.packet.DuoXinPoStatusPacket;
import org.joml.Vector3f;

import java.util.*;

public class DuoXinPoEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.DUOXINPO;
    private static final int RANGE = 10; // 影响范围
    private static final int CONTROL_RANGE = 15; // 控制距离
    private static final int CONTROL_DURATION = 60 * 20; // 60秒的tick数（1分钟）
    private static final int SANITY_DRAIN = 1; // 每秒降低的理智值
    private static final int MIN_SANITY = 500; // 最低理智值限制
    private static final int BELIEF_THRESHOLD = 6; // 信念阈值（无视最低理智限制）
    private static final int FREE_COST_THRESHOLD = 300; // 免费释放阈值
    private static final int CONTINUOUS_SANITY_COST = 5; // 每秒持续消耗的理智值
    private static final int ACTIVE_SANITY_COST = 100; // 主动使用消耗的理智值
    private static final int TOGGLE_SANITY_COST = 30; // 开启持续效果的消耗
    
    // 粒子效果相关
    private static final float RED = 1.0F;
    private static final float GREEN = 0.0F;
    private static final float BLUE = 0.0F;
    private static final float PARTICLE_SIZE = 1.0F;
    
    private final Map<UUID, Long> controlledTargets = new HashMap<>(); // 被控制的目标及其结束时间
    private final Map<UUID, LivingEntity> controlledEntities = new HashMap<>(); // 被控制的生物
    private final Map<UUID, Long> controlledEntityEndTimes = new HashMap<>(); // 生物控制结束时间
    private int tickCounter = 0;
    
    public DuoXinPoEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            ACTIVE_SANITY_COST,  // 使用固定值100点理智消耗
            PRESET.getContinuousSanityConsumption()
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...夺心入脑，心魔渐起..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        if (!isActive()) return;
        
        // 每秒触发一次效果
        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            applyPassiveEffect(player);
            
            // 检查理智值和信念值
            int currentSanity = SanityManager.getSanity(player);
            int faith = SanityManager.getFaith(player);
            
            // 如果信念大于等于10点且理智低于300，则不消耗理智
            boolean freeCost = faith >= 10 && currentSanity < FREE_COST_THRESHOLD;
            
            // 如果不是免费释放且理智不足，则关闭效果
            if (!freeCost && currentSanity < CONTINUOUS_SANITY_COST) {
                setActiveAndUpdate(player, false);
                onDeactivate(player);
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神已竭，夺心之法难以为继..."));
                return;
            }
            
            // 如果不是免费释放，消耗理智
            if (!freeCost) {
                SanityManager.modifySanity(player, -CONTINUOUS_SANITY_COST);
            }
            
            // 添加红色粒子效果
            if (player.level() instanceof ServerLevel serverLevel) {
                Vec3 pos = player.position();
                DustParticleOptions redParticle = new DustParticleOptions(new Vector3f(RED, GREEN, BLUE), PARTICLE_SIZE);
                
                for (int i = 0; i < 8; i++) {
                    double offsetX = (player.getRandom().nextDouble() - 0.5) * 2;
                    double offsetY = player.getRandom().nextDouble() * 2;
                    double offsetZ = (player.getRandom().nextDouble() - 0.5) * 2;
                    
                    serverLevel.sendParticles(redParticle,
                        pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                        1, 0, 0, 0, 0);
                }
            }
        }
        
        // 处理被控制的目标
        handleControlledTargets(player);
    }

    private void applyPassiveEffect(ServerPlayer player) {
        int beliefLevel = SanityManager.getBeliefLevel(player);
        boolean noLimit = beliefLevel >= BELIEF_THRESHOLD;
        
        AABB box = player.getBoundingBox().inflate(RANGE);
        List<ServerPlayer> nearbyPlayers = player.level().getEntitiesOfClass(ServerPlayer.class, box);
        
        for (ServerPlayer target : nearbyPlayers) {
            if (target == player) continue;
            
            int currentSanity = SanityManager.getSanity(target);
            if (!noLimit && currentSanity <= MIN_SANITY) continue;
            
            SanityManager.modifySanity(target, -SANITY_DRAIN);
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        // 清除所有被控制的目标
        controlledTargets.clear();
        
        // 恢复所有被控制生物的AI
        for (LivingEntity entity : controlledEntities.values()) {
            if (entity instanceof Mob mob) {
                mob.setNoAi(false);
            }
        }
        controlledEntities.clear();
        controlledEntityEndTimes.clear();
        
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...仙法消散，众生皆返本心..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {

        // 检查是否被禁用
        if (this.isDisabled()) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...夺心之法暂失其效..."));
            return false;
        }
        
        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        
        // 如果信念大于等于10点且理智低于300，则不消耗理智
        if (faith >= 10 && currentSanity < FREE_COST_THRESHOLD) {
            return true;
        }
        
        // 否则检查理智是否足够
        if (currentSanity < ACTIVE_SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以施展..."));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 获取玩家视线方向的目标
        Object target = getTarget(player);
        if (target == null) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...夺心之法无处可施..."));
            return;
        }
        
        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        
        // 如果信念大于等于10点且理智低于300，则不消耗理智
        boolean freeCost = faith >= 10 && currentSanity < FREE_COST_THRESHOLD;
        
        // 如果不是免费释放，消耗理智
        if (!freeCost) {
            SanityManager.modifySanity(player, -ACTIVE_SANITY_COST);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + ACTIVE_SANITY_COST + "点心神，施展夺心之法..."));
        } else {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念坚定，夺心之法不耗心神..."));
        }
        
        // 添加到控制目标列表
        long endTime = System.currentTimeMillis() + CONTROL_DURATION * 50;
        
        if (target instanceof ServerPlayer targetPlayer) {
            controlledTargets.put(targetPlayer.getUUID(), endTime);
            spawnConnectionParticles(player, targetPlayer);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...仙法已成，")
                .append(targetPlayer.getDisplayName())
                .append(Component.literal(" §f之心已为你所控...")));
            targetPlayer.sendSystemMessage(Component.literal("§b[十日终焉] §f...你的心神已被 ")
                .append(player.getDisplayName())
                .append(Component.literal(" §f所夺...")));
            updateState(player); // 更新状态
        } else if (target instanceof LivingEntity targetEntity) {
            UUID entityId = UUID.randomUUID();
            controlledEntities.put(entityId, targetEntity);
            controlledEntityEndTimes.put(entityId, endTime);
            spawnConnectionParticles(player, targetEntity);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...仙法已成，")
                .append(targetEntity.getName())
                .append(Component.literal(" §f已为你所控...")));
            
            if (targetEntity instanceof Mob mob) {
                mob.setTarget(null);
                mob.setNoAi(true);
            }
            updateState(player); // 更新状态
        }
    }
    
    private Object getTarget(ServerPlayer player) {
        // 获取玩家视线方向的射线
        double reachDistance = 20.0; // 最大搜索距离
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        Vec3 endPos = eyePosition.add(lookVector.x * reachDistance, lookVector.y * reachDistance, lookVector.z * reachDistance);
        
        // 创建射线的碰撞箱
        AABB searchBox = player.getBoundingBox().inflate(reachDistance);
        
        // 搜索玩家
        List<ServerPlayer> players = player.level().getEntitiesOfClass(ServerPlayer.class, searchBox, 
            target -> target != player && target.isAlive());
            
        // 搜索生物
        List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, searchBox,
            entity -> entity != player && entity.isAlive() && !(entity instanceof ServerPlayer));
        
        // 寻找最近的目标
        Object nearestTarget = null;
        double nearestDistance = Double.MAX_VALUE;
        
        // 检查玩家
        for (ServerPlayer target : players) {
            AABB targetBox = target.getBoundingBox();
            Optional<Vec3> hitResult = targetBox.clip(eyePosition, endPos);
            
            if (hitResult.isPresent()) {
                double distance = eyePosition.distanceTo(hitResult.get());
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestTarget = target;
                }
            }
        }
        
        // 检查生物
        for (LivingEntity entity : entities) {
            AABB targetBox = entity.getBoundingBox();
            Optional<Vec3> hitResult = targetBox.clip(eyePosition, endPos);
            
            if (hitResult.isPresent()) {
                double distance = eyePosition.distanceTo(hitResult.get());
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestTarget = entity;
                }
            }
        }
        
        return nearestTarget;
    }
    
    private void handleControlledTargets(ServerPlayer controller) {
        long currentTime = System.currentTimeMillis();
        
        // 处理玩家目标
        Iterator<Map.Entry<UUID, Long>> it = controlledTargets.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Long> entry = it.next();
            if (currentTime > entry.getValue()) {
                // 控制时间结束
                ServerPlayer target = controller.getServer().getPlayerList().getPlayer(entry.getKey());
                if (target != null) {
                    target.sendSystemMessage(Component.literal("§b[十日终焉] §f你摆脱了夺心魄的控制！"));
                    // 清除状态显示
                    NetworkManager.getChannel().sendTo(
                        new DuoXinPoStatusPacket("clear", "", 0),
                        target.connection.connection,
                        NetworkDirection.PLAY_TO_CLIENT
                    );
                }
                it.remove();
                continue;
            }
            
            // 获取目标玩家
            ServerPlayer target = controller.getServer().getPlayerList().getPlayer(entry.getKey());
            if (target == null) {
                it.remove();
                continue;
            }

            // 同步动作
            syncMovement(controller, target);
            
            // 更新状态显示
            long remainingTime = (entry.getValue() - currentTime) / 1000; // 转换为秒
            
            // 为控制者显示状态
            NetworkManager.getChannel().sendTo(
                new DuoXinPoStatusPacket("controller", target.getName().getString(), remainingTime),
                controller.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT
            );
            
            // 为被控制者显示状态
            NetworkManager.getChannel().sendTo(
                new DuoXinPoStatusPacket("controlled", controller.getName().getString(), remainingTime),
                target.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT
            );
        }
        
        // 处理生物目标
        Iterator<Map.Entry<UUID, LivingEntity>> entityIt = controlledEntities.entrySet().iterator();
        while (entityIt.hasNext()) {
            Map.Entry<UUID, LivingEntity> entry = entityIt.next();
            UUID entityId = entry.getKey();
            LivingEntity entity = entry.getValue();
            
            // 检查控制时间是否结束
            Long endTime = controlledEntityEndTimes.get(entityId);
            if (endTime != null && currentTime > endTime) {
                if (entity instanceof Mob mob) {
                    mob.setNoAi(false);
                }
                entityIt.remove();
                controlledEntityEndTimes.remove(entityId);
                continue;
            }
            
            if (!entity.isAlive()) {
                entityIt.remove();
                controlledEntityEndTimes.remove(entityId);
                if (entity instanceof Mob mob) {
                    mob.setNoAi(false);
                }
                continue;
            }
            
            // 同步动作
            syncMovement(controller, entity);
            
            // 计算并显示剩余时间
            long remainingTime = endTime != null ? (endTime - currentTime) / 1000 : 30;
            
            // 为控制者显示状态
            NetworkManager.getChannel().sendTo(
                new DuoXinPoStatusPacket("controller", entity.getName().getString(), remainingTime),
                controller.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT
            );
        }
        
        // 当目标列表发生变化时，更新状态
        if (!controlledTargets.isEmpty() || !controlledEntities.isEmpty()) {
            updateState(controller);
        }
    }
    
    // 添加广播实体移动的辅助方法
    private void broadcastEntityMotion(ServerPlayer controller, LivingEntity target) {
        // 获取所有在线玩家
        for (ServerPlayer player : controller.getServer().getPlayerList().getPlayers()) {
            player.connection.send(new ClientboundSetEntityMotionPacket(
                target
            ));
        }
    }

    private void syncMovement(ServerPlayer controller, LivingEntity target) {
        // 同步移动方向
        float forward = controller.zza; // 前后移动
        float strafe = controller.xxa; // 左右移动
        
        if (forward != 0 || strafe != 0) {
            // 计算移动向量
            double moveSpeed = target instanceof ServerPlayer ? 0.2 : 0.15;
            double dx = strafe * moveSpeed;
            double dz = forward * moveSpeed;
            
            // 根据控制者的视角旋转移动向量
            float yRot = controller.getYRot() * ((float)Math.PI / 180F);
            double sin = Math.sin(yRot);
            double cos = Math.cos(yRot);
            double nx = dx * cos - dz * sin;
            double nz = dz * cos + dx * sin;
            
            // 应用移动
            target.addDeltaMovement(new Vec3(nx, target.getDeltaMovement().y, nz));
            
            // 广播移动向量到所有客户端
            broadcastEntityMotion(controller, target);
        } else {
            // 停止移动
            target.addDeltaMovement(new Vec3(0, target.getDeltaMovement().y, 0));
            // 广播停止状态到所有客户端
            broadcastEntityMotion(controller, target);
        }
        
        // 同步跳跃
        if (controller.getDeltaMovement().y > 0 && target.onGround()) {
            target.addDeltaMovement(new Vec3(target.getDeltaMovement().x, 0.42, target.getDeltaMovement().z));
            broadcastEntityMotion(controller, target);
        }
        
        // 同步视角
        target.setYRot(controller.getYRot());
        target.setXRot(controller.getXRot());
        target.yRotO = target.getYRot();
        target.xRotO = target.getXRot();
        
        // 同步生物的朝向到客户端
        if (target instanceof Mob mob) {
            mob.yHeadRot = mob.getYRot();
            mob.yHeadRotO = mob.yHeadRot;
            
            // 广播头部旋转到所有客户端
            for (ServerPlayer player : controller.getServer().getPlayerList().getPlayers()) {
                player.connection.send(new ClientboundRotateHeadPacket(mob, (byte)(mob.yHeadRot * 256.0F / 360.0F)));
            }
        }
    }
    
    private void spawnConnectionParticles(ServerPlayer player, LivingEntity target) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        
        Vec3 start = player.getEyePosition();
        Vec3 end = target.position().add(0, target.getBbHeight() * 0.5, 0);
        Vec3 direction = end.subtract(start);
        double distance = direction.length();
        direction = direction.normalize();
        
        DustParticleOptions redParticle = new DustParticleOptions(new Vector3f(RED, GREEN, BLUE), PARTICLE_SIZE * 0.5F);
        
        // 在两点之间生成粒子线
        for (double d = 0; d < distance; d += 0.5) {
            double x = start.x + direction.x * d;
            double y = start.y + direction.y * d;
            double z = start.z + direction.z * d;
            
            serverLevel.sendParticles(redParticle, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT(); // 先获取基类数据
        
        // 存储被控制的目标
        CompoundTag targetsTag = new CompoundTag();
        for (Map.Entry<UUID, Long> entry : controlledTargets.entrySet()) {
            targetsTag.putLong(entry.getKey().toString(), entry.getValue());
        }
        tag.put("controlled_targets", targetsTag);
        
        // 存储计时器
        tag.putInt("tick_counter", tickCounter);
        
        return tag;
    }
    
    public static DuoXinPoEcho fromNBT(CompoundTag tag) {
        DuoXinPoEcho echo = new DuoXinPoEcho();
        echo.setActive(tag.getBoolean("isActive"));
        
        // 恢复被控制的目标
        if (tag.contains("controlled_targets")) {
            CompoundTag targetsTag = tag.getCompound("controlled_targets");
            for (String key : targetsTag.getAllKeys()) {
                echo.controlledTargets.put(UUID.fromString(key), targetsTag.getLong(key));
            }
        }
        
        // 恢复计时器
        echo.tickCounter = tag.getInt("tick_counter");
        
        return echo;
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        if (!isActive()) {
            // 检查理智值和信念值
            int currentSanity = SanityManager.getSanity(player);
            int faith = SanityManager.getFaith(player);
            
            // 如果信念大于等于10点且理智低于300，则不消耗理智
            boolean freeCost = faith >= 10 && currentSanity < FREE_COST_THRESHOLD;
            
            // 如果不是免费释放且理智不足，则无法开启
            if (!freeCost && currentSanity < TOGGLE_SANITY_COST) {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以施展夺心之法..."));
                return;
            }
            
            // 如果不是免费释放，消耗理智
            if (!freeCost) {
                SanityManager.modifySanity(player, -TOGGLE_SANITY_COST);
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + TOGGLE_SANITY_COST + "点心神，施展夺心之法..."));
            } else {
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念引导，夺心之法不耗心神..."));
            }
            
            setActiveAndUpdate(player, true);
            onActivate(player);
        } else {
            setActiveAndUpdate(player, false);
            onDeactivate(player);
        }
    }
} 