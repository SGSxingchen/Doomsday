package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.config.EchoConfig;

public class NaGouEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.NAGOU;

    public NaGouEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            EchoConfig.NAGOU_SANITY_COST.get(),
            0
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.translatable("message.doomsday.nagou.activate"));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        if (isActive()) {
            // 持续应用缓慢II效果
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 
                60, // 3秒持续时间，需要持续更新
                1, // 缓慢II = level 1
                false, 
                false, 
                true));
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        // 移除缓慢效果
        player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        player.sendSystemMessage(Component.translatable("message.doomsday.nagou.deactivate"));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        return true; // 纳垢没有特殊的使用条件
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 纳垢是被动效果，不需要主动使用
        player.sendSystemMessage(Component.translatable("message.doomsday.nagou.passive"));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        if (!isActive()) {
            // 开启持续模式
            setActiveAndUpdate(player, true);
            onActivate(player);
        } else {
            // 关闭持续模式
            setActiveAndUpdate(player, false);
            onDeactivate(player);
        }
    }

    /**
     * 处理玩家受到伤害时的泥土抵挡效果
     * 这个方法应该在伤害事件处理器中调用
     */
    public boolean tryAbsorbDamage(ServerPlayer player, DamageSource damageSource, float amount) {
        if (!isActive()) {
            return false;
        }

        // 获取玩家信念值
        int faith = SanityManager.getFaith(player);
        
        // 根据信念值确定泥土消耗量
        int dirtCost = faith >= EchoConfig.NAGOU_HIGH_FAITH_THRESHOLD.get() ? 
            EchoConfig.NAGOU_DIRT_COST_HIGH_FAITH.get() : 
            EchoConfig.NAGOU_DIRT_COST_NORMAL.get();

        // 检查并消耗泥土
        if (consumeDirt(player, dirtCost)) {
            // 发送消息
            player.sendSystemMessage(Component.translatable("message.doomsday.nagou.absorb_damage", dirtCost));
            return true; // 成功抵挡伤害
        } else {
            // 泥土不够，发送警告
            player.sendSystemMessage(Component.translatable("message.doomsday.nagou.insufficient_dirt", dirtCost));
            return false;
        }
    }

    /**
     * 消耗玩家背包中的泥土
     */
    private boolean consumeDirt(ServerPlayer player, int amount) {
        var inventory = player.getInventory();
        int dirtFound = 0;
        
        // 先统计总泥土数量
        for (ItemStack stack : inventory.items) {
            if (stack.getItem() == Items.DIRT) {
                dirtFound += stack.getCount();
            }
        }
        
        // 检查是否有足够的泥土
        if (dirtFound < amount) {
            return false;
        }
        
        // 开始消耗泥土
        int remainingToConsume = amount;
        for (int i = 0; i < inventory.items.size() && remainingToConsume > 0; i++) {
            ItemStack stack = inventory.items.get(i);
            if (stack.getItem() == Items.DIRT) {
                int consume = Math.min(stack.getCount(), remainingToConsume);
                stack.shrink(consume);
                remainingToConsume -= consume;
            }
        }
        
        return true;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        return tag;
    }

    public static NaGouEcho fromNBT(CompoundTag tag) {
        NaGouEcho echo = new NaGouEcho();
        echo.setActive(tag.getBoolean("isActive"));
        return echo;
    }
}