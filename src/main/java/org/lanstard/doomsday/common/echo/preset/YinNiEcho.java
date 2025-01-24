package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.chat.Component;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import net.minecraft.nbt.CompoundTag;

public class YinNiEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.YINNI;
    private static final int TOGGLE_SANITY_COST = 30;        // 开启消耗
    private static final int CONTINUOUS_SANITY_COST = 1;     // 每秒消耗
    private static final int FREE_COST_THRESHOLD = 300;      // 免费释放阈值
    private static final int MIN_BELIEF = 10;                // 最小信念要求
    
    private int tickCounter = 0;                             // 用于计时每秒消耗

    public YinNiEcho() {
        super(PRESET.name().toLowerCase(), PRESET.getDisplayName(), PRESET.getType(), PRESET.getActivationType(), TOGGLE_SANITY_COST, CONTINUOUS_SANITY_COST);
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...隐匿之术已成，身形渐隐..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        if (!isActive()) return;
        
        tickCounter++;
        if (tickCounter >= 20) { // 每秒检查一次
            tickCounter = 0;
            
            // 检查理智是否足够继续维持
            int currentSanity = SanityManager.getSanity(player);
            boolean isFree = SanityManager.getFaith(player) >= MIN_BELIEF && currentSanity < FREE_COST_THRESHOLD;
            
            if (!isFree) {
                if (currentSanity < CONTINUOUS_SANITY_COST) {
                    // 理智不足，强制关闭
                    toggleContinuous(player);
                    player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神已竭，隐匿之术难以为继..."));
                    return;
                }
                
                // 消耗理智
                SanityManager.modifySanity(player, -CONTINUOUS_SANITY_COST);
            }

            // 刷新隐身效果
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 40, 0, false, false));
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        // 移除隐身效果
        player.removeEffect(MobEffects.INVISIBILITY);
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...隐匿消散，身形显现..."));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        if (!isActive()) {
            // 检查是否可以免费释放
            int currentSanity = SanityManager.getSanity(player);
            boolean isFree = SanityManager.getFaith(player) >= MIN_BELIEF && currentSanity < FREE_COST_THRESHOLD;
            
            // 如果不能免费释放，检查理智是否足够
            if (!isFree && currentSanity < TOGGLE_SANITY_COST) {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以施展隐匿之术..."));
                return;
            }
            
            // 消耗理智
            if (!isFree) {
                SanityManager.modifySanity(player, -TOGGLE_SANITY_COST);
            }
            
            // 激活效果
            setActive(true);
            onActivate(player);
            
        } else {
            // 关闭效果
            setActive(false);
            onDeactivate(player);
        }
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 这是一个持续性技能，不需要主动使用
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...隐匿之术需要持续引导..."));
        return false;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 这是一个持续性技能，不需要主动使用
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putInt("tickCounter", tickCounter);
        return tag;
    }
    
    public static YinNiEcho fromNBT(CompoundTag tag) {
        YinNiEcho echo = new YinNiEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.tickCounter = tag.getInt("tickCounter");
        return echo;
    }
} 