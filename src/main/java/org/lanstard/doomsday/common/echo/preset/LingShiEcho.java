package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import net.minecraft.world.phys.AABB;
import java.util.List;
import org.lanstard.doomsday.common.echo.EchoManager;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.projectile.ProjectileUtil;

public class LingShiEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.LINGSHI;
    private static final int SANITY_COST = 20;           // 使用消耗
    private static final int FREE_COST_THRESHOLD = 300;  // 免费释放阈值
    private static final int MIN_BELIEF = 10;            // 最小信念要求
    private static final int MID_BELIEF = 5;             // 中等信念要求
    private static final int BASE_RANGE = 10;            // 基础检测范围
    private static final int MID_RANGE = 20;             // 中等信念检测范围
    private static final int HIGH_RANGE = 30;            // 高等信念检测范围

    public LingShiEcho() {
        super(PRESET.name().toLowerCase(), PRESET.getDisplayName(), PRESET.getType(), PRESET.getActivationType(), SANITY_COST, 0);
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...灵视之力已觉醒..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 这是一个主动技能，不需要更新
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...灵视之力已消散..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查是否可以免费释放
        int currentSanity = SanityManager.getSanity(player);
        boolean isFree = SanityManager.getFaith(player) >= MIN_BELIEF && currentSanity < FREE_COST_THRESHOLD;
        
        // 如果不能免费释放，检查理智是否足够
        if (!isFree && currentSanity < SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以施展灵视之术..."));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 获取玩家视线中的目标
        int faith = SanityManager.getFaith(player);
        double reach = faith >= MIN_BELIEF ? HIGH_RANGE : 
                      (faith >= MID_BELIEF ? MID_RANGE : BASE_RANGE);
                      
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
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...视线中无可探查之人..."));
            return;
        }
        
        // 检查是否可以免费释放
        boolean isFree = SanityManager.getFaith(player) >= MIN_BELIEF && SanityManager.getSanity(player) < FREE_COST_THRESHOLD;
        
        // 消耗理智
        if (!isFree) {
            int actualCost = faith >= MID_BELIEF ? SANITY_COST / 2 : SANITY_COST;
            SanityManager.modifySanity(player, -actualCost);
            String faithLevel = faith >= MIN_BELIEF ? "坚定" : (faith >= MID_BELIEF ? "稳固" : "微弱");
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + actualCost + "点心神，灵视之力(" + faithLevel + ")已生效..."));
        } else {
            String faithLevel = faith >= MIN_BELIEF ? "坚定" : (faith >= MID_BELIEF ? "稳固" : "微弱");
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念引导，灵视之力(" + faithLevel + ")已生效..."));
        }

        // 获取目标的回响和理智值
        int targetSanity = SanityManager.getSanity(target);
        int targetMaxSanity = SanityManager.getMaxSanity(target);
        
        // 发送信息
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...灵视之下，" + target.getName().getString() + "的心神为" + targetSanity + "/" + targetMaxSanity + "..."));
        
        // 获取并显示目标的回响信息
        List<Echo> echoes = EchoManager.getPlayerEchoes(target);
        if (echoes.isEmpty()) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...此人尚未觉醒任何回响..."));
        } else {
            StringBuilder message = new StringBuilder("§b[十日终焉] §f...此人已觉醒的回响：\n");
            for (Echo echo : echoes) {
                message.append(echo.getName());
                if (echo.isActive()) {
                    message.append(" §a[已激活]§f");
                }
                message.append("\n");
            }
            player.sendSystemMessage(Component.literal(message.toString()));
        }
        notifyEchoClocks(player);
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 这是一个主动技能，不需要切换持续状态
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...灵视之术需要主动施展..."));
    }
} 