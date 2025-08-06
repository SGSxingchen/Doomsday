package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class WangYouEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.WANGYOU;
    private static final int SANITY_COST = 30;
    public static final int MIN_FAITH = 10;
    public static final int FREE_COST_THRESHOLD = 300;
    private static final int CONTINUOUS_SANITY_COST = 1;
    public static final float BASE_DAMAGE_REDUCTION = 0.5F;  // 基础50%减伤
    public static final float FAITH_DAMAGE_REDUCTION = 0.1F; // 每2点信念增加10%减伤
    
    private int tickCounter = 0;

    public WangYouEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            
            SANITY_COST,
            CONTINUOUS_SANITY_COST
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        int faith = SanityManager.getFaith(player);
        float reduction = BASE_DAMAGE_REDUCTION + (faith / 2) * FAITH_DAMAGE_REDUCTION;
        // 限制最大减伤为90%
        reduction = Math.min(0.9F, reduction);
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...忘忧之力涌动，将减免")
            .append(Component.literal(String.format("%.1f", reduction * 100)))
            .append(Component.literal("%的伤害...")));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        if (isActive()) {
            tickCounter++;
            if (tickCounter >= 20) {
                tickCounter = 0;
                int currentSanity = SanityManager.getSanity(player);
                int faith = SanityManager.getFaith(player);
                boolean freeCost = currentSanity < FREE_COST_THRESHOLD || faith >= MIN_FAITH;
                if(!freeCost) {
                    SanityManager.modifySanity(player, -CONTINUOUS_SANITY_COST);
                }
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
                notifyEchoClocks(player);
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