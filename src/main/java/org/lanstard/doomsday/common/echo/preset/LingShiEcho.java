package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.config.EchoConfig;
import net.minecraft.world.phys.AABB;
import java.util.List;
import org.lanstard.doomsday.common.echo.EchoManager;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.projectile.ProjectileUtil;

public class LingShiEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.LINGSHI;

    public LingShiEcho() {
        super(PRESET.name().toLowerCase(), PRESET.getDisplayName(), PRESET.getType(), EchoConfig.LING_SANITY_COST.get(), 0);
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.translatable("message.doomsday.lingshi.activate"));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 这是一个主动技能，不需要更新
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.translatable("message.doomsday.lingshi.deactivate"));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查是否可以免费释放
        int currentSanity = SanityManager.getSanity(player);
        boolean isFree = SanityManager.getFaith(player) >= EchoConfig.LING_MIN_FAITH.get() && currentSanity < EchoConfig.LING_FREE_COST_THRESHOLD.get();
        
        // 如果不能免费释放，检查理智是否足够
        if (!isFree && currentSanity < EchoConfig.LING_SANITY_COST.get()) {
            player.sendSystemMessage(Component.translatable("message.doomsday.lingshi.low_sanity"));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 获取玩家视线中的目标
        int faith = SanityManager.getFaith(player);
        double reach = faith >= EchoConfig.LING_MIN_FAITH.get() ? EchoConfig.LING_HIGH_RANGE.get() : 
                      (faith >= EchoConfig.LING_MID_FAITH.get() ? EchoConfig.LING_MID_RANGE.get() : EchoConfig.LING_BASE_RANGE.get());
                      
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
            player.sendSystemMessage(Component.translatable("message.doomsday.lingshi.no_target"));
            return;
        }
        
        // 检查是否可以免费释放
        boolean isFree = SanityManager.getFaith(player) >= EchoConfig.LING_MIN_FAITH.get() && SanityManager.getSanity(player) < EchoConfig.LING_FREE_COST_THRESHOLD.get();
        
        // 获取信念等级文本
        String faithLevelKey = faith >= EchoConfig.LING_MIN_FAITH.get() ? "message.doomsday.faith_level.firm" : 
                              (faith >= EchoConfig.LING_MID_FAITH.get() ? "message.doomsday.faith_level.stable" : "message.doomsday.faith_level.weak");
        Component faithLevelText = Component.translatable(faithLevelKey);
        
        // 消耗理智
        if (!isFree) {
            int actualCost = faith >= EchoConfig.LING_MID_FAITH.get() ? EchoConfig.LING_SANITY_COST.get() / 2 : EchoConfig.LING_SANITY_COST.get();
            SanityManager.modifySanity(player, -actualCost);
            player.sendSystemMessage(Component.translatable("message.doomsday.lingshi.use_cost", actualCost, faithLevelText));
        } else {
            player.sendSystemMessage(Component.translatable("message.doomsday.lingshi.use_free", faithLevelText));
        }

        // 获取目标的回响和理智值
        int targetSanity = SanityManager.getSanity(target);
        int targetMaxSanity = SanityManager.getMaxSanity(target);
        
        // 发送信息
        player.sendSystemMessage(Component.translatable("message.doomsday.lingshi.view_info", target.getName().getString(), targetSanity, targetMaxSanity));
        
        // 获取并显示目标的回响信息
        List<Echo> echoes = EchoManager.getPlayerEchoes(target);
        if (echoes.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.doomsday.lingshi.no_echoes"));
        } else {
            StringBuilder message = new StringBuilder(Component.translatable("message.doomsday.lingshi.echoes_prefix").getString());
            for (Echo echo : echoes) {
                message.append(echo.getName());
                if (echo.isActive()) {
                    message.append(Component.translatable("message.doomsday.lingshi.echo_active").getString());
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
        player.sendSystemMessage(Component.translatable("message.doomsday.lingshi.not_continuous"));
    }
} 