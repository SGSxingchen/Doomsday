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

public class JingLeiEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.JINGLEI;
    private static final int SANITY_COST = 20;
    private static final int MIN_FAITH = 10;
    private static final int FREE_COST_THRESHOLD = 300;
    private static final int RANGE = 64;
    private static final int COOLDOWN = 200; // 10秒 = 200tick
    private static final float DAMAGE = 8.0f;
    
    private long lastUseTime = 0;

    public JingLeiEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            SANITY_COST,
            0
        );
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查冷却时间
        long currentTime = player.level().getGameTime();
        if (lastUseTime > 0) {
            long timeDiff = currentTime - lastUseTime;
            if (timeDiff < COOLDOWN) {
                long remainingSeconds = (COOLDOWN - timeDiff) / 20;
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...惊雷之力尚需" + remainingSeconds + "秒恢复..."));
                return false;
            }
        }

        // 检查理智值是否足够
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = currentSanity < FREE_COST_THRESHOLD || faith >= MIN_FAITH;

        if (!freeCost && currentSanity < SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不宁，难以施展惊雷之法..."));
            return false;
        }

        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 获取玩家视线所指的方块位置
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = eyePosition.add(lookVec.x * RANGE, lookVec.y * RANGE, lookVec.z * RANGE);
        
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
                lightning.setDamage(DAMAGE);
                serverLevel.addFreshEntity(lightning);
            }
        }

        // 检查是否需要消耗理智值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = currentSanity < FREE_COST_THRESHOLD || faith >= MIN_FAITH;

        // 如果不是免费释放，消耗理智值
        if (!freeCost) {
            SanityManager.modifySanity(player, -SANITY_COST);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗了" + SANITY_COST + "点心神之力..."));
        }

        // 更新最后使用时间
        lastUseTime = player.level().getGameTime();
        
        // 更新状态
        updateState(player);
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...惊雷之力涌动，天地为之变色..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 不需要特殊的更新逻辑
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...惊雷之力消散..."));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 这是一个主动技能，不需要持续性效果
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...惊雷之法不是持续性回响..."));
    }
} 