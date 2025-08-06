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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
    private static final double BASE_MOVEMENT_SPEED = 0.4; // 基础移动速度调整为更合理的值
    private static final double MOVEMENT_MULTIPLIER = 20.0; // 移动速度倍率
    private static final int SYNC_INTERVAL = 2; // 同步间隔（ticks）
    
    // 粒子效果相关
    private static final float RED = 1.0F;
    private static final float GREEN = 0.0F;
    private static final float BLUE = 0.0F;
    private static final float PARTICLE_SIZE = 1.0F;
    
    private final Map<UUID, Long> controlledTargets = new ConcurrentHashMap<>();
    private final Map<UUID, LivingEntity> controlledEntities = new ConcurrentHashMap<>();
    private final Map<UUID, Long> controlledEntityEndTimes = new ConcurrentHashMap<>();
    private int tickCounter = 0;
    
    // 添加数据包发送限制
    private int packetCounter = 0;
    
    // 只需要记录控制者的上一次角度
    private Vec3 lastPosition = null;
    private float lastControllerYRot = 0;
    private float lastControllerXRot = 0;
    private float lastControllerYHeadRot = 0;
    
    public DuoXinPoEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
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
        cleanup();
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

        if(!isActive()){
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...必须先开启夺心之法..."));
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
            
            notifyEchoClocks(player);
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
        
        // 计算控制者的移动
        Vec3 controllerMotion;
        if (packetCounter >= SYNC_INTERVAL) {
            Vec3 currentPos = controller.position();
            if (lastPosition == null) {
                lastPosition = currentPos;
                controllerMotion = Vec3.ZERO;
            } else {
                controllerMotion = currentPos.subtract(lastPosition);
                lastPosition = currentPos;
            }
            packetCounter = 0;
        } else {
            packetCounter++;
            return;
        }
        
        // 计算镜像移动
        Vec3 mirroredMotion = new Vec3(
            -controllerMotion.x,
            0,
            -controllerMotion.z
        ).scale(0.5);
        
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

            // 检查距离
            if (controller.distanceTo(target) > CONTROL_RANGE * 1.5) {
                controlledTargets.remove(target.getUUID());
                target.sendSystemMessage(Component.literal("§b[十日终焉] §f...距离过远，夺心之法已解除..."));
                continue;
            }

            // 应用移动
            target.setDeltaMovement(mirroredMotion);
            
            // 处理跳跃
            if (controller.getDeltaMovement().y > 0 && target.onGround()) {
                target.addDeltaMovement(new Vec3(0, 0.42, 0));
            }

            // 同步动量到客户端
            if (!target.level().isClientSide) {
                target.level().broadcastEntityEvent(target, (byte) 38);
                for (ServerPlayer players : ((ServerLevel)target.level()).players()) {
                    players.connection.send(new ClientboundSetEntityMotionPacket(target));
                }
            }
            
            // 更新状态显示
            long remainingTime = (entry.getValue() - currentTime) / 1000;
            NetworkManager.getChannel().sendTo(
                new DuoXinPoStatusPacket("controller", target.getName().getString(), remainingTime),
                controller.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT
            );
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
                entityIt.remove();
                controlledEntityEndTimes.remove(entityId);
                continue;
            }
            
            if (!entity.isAlive()) {
                entityIt.remove();
                controlledEntityEndTimes.remove(entityId);
                continue;
            }
            
            // 检查距离
            if (controller.distanceTo(entity) > CONTROL_RANGE * 1.5) {
                controlledEntities.remove(entityId);
                controlledEntityEndTimes.remove(entityId);
                continue;
            }
            
            // 应用移动
            entity.setDeltaMovement(mirroredMotion);
            
            // 处理跳跃
            if (controller.getDeltaMovement().y > 0 && entity.onGround()) {
                entity.addDeltaMovement(new Vec3(0, 0.42, 0));
            }

            // 同步动量到客户端
            if (!entity.level().isClientSide) {
                entity.level().broadcastEntityEvent(entity, (byte) 38);
                for (ServerPlayer players : ((ServerLevel)entity.level()).players()) {
                    players.connection.send(new ClientboundSetEntityMotionPacket(entity));
                }
            }
            
            // 计算并显示剩余时间
            long remainingTime = endTime != null ? (endTime - currentTime) / 1000 : 30;
            NetworkManager.getChannel().sendTo(
                new DuoXinPoStatusPacket("controller", entity.getName().getString(), remainingTime),
                controller.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT
            );
        }
        
        // 更新视角
        if (!controlledTargets.isEmpty() || !controlledEntities.isEmpty()) {
            // 计算控制者的角度变化
            float yRotDelta = controller.getYRot() - lastControllerYRot;
            float xRotDelta = controller.getXRot() - lastControllerXRot;
            float yHeadRotDelta = controller.getYHeadRot() - lastControllerYHeadRot;
            
            // 更新所有目标的视角
            for (ServerPlayer target : controller.getServer().getPlayerList().getPlayers()) {
                if (controlledTargets.containsKey(target.getUUID())) {
                    target.setYRot(target.getYRot() - yRotDelta);
                    target.setXRot(Mth.clamp(target.getXRot() + xRotDelta, -90.0F, 90.0F));
                    target.setYHeadRot(target.getYHeadRot() - yHeadRotDelta);
                    
                    for (ServerPlayer player : controller.getServer().getPlayerList().getPlayers()) {
                        player.connection.send(new ClientboundRotateHeadPacket(target, 
                            (byte)(target.getYHeadRot() * 256.0F / 360.0F)));
                    }
                }
            }
            
            for (LivingEntity entity : controlledEntities.values()) {
                if (entity instanceof Mob mob) {
                    mob.setYRot(mob.getYRot() - yRotDelta);
                    mob.setXRot(Mth.clamp(mob.getXRot() + xRotDelta, -90.0F, 90.0F));
                    mob.yHeadRot = mob.getYRot();
                    mob.yHeadRotO = mob.yHeadRot;
                    
                    for (ServerPlayer player : controller.getServer().getPlayerList().getPlayers()) {
                        player.connection.send(new ClientboundRotateHeadPacket(mob, 
                            (byte)(mob.yHeadRot * 256.0F / 360.0F)));
                    }
                }
            }
            
            // 更新控制者的上一次角度记录
            lastControllerYRot = controller.getYRot();
            lastControllerXRot = controller.getXRot();
            lastControllerYHeadRot = controller.getYHeadRot();
            
            updateState(controller);
        }
    }

    private void spawnConnectionParticles(ServerPlayer player, LivingEntity target) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        
        Vec3 start = player.getEyePosition();
        Vec3 end = target.position().add(0, target.getBbHeight() * 0.5, 0);
        Vec3 direction = end.subtract(start);
        double distance = direction.length();
        direction = direction.normalize();
        
        DustParticleOptions redParticle = new DustParticleOptions(
            new Vector3f(RED, GREEN, BLUE), 
            PARTICLE_SIZE * 0.5F
        );
        
        // 减少粒子数量，增加间隔
        for (double d = 0; d < distance; d += 1.0) {
            double x = start.x + direction.x * d;
            double y = start.y + direction.y * d;
            double z = start.z + direction.z * d;
            
            serverLevel.sendParticles(redParticle, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        
        // 存储被控制的玩家目标
        CompoundTag targetsTag = new CompoundTag();
        for (Map.Entry<UUID, Long> entry : controlledTargets.entrySet()) {
            targetsTag.putLong(entry.getKey().toString(), entry.getValue());
        }
        tag.put("controlled_targets", targetsTag);
        
        // 存储被控制的实体
        CompoundTag entitiesTag = new CompoundTag();
        CompoundTag entityTimesTag = new CompoundTag();
        for (Map.Entry<UUID, LivingEntity> entry : controlledEntities.entrySet()) {
            // 存储实体的UUID和类型信息
            CompoundTag entityData = new CompoundTag();
            entityData.putString("type", entry.getValue().getType().toString());
            entityData.putUUID("entity_uuid", entry.getValue().getUUID());
            entitiesTag.put(entry.getKey().toString(), entityData);
            
            // 存储实体的控制结束时间
            Long endTime = controlledEntityEndTimes.get(entry.getKey());
            if (endTime != null) {
                entityTimesTag.putLong(entry.getKey().toString(), endTime);
            }
        }
        tag.put("controlled_entities", entitiesTag);
        tag.put("controlled_entity_times", entityTimesTag);
        
        // 存储计数器和角度记录
        tag.putInt("tick_counter", tickCounter);
        tag.putInt("packet_counter", packetCounter);
        tag.putFloat("last_controller_yrot", lastControllerYRot);
        tag.putFloat("last_controller_xrot", lastControllerXRot);
        tag.putFloat("last_controller_yheadrot", lastControllerYHeadRot);
        
        return tag;
    }
    
    public static DuoXinPoEcho fromNBT(CompoundTag tag) {
        DuoXinPoEcho echo = new DuoXinPoEcho();
        echo.setActive(tag.getBoolean("isActive"));
        
        // 恢复被控制的玩家目标
        if (tag.contains("controlled_targets")) {
            CompoundTag targetsTag = tag.getCompound("controlled_targets");
            for (String key : targetsTag.getAllKeys()) {
                echo.controlledTargets.put(UUID.fromString(key), targetsTag.getLong(key));
            }
        }
        
        // 恢复被控制的实体时间数据
        if (tag.contains("controlled_entity_times")) {
            CompoundTag entityTimesTag = tag.getCompound("controlled_entity_times");
            for (String key : entityTimesTag.getAllKeys()) {
                echo.controlledEntityEndTimes.put(UUID.fromString(key), entityTimesTag.getLong(key));
            }
        }
        
        // 恢复计数器和角度记录
        echo.tickCounter = tag.getInt("tick_counter");
        echo.packetCounter = tag.getInt("packet_counter");
        echo.lastControllerYRot = tag.getFloat("last_controller_yrot");
        echo.lastControllerXRot = tag.getFloat("last_controller_xrot");
        echo.lastControllerYHeadRot = tag.getFloat("last_controller_yheadrot");
        
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

    // 添加清理方法
    private void cleanup() {
        controlledTargets.clear();
        controlledEntities.clear();
        controlledEntityEndTimes.clear();
        lastPosition = null;
        lastControllerYRot = 0;
        lastControllerXRot = 0;
        lastControllerYHeadRot = 0;
    }
} 