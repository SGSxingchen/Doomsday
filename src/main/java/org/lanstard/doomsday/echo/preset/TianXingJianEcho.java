package org.lanstard.doomsday.echo.preset;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerPlayer;
import org.lanstard.doomsday.echo.Echo;
import org.lanstard.doomsday.echo.EchoPreset;
import org.lanstard.doomsday.sanity.SanityManager;

public class TianXingJianEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.TIANXINGJIAN;
    private static final int COOLDOWN_TICKS = 2400; // 2分钟 = 2 * 60 * 20 ticks
    private static final int EFFECT_DURATION = 2400; // 2分钟
    private static final int LOW_SANITY_THRESHOLD = 200;
    
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
    }
    
    @Override
    public void onUpdate(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE,
                -1, // 无限持续
                0,  // 等级I
                false,
                false
        ));
        if (cooldownTicks > 0) {
            cooldownTicks--;
        }
    }
    
    @Override
    public void onDeactivate(ServerPlayer player) {
        // 移除永久效果
        player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
    }
    
    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查冷却和理智值
        int currentSanity = SanityManager.getSanity(player);
        return cooldownTicks <= 0 || currentSanity < LOW_SANITY_THRESHOLD;
    }
    
    @Override
    protected void doUse(ServerPlayer player) {
        int currentSanity = SanityManager.getSanity(player);
        // 添加增益效果
        player.addEffect(new MobEffectInstance(
            MobEffects.DAMAGE_RESISTANCE,
            EFFECT_DURATION,
            2  // 等级III
        ));
        
        player.addEffect(new MobEffectInstance(
            MobEffects.DAMAGE_BOOST,
            EFFECT_DURATION,
            2  // 等级III
        ));
        
        player.addEffect(new MobEffectInstance(
            MobEffects.REGENERATION,
            EFFECT_DURATION,
            0  // 等级I
        ));

        if (currentSanity >= LOW_SANITY_THRESHOLD) {
            cooldownTicks = COOLDOWN_TICKS;
        }

        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...你使用了回响 ")
            .append(Component.literal(this.getName()).withStyle(ChatFormatting.YELLOW)));
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
    public void toggleContinuous(ServerPlayer player) {}
} 