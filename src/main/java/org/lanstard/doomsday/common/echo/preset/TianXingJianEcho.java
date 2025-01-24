package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerPlayer;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;

public class TianXingJianEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.TIANXINGJIAN;
    private static final int COOLDOWN_TICKS = 2400; // 2分钟
    private static final int EFFECT_DURATION = 2400; // 2分钟
    private static final int LOW_SANITY_THRESHOLD = 300;
    private static final int FREE_COST_THRESHOLD = 300;          // 免费释放阈值
    private static final int PASSIVE_EFFECT_DURATION = 3*20; // 3秒，用于刷新永久效果
    
    private int cooldownTicks = 0;
    
    public TianXingJianEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            PRESET.getSanityConsumption()
        );
    }
    
    @Override
    public void onActivate(ServerPlayer player) {
        // 获得回响时立即给予永久抗性I
        applyPassiveEffect(player);
    }
    
    @Override
    public void onUpdate(ServerPlayer player) {
        // 更新冷却时间
        if (cooldownTicks > 0) {
            cooldownTicks--;
        }
        
        // 维持永久抗性I效果
        applyPassiveEffect(player);
    }
    
    private void applyPassiveEffect(ServerPlayer player) {
        // 给予永久抗性I（通过短时间不断刷新实现）
        player.addEffect(new MobEffectInstance(
            MobEffects.DAMAGE_RESISTANCE,
            PASSIVE_EFFECT_DURATION,
            0,  // 等级I
            false,
            false,  // 不显示粒子
            true    // 显示图标
        ));
    }
    
    @Override
    public void onDeactivate(ServerPlayer player) {
        // 移除所有效果
        player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
        player.removeEffect(MobEffects.DAMAGE_BOOST);
        player.removeEffect(MobEffects.REGENERATION);
    }
    
    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查是否被禁用
        if (this.isDisabled()) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...天行之力暂失其效..."));
            return false;
        }
        int currentSanity = SanityManager.getSanity(player);
        // 低理智时可以无视冷却
        if (currentSanity < LOW_SANITY_THRESHOLD) {
            return true;
        }
        
        // 检查冷却
        if (cooldownTicks > 0) {
            int remainingSeconds = cooldownTicks / 20;
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...天行之力尚需积蓄，剩余" + remainingSeconds + "秒..."));
            return false;
        }
        
        // 检查理智值和信念
        int faith = SanityManager.getFaith(player);
        
        // 如果信念大于等于10点且理智低于300，则不消耗理智
        if (faith >= 10 && currentSanity < FREE_COST_THRESHOLD) {
            return true;
        }
        
        // 否则检查理智是否足够
        if (currentSanity < PRESET.getSanityConsumption()) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以施展..."));
            return false;
        }
        
        return true;
    }
    
    @Override
    protected void doUse(ServerPlayer player) {
        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        
        // 如果信念大于等于10点且理智低于300，则不消耗理智
        boolean freeCost = faith >= 10 && currentSanity < FREE_COST_THRESHOLD;
        
        // 如果不是免费释放，消耗理智
        if (!freeCost) {
            SanityManager.modifySanity(player, -PRESET.getSanityConsumption());
        }
        
        // 添加效果
        if (currentSanity < LOW_SANITY_THRESHOLD) {
            // 低理智状态下，效果加强且无冷却
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, EFFECT_DURATION, 2));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, EFFECT_DURATION, 2));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, EFFECT_DURATION, 2));
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...天行健，君子以自强不息！"));
        } else {
            // 正常状态
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, EFFECT_DURATION, 1));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, EFFECT_DURATION, 1));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, EFFECT_DURATION, 1));
            cooldownTicks = COOLDOWN_TICKS;
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...天行之力加持，刚毅不屈..."));
        }
    }
    
    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putInt("cooldown", cooldownTicks);
        return tag;
    }
    
    public static TianXingJianEcho fromNBT(CompoundTag tag) {
        TianXingJianEcho echo = new TianXingJianEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.cooldownTicks = tag.getInt("cooldown");
        return echo;
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 天行健是主动技能，不需要持续性开关
    }
} 