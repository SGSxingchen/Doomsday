package org.lanstard.doomsday.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import org.lanstard.doomsday.echo.*;
import org.lanstard.doomsday.sanity.SanityManager;
import org.lanstard.doomsday.network.NetworkManager;
import org.lanstard.doomsday.network.packet.SpawnScreenTextPacket;
import org.lanstard.doomsday.client.gui.text.ScreenTextPreset;
import net.minecraftforge.network.NetworkDirection;
import org.lanstard.doomsday.network.packet.DuoXinPoStatusPacket;

import java.util.*;

public class DuoXinPoEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.DUOXINPO;
    private static final int RANGE = 10; // 影响范围
    private static final int CONTROL_RANGE = 15; // 控制距离
    private static final int CONTROL_DURATION = 30 * 20; // 30秒的tick数
    private static final int SANITY_DRAIN = 1; // 每秒降低的理智值
    private static final int MIN_SANITY = 500; // 最低理智值限制
    private static final int BELIEF_THRESHOLD = 6; // 信念阈值
    private static final int CONTINUOUS_SANITY_COST = 5; // 每秒持续消耗的理智值
    
    private final Map<UUID, Long> controlledTargets = new HashMap<>(); // 被控制的目标及其结束时间
    private final Map<UUID, LivingEntity> controlledEntities = new HashMap<>(); // 被控制的生物
    private final Map<UUID, Long> controlledEntityEndTimes = new HashMap<>(); // 生物控制结束时间
    private int tickCounter = 0;
    private boolean wasJumping = false; // 记录上一tick是否在跳跃
    private boolean wasSneaking = false; // 记录上一tick是否在下蹲
    
    public DuoXinPoEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            PRESET.getSanityConsumption(),
            PRESET.getContinuousSanityConsumption()
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f夺心魄的力量在你体内流转..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        if (!isActive()) return;
        
        // 每秒触发一次效果
        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            applyPassiveEffect(player);
            
            // 检查理智值是否足够继续维持效果
            int currentSanity = SanityManager.getSanity(player);
            if (currentSanity < CONTINUOUS_SANITY_COST) {
                // 理智不足，自动关闭效果
                setActive(false);
                onDeactivate(player);
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f理智不足，夺心魄被迫停止..."));
                return;
            }
            
            // 消耗理智值
            SanityManager.modifySanity(player, -CONTINUOUS_SANITY_COST);
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
        
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f夺心魄的力量暂时消散了..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查理智值是否足够
        int currentSanity = SanityManager.getSanity(player);
        
        // 检查是否有足够理智使用主动技能
        if (currentSanity < getSanityConsumption()) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f你的理智不足以使用夺心魄！"));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 获取玩家视线方向的目标
        Object target = getTarget(player);
        if (target == null) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f未找到有效目标！"));
            return;
        }
        
        // 添加到控制目标列表
        long endTime = System.currentTimeMillis() + CONTROL_DURATION * 50;
        
        if (target instanceof ServerPlayer targetPlayer) {
            controlledTargets.put(targetPlayer.getUUID(), endTime);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f你开始控制玩家 ")
                .append(targetPlayer.getDisplayName())
                .append(Component.literal(" §f的行动！")));
            targetPlayer.sendSystemMessage(Component.literal("§c[十日终焉] §f你被 ")
                .append(player.getDisplayName())
                .append(Component.literal(" §f用夺心魄控制了！")));
        } else if (target instanceof LivingEntity targetEntity) {
            UUID entityId = UUID.randomUUID();
            controlledEntities.put(entityId, targetEntity);
            controlledEntityEndTimes.put(entityId, endTime);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f你开始控制生物 ")
                .append(targetEntity.getName())
                .append(Component.literal(" §f的行动！")));
            
            if (targetEntity instanceof Mob mob) {
                mob.setTarget(null);
                mob.setNoAi(true);
            }
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
        if (!doCanUse(player)) return;
        
        if (!isActive()) {
            // 激活持续效果
            SanityManager.modifySanity(player, -getSanityConsumption());
            setActive(true);
            onActivate(player);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f你激活了夺心魄的持续效果..."));
        } else {
            // 关闭持续效果
            setActive(false);
            onDeactivate(player);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f你关闭了夺心魄的持续效果..."));
        }
    }
} 