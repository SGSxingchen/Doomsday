package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import org.lanstard.doomsday.common.echo.Echo;

import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.common.entities.ModEntities;
import org.lanstard.doomsday.common.entities.MuaEntity;

public class MaoMuEcho extends Echo {
    private static final int SANITY_COST = 20;
    private static final int MIN_FAITH = 10;
    private static final int FREE_SANITY_THRESHOLD = 300;
    private static final int COOLDOWN_TICKS = 200; // 10秒 = 10 * 20 ticks
    private long cooldownEndTime = 0;
    private static final EchoPreset PRESET = EchoPreset.MAOMU;

    public MaoMuEcho() {
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
    public void onActivate(ServerPlayer player) {
        // 激活时不需要特殊处理
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 更新时不需要特殊处理
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        // 停用时不需要特殊处理
    }

    @Override
    public boolean doCanUse(ServerPlayer player) {
        // 检查冷却时间
        // if (System.currentTimeMillis() < cooldownEndTime) {
        //     player.sendSystemMessage(Component.literal("§b[十日终焉] §f...此法尚需时日方可再施展..."));
        //     return false;
        // }
        long timeMs = cooldownEndTime - System.currentTimeMillis();
        if (timeMs > 0) {
            long remainingSeconds = timeMs / 20 / 50;
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...茂木之力尚需" + remainingSeconds + "秒恢复..."));
            return false;
        }
        
        // 检查信仰和理智
        int faith = SanityManager.getFaith(player);
        int sanity = SanityManager.getSanity(player);
        
        // 当信仰大于等于10且理智小于300时，不消耗理智
        if (faith >= MIN_FAITH && sanity < FREE_SANITY_THRESHOLD) {
            return true;
        }
        
        // 其他情况需要消耗理智
        if (sanity < SANITY_COST) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...理智不足，无法使用茂木回响"));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 检查是否需要消耗理智值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCast = faith >= MIN_FAITH && currentSanity < FREE_SANITY_THRESHOLD;
        
        // 获取玩家视线所指的方块
        double reach = 32.0D;
        var hitResult = player.pick(reach, 1.0F, false);
        
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...未找到合适的目标位置..."));
            return;
        }
        
        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos pos = blockHit.getBlockPos().above();
        
        // 只有在不满足免费释放条件时才消耗理智
        if (!freeCast) {
            SanityManager.modifySanity(player, -SANITY_COST);
        }

        // 生成茂木造物
        MuaEntity mua = new MuaEntity(ModEntities.MUA.get(), player.level());
        mua.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
        mua.setOwner(player);
        player.level().addFreshEntity(mua);
        
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...茂木之力凝聚，召唤出了一个茂木造物..."));

        // 设置冷却时间
        cooldownEndTime = System.currentTimeMillis() + (COOLDOWN_TICKS * 50); // 50ms per tick
        updateState(player);
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...此法不可持续施展..."));
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("cooldownEndTime", cooldownEndTime);
        return tag;
    }

    public static MaoMuEcho fromNBT(CompoundTag tag) {
        MaoMuEcho echo = new MaoMuEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.cooldownEndTime = tag.getLong("cooldownEndTime");
        return echo;
    }
} 