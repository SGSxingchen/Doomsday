package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;

public class TianXingJianEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.TIANXINGJIAN;
    private static final int COOL_DOWN_TICKS = 2400; // 2分钟
    private static final int EFFECT_DURATION = 2400; // 2分钟
    private static final int LOW_SANITY_THRESHOLD = 300;
    private static final int FREE_COST_THRESHOLD = 300;          // 免费释放阈值
    private static final int PASSIVE_EFFECT_DURATION = 3 * 20;   // 3秒，用于刷新永久效果
    private static final int MID_FAITH = 5;                      // 中等信念要求
    private static final int HIGH_FAITH = 10;                    // 高等信念要求
    private static final int BASE_MAX_HEALTH = 10;              // 基础增加10点最大生命值
    private static final int MID_MAX_HEALTH = 15;               // 信念5增加15点最大生命值
    private static final int HIGH_MAX_HEALTH = 20;              // 信念10增加20点最大生命值
    
    private int cooldownTicks = 0;
    
    public TianXingJianEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            
            PRESET.getSanityConsumption()
        );
    }
    
    @Override
    public void onActivate(ServerPlayer player) {
        // 获得回响时给予永久抗性，等级基于信念
        int faith = SanityManager.getFaith(player);
        int resistanceLevel = faith >= HIGH_FAITH ? 1 : (faith >= MID_FAITH ? 0 : 0);
        applyPassiveEffect(player, resistanceLevel);
    }
    
    @Override
    public void onUpdate(ServerPlayer player) {
        // 更新冷却时间
        if (cooldownTicks > 0) {
            cooldownTicks--;
        }
        
        // 维持永久抗性效果，等级基于信念
        int faith = SanityManager.getFaith(player);
        int resistanceLevel = faith >= HIGH_FAITH ? 1 : (faith >= MID_FAITH ? 0 : 0);
        applyPassiveEffect(player, resistanceLevel);
    }
    
    private void applyPassiveEffect(ServerPlayer player, int level) {
        player.addEffect(new MobEffectInstance(
            MobEffects.DAMAGE_RESISTANCE,
            PASSIVE_EFFECT_DURATION,
            level,
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
        boolean freeCost = faith >= HIGH_FAITH && currentSanity < FREE_COST_THRESHOLD;
        
        // 如果不是免费释放，消耗理智
        if (!freeCost) {
            SanityManager.modifySanity(player, -PRESET.getSanityConsumption());
        }

        // 使用属性修改器来应用生命值变化
        var attribute = player.getAttribute(Attributes.MAX_HEALTH);
        var modifierId = java.util.UUID.fromString("b0f99a89-f5c9-4624-9d38-4a1f5d8b9a91");

        // 移除旧的修改器（如果存在）
        if (attribute != null) {
            attribute.removePermanentModifier(modifierId);
        }

        // 计算效果等级
        int healthBonus;
        int resistanceLevel;
        int regenLevel;
        int strengthLevel;
        boolean noCooldown = false;

        if (currentSanity < LOW_SANITY_THRESHOLD) {
            // 低理智状态，效果最强
            healthBonus = HIGH_MAX_HEALTH;
            resistanceLevel = faith >= HIGH_FAITH ? 6 : (faith >= MID_FAITH ? 3 : 2);
            regenLevel = faith >= HIGH_FAITH ? 6 : (faith >= MID_FAITH ? 5 : 4);
            strengthLevel = faith >= HIGH_FAITH ? 6 : (faith >= MID_FAITH ? 3 : 2);
            noCooldown = true;
        } else {
            // 正常状态，效果基于信念
            healthBonus = faith >= HIGH_FAITH ? HIGH_MAX_HEALTH : (faith >= MID_FAITH ? MID_MAX_HEALTH : BASE_MAX_HEALTH);
            resistanceLevel = faith >= HIGH_FAITH ? 2 : (faith >= MID_FAITH ? 2 : 1);
            regenLevel = faith >= HIGH_FAITH ? 3 : (faith >= MID_FAITH ? 3 : 2);
            strengthLevel = faith >= HIGH_FAITH ? 2 : (faith >= MID_FAITH ? 2 : 1);
        }

        // 应用效果
        if (attribute != null) {
            attribute.addPermanentModifier(new AttributeModifier(
                modifierId,
                "TianXingJianEcho Health Modifier",
                healthBonus,
                AttributeModifier.Operation.ADDITION
            ));
        }

        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, EFFECT_DURATION, resistanceLevel));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, EFFECT_DURATION, regenLevel));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, EFFECT_DURATION, strengthLevel));

        // 设置冷却
        if (!noCooldown) {
            cooldownTicks = COOL_DOWN_TICKS;
        }

        // 发送提示信息
        if (currentSanity < LOW_SANITY_THRESHOLD) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...天行健，君子以自强不息！"));
        } else {
            String faithLevel = faith >= HIGH_FAITH ? "至高" : (faith >= MID_FAITH ? "精进" : "初始");
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...天行之力(" + faithLevel + ")加持，刚毅不屈..."));
        }
        notifyEchoClocks(player);
        updateState(player);
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