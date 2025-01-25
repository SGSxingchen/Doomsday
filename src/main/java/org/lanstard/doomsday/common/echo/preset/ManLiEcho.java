package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import net.minecraft.nbt.CompoundTag;

public class ManLiEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.MANLI;
    private static final int EFFECT_DURATION = 20 * 60;  // 1分钟
    private static final int SANITY_COST = 150;          // 理智消耗
    private static final int FREE_COST_THRESHOLD = 300;  // 免费释放阈值
    private static final int MIN_BELIEF = 10;            // 最小信念要求
    private static final int STRENGTH_AMPLIFIER = 1;     // 力量2效果
    private static final int RESISTANCE_AMPLIFIER = 0;   // 抗性1效果
    private static final int INCREASE_MAX_HEALTH = 40;   // 提升最大生命值
    
    private long cooldownEndTime = 0;                    // 冷却结束时间

    public ManLiEcho() {
        super(PRESET.name().toLowerCase(), PRESET.getDisplayName(), PRESET.getType(), PRESET.getActivationType(), SANITY_COST, 0);
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...蛮力回响在耳，力量涌动..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 这是一个主动技能，不需要持续更新
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...蛮力消散，归于平静..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查冷却
        if (System.currentTimeMillis() < cooldownEndTime) {
            long remainingSeconds = (cooldownEndTime - System.currentTimeMillis()) / 1000;
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...蛮力尚需等待" + remainingSeconds + "秒..."));
            return false;
        }

        // 检查是否可以免费释放
        int currentSanity = SanityManager.getSanity(player);
        boolean isFree = SanityManager.getFaith(player) >= MIN_BELIEF && currentSanity < FREE_COST_THRESHOLD;
        
        // 如果不能免费释放，检查理智是否足够
        if (!isFree && currentSanity < SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以引动蛮力..."));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 检查是否可以免费释放
        boolean isFree = SanityManager.getFaith(player) >= MIN_BELIEF && 
                        SanityManager.getSanity(player) < FREE_COST_THRESHOLD;
        
        // 消耗理智
        if (!isFree) {
            SanityManager.modifySanity(player, -SANITY_COST);
        }

        // 使用属性修改器来应用生命值变化
        var attribute = player.getAttribute(Attributes.MAX_HEALTH);
        var modifierId = java.util.UUID.fromString("b9c99a89-f5c9-4624-9d38-4a1f5d8b9a91"); // 固定UUID用于识别这个修改器

        // 移除旧的修改器（如果存在）
        if (attribute != null) {
            attribute.removePermanentModifier(modifierId);
        }

        // 只有在有修改时才添加修改器
        if (attribute != null) {
            attribute.addPermanentModifier(new AttributeModifier(
                    modifierId,
                    "ManliEcho Health Modifier",
                    INCREASE_MAX_HEALTH,
                    AttributeModifier.Operation.ADDITION
            ));
        }

        // 添加效果
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, EFFECT_DURATION, STRENGTH_AMPLIFIER, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, EFFECT_DURATION, RESISTANCE_AMPLIFIER, false, true));
        
        // 设置冷却
        cooldownEndTime = System.currentTimeMillis() + EFFECT_DURATION;
        
        // 发送消息
        if (isFree) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念引动蛮力，力量涌现..."));
        } else {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + SANITY_COST + "点心神，引动蛮力..."));
        }
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 这是一个主动技能，不需要切换持续状态
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...蛮力需要主动引导..."));
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("cooldownEndTime", cooldownEndTime);
        return tag;
    }
    
    public static ManLiEcho fromNBT(CompoundTag tag) {
        ManLiEcho echo = new ManLiEcho();
        echo.cooldownEndTime = tag.getLong("cooldownEndTime");
        return echo;
    }
} 