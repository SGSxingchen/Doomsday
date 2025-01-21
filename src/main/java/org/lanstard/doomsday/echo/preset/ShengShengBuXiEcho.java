package org.lanstard.doomsday.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.lanstard.doomsday.echo.*;
import org.lanstard.doomsday.sanity.SanityManager;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

public class ShengShengBuXiEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.SHENGSHENGBUXI;
    private static final int REGENERATION_RANGE = 5;
    private int tickCounter = 0;
    
    public ShengShengBuXiEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            PRESET.getSanityConsumption(),
            PRESET.getContinuousSanityConsumption()
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f生生不息的力量在你体内流转..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        if (!isActive()) return;
        
        // 每3秒(60刻)消耗理智并检查是否需要停用
        tickCounter++;
        if (tickCounter >= 60) {
            tickCounter = 0;    
            // 检查理智值是否足够继续维持效果
            int currentSanity = SanityManager.getSanity(player);
            if (currentSanity < getContinuousSanityConsumption()) {
                // 理智不足，自动关闭效果
                setActive(false);
                onDeactivate(player);
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f理智不足，生生不息被迫停止..."));
                return;
            }
            
            // 消耗理智值
            SanityManager.modifySanity(player, -getContinuousSanityConsumption());
        }

        // 应用治疗效果
        Level level = player.level();
        AABB box = player.getBoundingBox().inflate(REGENERATION_RANGE);
        List<ServerPlayer> nearbyPlayers = level.getEntitiesOfClass(ServerPlayer.class, box);
        
        for (ServerPlayer nearbyPlayer : nearbyPlayers) {
            nearbyPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 0, false, true));
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f生生不息的力量暂时消散了..."));
        tickCounter = 0;
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查理智值是否足够激活
        int currentSanity = SanityManager.getSanity(player);
        if (currentSanity < getSanityConsumption()) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f理智不足，无法激活生生不息！"));
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
            if (!doCanUse(player)) return;
            
            // 激活并消耗理智
            SanityManager.modifySanity(player, -getSanityConsumption());
            setActive(true);
            onActivate(player);
        } else {
            // 关闭效果
            setActive(false);
            onDeactivate(player);
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putInt("tick_counter", tickCounter);
        return tag;
    }
    
    public static ShengShengBuXiEcho fromNBT(CompoundTag tag) {
        ShengShengBuXiEcho echo = new ShengShengBuXiEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.tickCounter = tag.getInt("tick_counter");
        return echo;
    }
} 