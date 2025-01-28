package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoType;
import org.lanstard.doomsday.common.echo.ActivationType;
import org.lanstard.doomsday.common.sanity.SanityManager;
import net.minecraft.nbt.CompoundTag;

public class YueQianEcho extends Echo {
    private static final int SANITY_COST = 50;
    private static final int MIN_FAITH = 10;
    private static final int FREE_COST_THRESHOLD = 300;
    private static final int MAX_RANGE = 64;
    private static final int COOL_DOWN = 30 * 20; // 1分钟 = 20tick * 60
    
    private long lastUseTime = 0;

    public YueQianEcho() {
        super("yueqian", "跃迁", EchoType.ACTIVE, ActivationType.TRIGGER, SANITY_COST);
    }

    @Override
    public void onActivate(ServerPlayer player) {
        // 主动技能，不需要激活逻辑
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 主动技能，不需要更新逻辑
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        // 主动技能，不需要停用逻辑
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 主动技能，不支持持续施展
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...跃迁之法不支持持续施展..."));
    }

    @Override
    public boolean doCanUse(ServerPlayer player) {
        // 检查冷却时间
        long currentTime = player.level().getGameTime();
        if(SanityManager.getFaith(player) >= 5) {
            if (currentTime - lastUseTime < COOL_DOWN / 10) {
                int remainingSeconds = (int)(((COOL_DOWN / 10) - (currentTime - lastUseTime)) / 20);
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...跃迁之法尚需" + remainingSeconds + "秒冷却..."));
                return false;
            }
        }
        else{
            if (currentTime - lastUseTime < COOL_DOWN) {
                int remainingSeconds = (int)((COOL_DOWN - (currentTime - lastUseTime)) / 20);
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...跃迁之法尚需" + remainingSeconds + "秒冷却..."));
                return false;
            }
        }


        int currentSanity = SanityManager.getSanity(player);
        int beliefLevel = SanityManager.getBeliefLevel(player);

        if (beliefLevel >= MIN_FAITH && currentSanity >= FREE_COST_THRESHOLD) {
            return true;
        }

        // 检查理智值是否足够
        if (currentSanity < SANITY_COST && currentSanity >= FREE_COST_THRESHOLD) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不宁，难以施展跃迁之法..."));
            return false;
        }

        return true;
    }

    @Override
    public void doUse(ServerPlayer player) {
        // 获取玩家视线方向的目标方块
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        Vec3 endPos = eyePosition.add(lookVector.x * MAX_RANGE, lookVector.y * MAX_RANGE, lookVector.z * MAX_RANGE);
        
        Level level = player.level();
        BlockHitResult hitResult = level.clip(new ClipContext(
            eyePosition,
            endPos,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            player
        ));

        // 获取目标位置
        BlockPos targetPos = hitResult.getBlockPos().above();
        
        // 如果目标位置和上方一格都不是空气，寻找最近的安全位置
        while (!level.getBlockState(targetPos).isAir() || !level.getBlockState(targetPos.above()).isAir()) {
            targetPos = targetPos.above();
            if (targetPos.getY() >= level.getMaxBuildHeight()) {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...目标位置不安全，无法跃迁..."));
                return;
            }
        }

        // 传送玩家
        player.teleportTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...跃迁之法，瞬息千里..."));

        // 消耗理智值（仅当信念点数大于等于10且理智值大于等于300时消耗）
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        if (currentSanity >= FREE_COST_THRESHOLD || faith < MIN_FAITH) {
            SanityManager.modifySanity(player, -SANITY_COST);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗了" + SANITY_COST + "点心神之力..."));
        }

        // 更新冷却时间
        lastUseTime = level.getGameTime();
        updateState(player);
        notifyEchoClocks(player);

    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("lastUseTime", lastUseTime);
        return tag;
    }

    public static YueQianEcho fromNBT(CompoundTag tag) {
        YueQianEcho echo = new YueQianEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.lastUseTime = tag.getLong("lastUseTime");
        return echo;
    }
} 