package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class TiZuiEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.TIZUI;
    private static final int SANITY_COST = 50;              // 理智消耗
    private static final int FREE_COST_THRESHOLD = 300;     // 免费释放阈值
    private static final int MIN_FAITH_REQUIREMENT = 10;    // 最低信念要求
    private static final int RANGE = 16;                    // 选择目标的范围
    
    private UUID boundTargetId = null;                      // 绑定目标的UUID
    private String boundTargetName = null;                  // 绑定目标的名字（用于显示）

    public TiZuiEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            SANITY_COST,  // 主动技能消耗
            0            // 无被动消耗
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...我听到了，替罪的回响..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 检查绑定目标是否还存在
        if (boundTargetId != null) {
            ServerPlayer target = player.getServer().getPlayerList().getPlayer(boundTargetId);
            if (target == null || !target.isAlive()) {
                // 目标不存在或已死亡，解除绑定
                boundTargetId = null;
                boundTargetName = null;
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...替罪之契已然断绝..."));
            }
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        // 解除绑定
        boundTargetId = null;
        boundTargetName = null;
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...替罪的回响消散了..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 如果已经绑定了目标，不能再次使用
        if (boundTargetId != null) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...已有替罪之契，无法再立新约..."));
            return false;
        }

        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = faith >= MIN_FAITH_REQUIREMENT && currentSanity < FREE_COST_THRESHOLD;

        // 如果不是免费释放且理智不足
        if (!freeCost && currentSanity < SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以立下替罪之契..."));
            return false;
        }

        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 获取玩家视线中的目标
        double reach = RANGE;
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        Vec3 endPos = eyePosition.add(lookVector.x * reach, lookVector.y * reach, lookVector.z * reach);
        
        EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(
            player.level(),
            player,
            eyePosition,
            endPos,
            new AABB(eyePosition, endPos).inflate(1.0),
            entity -> entity instanceof ServerPlayer && entity != player,
            0.0f
        );

        if (hitResult == null || !(hitResult.getEntity() instanceof ServerPlayer target)) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...视线中无可替罪之人..."));
            return;
        }

        // 检查是否可以免费释放
        int currentSanity = SanityManager.getSanity(player);
        boolean isFree = SanityManager.getFaith(player) >= MIN_FAITH_REQUIREMENT && currentSanity < FREE_COST_THRESHOLD;

        // 消耗理智
        if (!isFree) {
            SanityManager.modifySanity(player, -SANITY_COST);
        }

        // 绑定目标
        boundTargetId = target.getUUID();
        boundTargetName = target.getName().getString();

        // 发送消息
        if (isFree) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念引导，替罪之契已立..."));
        } else {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + SANITY_COST + "点心神，替罪之契已立..."));
        }
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...你与" + boundTargetName + "的命运已然相连..."));
        target.sendSystemMessage(Component.literal("§b[十日终焉] §f..." + player.getName().getString() + "与你立下了替罪之契..."));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 这是一个主动技能，不需要切换
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...替罪之契需要主动立下..."));
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        if (boundTargetId != null) {
            tag.putUUID("boundTargetId", boundTargetId);
            tag.putString("boundTargetName", boundTargetName);
        }
        return tag;
    }
    
    public static TiZuiEcho fromNBT(CompoundTag tag) {
        TiZuiEcho echo = new TiZuiEcho();
        echo.setActive(tag.getBoolean("isActive"));
        if (tag.contains("boundTargetId")) {
            echo.boundTargetId = tag.getUUID("boundTargetId");
            echo.boundTargetName = tag.getString("boundTargetName");
        }
        return echo;
    }

    // 当绑定的目标即将死亡时调用此方法
    public void onTargetDeath(ServerPlayer target, ServerPlayer owner) {
        if (target.getUUID().equals(boundTargetId)) {
            // 阻止目标死亡
            target.setHealth(1.0f);
            
            // 替罪者死亡
            owner.kill();
            
            // 解除绑定
            boundTargetId = null;
            boundTargetName = null;
            
            // 发送消息
            target.sendSystemMessage(Component.literal("§b[十日终焉] §f..." + owner.getName().getString() + "替你承担了死亡..."));
            owner.sendSystemMessage(Component.literal("§b[十日终焉] §f...替罪之契已成，你替" + boundTargetName + "承担了死亡..."));
        }
    }

    // 检查是否是绑定的目标
    public boolean isBoundTarget(UUID targetId) {
        return boundTargetId != null && boundTargetId.equals(targetId);
    }
} 