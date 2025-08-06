package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.chat.Component;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import net.minecraft.nbt.CompoundTag;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class YinNiEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.YINNI;
    private static final int TOGGLE_SANITY_COST = 30;        // 开启消耗
    private static final int CONTINUOUS_SANITY_COST = 1;     // 每秒消耗
    private static final int FREE_COST_THRESHOLD = 300;      // 免费释放阈值
    private static final int MIN_BELIEF = 10;                // 最小信念要求
    private static final int INVISIBILITY_DURATION = 40;     // 隐身持续时间（2秒）
    private static final int HIGH_FAITH_DURATION = 60;       // 高信念时隐身持续时间（3秒）
    private static final int GROUP_RANGE = 5;                // 群体隐身范围
    private static final int MAX_TARGETS = 3;                // 最大目标数量
    
    private int tickCounter = 0;                             // 用于计时每秒消耗
    private final List<UUID> selectedTargets = new ArrayList<>(); // 选中的目标列表

    public YinNiEcho() {
        super(PRESET.name().toLowerCase(), PRESET.getDisplayName(), PRESET.getType(), TOGGLE_SANITY_COST, CONTINUOUS_SANITY_COST);
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...隐匿之术已成，身形渐隐..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        if (!isActive()) return;
        
        tickCounter++;
        if (tickCounter >= 20) { // 每秒检查一次
            tickCounter = 0;
            
            // 检查理智是否足够继续维持
            int currentSanity = SanityManager.getSanity(player);
            boolean isFree = SanityManager.getFaith(player) >= MIN_BELIEF && currentSanity < FREE_COST_THRESHOLD;
            
            if (!isFree) {
                if (currentSanity < CONTINUOUS_SANITY_COST) {
                    // 理智不足，强制关闭
                    toggleContinuous(player);
                    player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神已竭，隐匿之术难以为继..."));
                    return;
                }
                
                // 消耗理智
                SanityManager.modifySanity(player, -CONTINUOUS_SANITY_COST);
            }

            // 刷新隐身效果
            int faith = SanityManager.getFaith(player);
            if(faith >= 5) {
                // 高信念时：群体隐身
                // 给自己添加效果
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, HIGH_FAITH_DURATION, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, HIGH_FAITH_DURATION, 0, false, false));
                
                // 给范围内的选定目标添加效果
                AABB box = player.getBoundingBox().inflate(GROUP_RANGE);
                List<ServerPlayer> nearbyPlayers = player.level().getEntitiesOfClass(
                    ServerPlayer.class,
                    box,
                    target -> target != player && selectedTargets.contains(target.getUUID())
                );
                
                for (ServerPlayer target : nearbyPlayers) {
                    target.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, HIGH_FAITH_DURATION, 0, false, false));
                    target.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, HIGH_FAITH_DURATION, 0, false, false));
                }
            } else {
                // 普通隐身效果
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, INVISIBILITY_DURATION, 0, false, false));
            }
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        // 移除自身效果
        player.removeEffect(MobEffects.INVISIBILITY);
        player.removeEffect(MobEffects.SLOW_FALLING);
        
        // 移除所有目标的效果
        if (SanityManager.getFaith(player) >= 5) {
            AABB box = player.getBoundingBox().inflate(GROUP_RANGE);
            List<ServerPlayer> nearbyPlayers = player.level().getEntitiesOfClass(
                ServerPlayer.class,
                box,
                target -> target != player && selectedTargets.contains(target.getUUID())
            );
            
            for (ServerPlayer target : nearbyPlayers) {
                target.removeEffect(MobEffects.INVISIBILITY);
                target.removeEffect(MobEffects.SLOW_FALLING);
            }
        }
        
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...隐匿消散，身形显现..."));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        if (!isActive()) {
            // 检查是否可以免费释放
            int currentSanity = SanityManager.getSanity(player);
            boolean isFree = SanityManager.getFaith(player) >= MIN_BELIEF && currentSanity < FREE_COST_THRESHOLD;
            
            // 如果不能免费释放，检查理智是否足够
            if (!isFree && currentSanity < TOGGLE_SANITY_COST) {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以施展隐匿之术..."));
                return;
            }
            
            // 消耗理智
            if (!isFree) {
                SanityManager.modifySanity(player, -TOGGLE_SANITY_COST);
            }
            
            // 激活效果
            setActiveAndUpdate(player,true);
            notifyEchoClocks(player);
            onActivate(player);
            
        } else {
            // 关闭效果
            setActiveAndUpdate(player,false);
            onDeactivate(player);
        }
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        if (SanityManager.getFaith(player) >= 5) {
            // 高信念时可以选择目标
            return true;
        }
        
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...隐匿之术需要持续引导..."));
        return false;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        if (player.isShiftKeyDown()) {
            if (!selectedTargets.isEmpty()) {
                selectedTargets.clear();
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...已清除所有选择的目标..."));
            }
            return;
        }

        // 获取玩家视线方向的目标
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        Vec3 endPos = eyePosition.add(lookVector.x * GROUP_RANGE, lookVector.y * GROUP_RANGE, lookVector.z * GROUP_RANGE);
        
        AABB box = new AABB(eyePosition, endPos).inflate(1.0);
        List<ServerPlayer> possibleTargets = player.level().getEntitiesOfClass(
            ServerPlayer.class,
            box,
            target -> target != player && 
                      player.hasLineOfSight(target) &&
                      !target.hasPermissions(2) && // 排除OP
                      !target.isCreative() &&      // 排除创造模式
                      !target.isSpectator()        // 排除旁观模式
        );
        
        if (!possibleTargets.isEmpty()) {
            ServerPlayer target = possibleTargets.get(0);
            UUID targetId = target.getUUID();
            
            if (selectedTargets.contains(targetId)) {
                // 取消选择
                selectedTargets.remove(targetId);
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...已取消选择 " + target.getName().getString()));
            } else if (selectedTargets.size() < MAX_TARGETS) {
                // 添加选择
                selectedTargets.add(targetId);
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...已选择 " + target.getName().getString() + 
                    " (" + selectedTargets.size() + "/" + MAX_TARGETS + ")"));
                // 通知被选择的玩家
                target.sendSystemMessage(Component.literal("§b[十日终焉] §f..." + player.getName().getString() + "将你选为隐匿目标..."));
            } else {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...已达到最大选择数量..."));
            }
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putInt("tickCounter", tickCounter);
        
        // 保存选定目标
        CompoundTag targetsTag = new CompoundTag();
        int index = 0;
        for (UUID uuid : selectedTargets) {
            CompoundTag uuidTag = new CompoundTag();
            uuidTag.putLong("most", uuid.getMostSignificantBits());
            uuidTag.putLong("least", uuid.getLeastSignificantBits());
            targetsTag.put("target" + index, uuidTag);
            index++;
        }
        tag.put("selectedTargets", targetsTag);
        
        return tag;
    }
    
    public static YinNiEcho fromNBT(CompoundTag tag) {
        YinNiEcho echo = new YinNiEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.tickCounter = tag.getInt("tickCounter");
        
        // 读取选定目标
        if (tag.contains("selectedTargets")) {
            CompoundTag targetsTag = tag.getCompound("selectedTargets");
            int index = 0;
            while (targetsTag.contains("target" + index)) {
                CompoundTag uuidTag = targetsTag.getCompound("target" + index);
                long most = uuidTag.getLong("most");
                long least = uuidTag.getLong("least");
                echo.selectedTargets.add(new UUID(most, least));
                index++;
            }
        }
        
        return echo;
    }
} 