package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;

public class WangYouEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.WANGYOU;
    private static final int SANITY_COST = 10;
    private static final int MIN_FAITH = 10;
    private static final int FREE_COST_THRESHOLD = 300;
    private static final int CONTINUOUS_SANITY_COST = 1;
    
    private int tickCounter = 0;

    public WangYouEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            SANITY_COST,
            CONTINUOUS_SANITY_COST
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...忘忧之力涌动，痛苦将化作心神之耗..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        if (isActive()) {
            tickCounter++;
            // 每秒消耗1点理智
            if (tickCounter >= 20) {
                tickCounter = 0;
                SanityManager.modifySanity(player, -CONTINUOUS_SANITY_COST);
            }
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...忘忧之力消散，痛苦重归本源..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查理智值是否足够
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = currentSanity < FREE_COST_THRESHOLD || faith >= MIN_FAITH;

        if (!freeCost && currentSanity < SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不宁，难以施展忘忧之法..."));
            return false;
        }

        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        if (!isActive()) {
            // 开启时才需要判断是否可用
            if (doCanUse(player)) {
                setActiveAndUpdate(player, true);
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...忘忧之力已开启..."));
            }
        } else {
            // 关闭时直接关闭
            setActiveAndUpdate(player, false);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...忘忧之力已关闭..."));
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putInt("tickCounter", tickCounter);
        return tag;
    }

    public static WangYouEcho fromNBT(CompoundTag tag) {
        WangYouEcho echo = new WangYouEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.tickCounter = tag.getInt("tickCounter");
        return echo;
    }
} 