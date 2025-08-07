package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.lanstard.doomsday.common.echo.BasicEcho;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.effects.ModEffects;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.common.data.HuntData;
import org.lanstard.doomsday.common.items.hunt.HuntCompassItem;
import org.lanstard.doomsday.common.items.hunt.EndBladeItem;
import org.lanstard.doomsday.common.items.hunt.InvisibilityCloakItem;
import org.lanstard.doomsday.config.EchoConfig;

import java.util.List;
import java.util.Optional;

public class WuZhongShouEcho extends Echo {
    
    private static final EchoPreset PRESET = EchoPreset.WUZHONGSHOU;
    
    public WuZhongShouEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            EchoConfig.WUZHONGSHOU_MARK_SANITY_COST.get(),
            0
        );
    }
    
    @Override
    public void onActivate(ServerPlayer player) {
        // 激活时开启狩猎本能效果
        int faith = SanityManager.getFaith(player);
        int huntInstinctLevel = Math.max(0, faith); // 信念值直接作为效果等级
        
        player.addEffect(new MobEffectInstance(
            ModEffects.HUNT_INSTINCT.get(),
            Integer.MAX_VALUE, // 无限持续，直到手动关闭
            huntInstinctLevel,
            false,
            true,
            true
        ));
        
        player.sendSystemMessage(Component.translatable("message.doomsday.wuzhongshou.hunt_instinct_activated"));
    }
    
    @Override
    public void onUpdate(ServerPlayer player) {
        // 如果处于激活状态，维持狩猎本能效果
        if (isActive()) {
            int faith = SanityManager.getFaith(player);
            int huntInstinctLevel = Math.max(0, faith); // 信念值直接作为效果等级
            
            MobEffectInstance currentEffect = player.getEffect(ModEffects.HUNT_INSTINCT.get());
            
            // 如果没有效果或等级不匹配，重新应用
            if (currentEffect == null || currentEffect.getAmplifier() != huntInstinctLevel) {
                // 先移除旧效果
                player.removeEffect(ModEffects.HUNT_INSTINCT.get());
                
                // 添加新效果（无限持续）
                player.addEffect(new MobEffectInstance(
                    ModEffects.HUNT_INSTINCT.get(),
                    Integer.MAX_VALUE,
                    huntInstinctLevel,
                    false,
                    true,
                    true
                ));
            }
        }
    }
    
    @Override
    public void onDeactivate(ServerPlayer player) {
        // 移除狩猎本能效果
        player.removeEffect(ModEffects.HUNT_INSTINCT.get());
        player.sendSystemMessage(Component.translatable("message.doomsday.wuzhongshou.hunt_instinct_deactivated"));
    }
    
    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查理智值
        int sanity = SanityManager.getSanity(player);
        if (sanity < EchoConfig.WUZHONGSHOU_MARK_SANITY_COST.get()) {
            player.sendSystemMessage(Component.translatable("message.doomsday.wuzhongshou.low_sanity"));
            return false;
        }
        
        // 检查冷却
        if (player.level() instanceof ServerLevel serverLevel) {
            HuntData huntData = HuntData.get(serverLevel);
            if (huntData.isOnMarkCooldown(player.getUUID())) {
                long remainingMs = huntData.getMarkCooldownRemaining(player.getUUID());
                long remainingMinutes = remainingMs / 60000;
                long remainingSeconds = (remainingMs % 60000) / 1000;
                
                player.sendSystemMessage(Component.translatable("message.doomsday.wuzhongshou.on_cooldown",
                    remainingMinutes, remainingSeconds));
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    protected void doUse(ServerPlayer player) {
        // 获取目标玩家
        ServerPlayer target = getTargetPlayer(player);
        if (target == null) {
            player.sendSystemMessage(Component.translatable("message.doomsday.wuzhongshou.no_target"));
            return;
        }
        
        if (target == player) {
            player.sendSystemMessage(Component.translatable("message.doomsday.wuzhongshou.self_target"));
            return;
        }
        
        // 检查目标是否已被标记
        if (target.hasEffect(ModEffects.HUNTED_MARK.get())) {
            player.sendSystemMessage(Component.translatable("message.doomsday.wuzhongshou.already_marked"));
            return;
        }
        
        // 消耗理智值
        SanityManager.modifySanity(player, -EchoConfig.WUZHONGSHOU_MARK_SANITY_COST.get());
        
        // 为目标添加被狩猎标记
        MobEffectInstance huntedMarkEffect = new MobEffectInstance(
            ModEffects.HUNTED_MARK.get(),
            EchoConfig.WUZHONGSHOU_HUNT_DURATION_TICKS.get(),
            0,
            false,
            true,
            true
        );
        target.addEffect(huntedMarkEffect);
        
        // 在HuntData中记录狩猎信息并设置冷却
        if (player.level() instanceof ServerLevel serverLevel) {
            HuntData huntData = HuntData.get(serverLevel);
            huntData.startHunt(player.getUUID(), target.getUUID(), target.getDisplayName().getString(), System.currentTimeMillis());
            // 设置标记冷却
            huntData.setMarkCooldown(player.getUUID());
        }
        
        // 给予狩猎者道具
        giveHuntItems(player, target);
        
        // 发送消息
        player.sendSystemMessage(Component.translatable("message.doomsday.wuzhongshou.mark_success")
            .append(target.getDisplayName()));
        target.sendSystemMessage(Component.translatable("message.doomsday.wuzhongshou.marked_by")
            .append(player.getDisplayName()));
        
        // 更新状态和通知回响钟
        updateState(player);
        notifyEchoClocks(player);
    }
    
    @Override
    public void toggleContinuous(ServerPlayer player) {
        if (!isActive()) {
            // 开启时激活
            setActiveAndUpdate(player, true);
            onActivate(player);
        } else {
            // 关闭时停用
            setActiveAndUpdate(player, false);
            onDeactivate(player);
        }
    }
    
    /**
     * 获取目标玩家
     */
    private ServerPlayer getTargetPlayer(ServerPlayer player) {
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        Vec3 endPos = eyePosition.add(lookVector.scale(EchoConfig.WUZHONGSHOU_BASE_REACH.get()));
        
        AABB searchBox = new AABB(eyePosition, endPos).inflate(EchoConfig.WUZHONGSHOU_TARGET_BOX_INFLATE.get());
        
        List<ServerPlayer> nearbyPlayers = player.level().getEntitiesOfClass(ServerPlayer.class, searchBox,
            target -> target != player && target.isPickable() && target.isAlive());
        
        ServerPlayer closestTarget = null;
        double minDistance = Double.MAX_VALUE;
        
        for (ServerPlayer potentialTarget : nearbyPlayers) {
            AABB hitBox = potentialTarget.getBoundingBox();
            Optional<Vec3> intersection = hitBox.clip(eyePosition, endPos);
            
            if (intersection.isPresent()) {
                double distance = eyePosition.distanceToSqr(intersection.get());
                if (distance < minDistance) {
                    minDistance = distance;
                    closestTarget = potentialTarget;
                }
            }
        }
        
        return closestTarget;
    }
    
    /**
     * 给予狩猎道具
     */
    private void giveHuntItems(ServerPlayer hunter, ServerPlayer target) {
        // 1. 追踪指南针
        ItemStack compass = HuntCompassItem.createHuntCompass(target.getUUID(), target.getDisplayName().getString());
        hunter.addItem(compass);
        
        // 2. 终末匕首
        ItemStack blade = EndBladeItem.createEndBlade();
        hunter.addItem(blade);
        
        // 3. 隐身斗篷
        ItemStack cloak = InvisibilityCloakItem.createInvisibilityCloak();
        hunter.addItem(cloak);
        
        // 4. 末影珍珠（数量可配置）
        int enderPearlCount = EchoConfig.WUZHONGSHOU_ENDER_PEARL_COUNT.get();
        for (int i = 0; i < enderPearlCount; i++) {
            ItemStack enderPearl = new ItemStack(Items.ENDER_PEARL);
            // 标记为狩猎物品
            enderPearl.getOrCreateTag().putBoolean("hunt_item", true);
            enderPearl.enchant(net.minecraft.world.item.enchantment.Enchantments.VANISHING_CURSE, 1);
            hunter.addItem(enderPearl);
        }
        
        hunter.sendSystemMessage(Component.translatable("message.doomsday.wuzhongshou.items_given"));
    }
    
    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        return tag;
    }
    
    public static WuZhongShouEcho fromNBT(CompoundTag tag) {
        WuZhongShouEcho echo = new WuZhongShouEcho();
        echo.setActive(tag.getBoolean("isActive"));
        return echo;
    }
}