package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.config.EchoConfig;

public class JingLeiEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.JINGLEI;
    private long lastUseTime = 0;

    public JingLeiEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            EchoConfig.JINGLEI_SANITY_COST.get(),
            0
        );
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查冷却时间
        long currentTime = player.level().getGameTime();
        if (lastUseTime > 0) {
            // 计算当前应该的冷却时间
            int faith = SanityManager.getFaith(player);
            float damage = EchoConfig.JINGLEI_BASE_DAMAGE.get().floatValue() + faith * EchoConfig.JINGLEI_DAMAGE_PER_FAITH.get().floatValue();
            long actualCoolDown = (long)(EchoConfig.JINGLEI_BASE_COOLDOWN.get() * ((damage / EchoConfig.JINGLEI_BASE_DAMAGE.get().floatValue()) / 0.75));
            if (faith >= EchoConfig.JINGLEI_MID_FAITH.get()) {
                actualCoolDown = actualCoolDown / 2;
            }
            
            long timeDiff = currentTime - lastUseTime;
            if (timeDiff < actualCoolDown) {
                long remainingSeconds = (actualCoolDown - timeDiff) / 20;
                player.sendSystemMessage(Component.translatable("message.doomsday.jinglei.cooldown_remaining", remainingSeconds));
                return false;
            }
        }

        // 检查理智值是否足够
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = currentSanity < EchoConfig.JINGLEI_FREE_COST_THRESHOLD.get() && faith >= EchoConfig.JINGLEI_MIN_FAITH.get();

        if (!freeCost && currentSanity < EchoConfig.JINGLEI_SANITY_COST.get()) {
            player.sendSystemMessage(Component.translatable("message.doomsday.jinglei.low_sanity"));
            return false;
        }

        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 检查是否需要消耗理智值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = currentSanity < EchoConfig.JINGLEI_FREE_COST_THRESHOLD.get() && faith >= EchoConfig.JINGLEI_MIN_FAITH.get();

        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        double range = EchoConfig.JINGLEI_RANGE.get();
        Vec3 endPos = eyePosition.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        
        BlockHitResult hitResult = player.level().clip(new ClipContext(
            eyePosition,
            endPos,
            ClipContext.Block.OUTLINE,
            ClipContext.Fluid.NONE,
            player
        ));
        
        BlockPos targetPos = hitResult.getBlockPos();
        
        // 在目标位置生成闪电
        if (player.level() instanceof ServerLevel serverLevel) {
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (lightning != null) {
                lightning.moveTo(Vec3.atBottomCenterOf(targetPos));
                lightning.setVisualOnly(false);
                // 根据信念计算伤害
                float damage = EchoConfig.JINGLEI_BASE_DAMAGE.get().floatValue() + faith * EchoConfig.JINGLEI_DAMAGE_PER_FAITH.get().floatValue();
                lightning.setDamage(damage);
                serverLevel.addFreshEntity(lightning);

                // 根据造成的伤害调整冷却时间并设置
                lastUseTime = player.level().getGameTime();
                
                // 发送信息时包含伤害和冷却信息
                if (!freeCost) {
                    int actualCost = faith >= EchoConfig.JINGLEI_MID_FAITH.get() ? EchoConfig.JINGLEI_SANITY_COST.get() / 2 : EchoConfig.JINGLEI_SANITY_COST.get();
                    SanityManager.modifySanity(player, -actualCost);
                    long actualCoolDown = (long)(EchoConfig.JINGLEI_BASE_COOLDOWN.get() * ((damage / EchoConfig.JINGLEI_BASE_DAMAGE.get().floatValue()) / 0.75));
                    if (faith >= EchoConfig.JINGLEI_MID_FAITH.get()) {
                        actualCoolDown = actualCoolDown / 2;
                    }
                    player.sendSystemMessage(Component.translatable("message.doomsday.jinglei.use_cost", 
                        actualCost, damage, actualCoolDown/20));
                } else {
                    long actualCoolDown = (long)(EchoConfig.JINGLEI_BASE_COOLDOWN.get() * ((damage / EchoConfig.JINGLEI_BASE_DAMAGE.get().floatValue()) / 0.75));
                    if (faith >= EchoConfig.JINGLEI_MID_FAITH.get()) {
                        actualCoolDown = actualCoolDown / 2;
                    }
                    player.sendSystemMessage(Component.translatable("message.doomsday.jinglei.use_free", 
                        damage, actualCoolDown/20));
                }
            }
        }

        // 更新状态
        notifyEchoClocks(player);
        updateState(player);
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.translatable("message.doomsday.jinglei.activate"));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 不需要特殊的更新逻辑
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.translatable("message.doomsday.jinglei.deactivate"));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 这是一个主动技能，不需要持续性效果
        player.sendSystemMessage(Component.translatable("message.doomsday.jinglei.not_continuous"));
    }
} 