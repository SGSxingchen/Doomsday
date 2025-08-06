package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.config.EchoConfig;

public class YingHuaEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.YINGHUA;

    public YingHuaEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            EchoConfig.YINGHUA_SANITY_COST.get(),
            EchoConfig.YINGHUA_CONTINUOUS_SANITY_COST.get()
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        // 永久效果：受到伤害时不会被击退 - 这个效果通过事件处理器实现
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        if (isActive()) {
            // 获取玩家的信念值
            int faith = SanityManager.getFaith(player);
            
            // 根据信念值决定抗性等级
            int resistanceLevel = faith >= EchoConfig.YINGHUA_HIGH_FAITH_THRESHOLD.get() ? 1 : 0; // 抗性2 = level 1
            
            // 应用抗性效果
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 
                60, // 3秒持续时间，需要持续更新
                resistanceLevel, 
                false, 
                false, 
                true));
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        // 移除抗性效果
        player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        return true; // 硬化没有特殊的使用条件
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 硬化是被动效果，不需要主动使用
        player.sendSystemMessage(Component.translatable("message.doomsday.yinghua.passive"));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        if (!isActive()) {
            // 开启持续模式
            setActiveAndUpdate(player, true);
            onActivate(player);
            player.sendSystemMessage(Component.translatable("message.doomsday.yinghua.continuous_on"));
        } else {
            // 关闭持续模式
            setActiveAndUpdate(player, false);
            onDeactivate(player);
            player.sendSystemMessage(Component.translatable("message.doomsday.yinghua.continuous_off"));
        }
    }

    /**
     * 处理玩家受到伤害时的反弹效果
     * 这个方法应该在伤害事件处理器中调用
     */
    public void onPlayerHurt(ServerPlayer player, DamageSource damageSource, float amount) {
        if (isActive() && damageSource.getEntity() instanceof LivingEntity attacker) {
            // 反弹固定伤害
            float reflectDamage = EchoConfig.YINGHUA_REFLECT_DAMAGE.get().floatValue();
            attacker.hurt(attacker.damageSources().magic(), reflectDamage);
            
            // 扣除理智
            int sanityCost = EchoConfig.YINGHUA_DAMAGE_SANITY_COST.get();
            SanityManager.modifySanity(player, -sanityCost);
            
            // 更新状态
            updateState(player);
            notifyEchoClocks(player);
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        return tag;
    }

    public static YingHuaEcho fromNBT(CompoundTag tag) {
        YingHuaEcho echo = new YingHuaEcho();
        echo.setActive(tag.getBoolean("isActive"));
        return echo;
    }
}