package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;

public class LiXiEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.LIXI;
    private static final int SANITY_COST = 10;               // 理智消耗
    private static final int COOLDOWN = 5 * 1;              // 0.25秒冷却
    private static final int FREE_COST_THRESHOLD = 300;      // 免费释放阈值
    private static final int MIN_FAITH_REQUIREMENT = 10;     // 最低信念要求
    
    private long cooldownEndTime = 0;

    public LiXiEcho() {
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
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...我听到了，离析的回响..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 这是一个主动技能，不需要更新逻辑
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...离析的回响渐渐消散..."));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...离析之力只能主动使用..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        long timeMs = cooldownEndTime - System.currentTimeMillis();
        if (timeMs > 0) {
            long remainingSeconds = timeMs / 20 / 50;
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...离析之力尚需" + remainingSeconds + "秒恢复..."));
            return false;
        }

        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = faith >= MIN_FAITH_REQUIREMENT && currentSanity < FREE_COST_THRESHOLD;

        // 如果不是免费释放，检查理智是否足够
        if (!freeCost && currentSanity < SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以引动离析之力..."));
            return false;
        }

        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 检查玩家指向的方块
        HitResult hitResult = player.pick(20.0D, 0.0F, false);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...未找到目标方块..."));
            return;
        }

        BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
        ServerLevel level = (ServerLevel) player.level();

        // 检查方块是否可以被破坏
        if (level.getBlockState(pos).getBlock() == Blocks.BEDROCK) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...此方块无法被离析之力影响..."));
            return;
        }

        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = faith >= MIN_FAITH_REQUIREMENT && currentSanity < FREE_COST_THRESHOLD;

        // 移除方块
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

        // 生成粒子效果
        for (int i = 0; i < 20; i++) {
            double offsetX = player.getRandom().nextGaussian() * 0.5;
            double offsetY = player.getRandom().nextGaussian() * 0.5;
            double offsetZ = player.getRandom().nextGaussian() * 0.5;
            
            level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.REVERSE_PORTAL,
                pos.getX() + 0.5 + offsetX,
                pos.getY() + 0.5 + offsetY,
                pos.getZ() + 0.5 + offsetZ,
                1,
                0, 0, 0,
                0.1
            );
        }

        // 消耗理智
        if (!freeCost) {
            SanityManager.modifySanity(player, -SANITY_COST);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + SANITY_COST + "点心神，离析之力已生效..."));
        } else {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念引导，离析之力已生效..."));
        }

        // 设置冷却
        cooldownEndTime = System.currentTimeMillis() + (COOLDOWN * 50);
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("cooldownEndTime", cooldownEndTime);
        return tag;
    }

    public static LiXiEcho fromNBT(CompoundTag tag) {
        LiXiEcho echo = new LiXiEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.cooldownEndTime = tag.getLong("cooldownEndTime");
        return echo;
    }
} 