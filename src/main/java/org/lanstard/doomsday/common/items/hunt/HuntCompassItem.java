package org.lanstard.doomsday.common.items.hunt;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.lanstard.doomsday.common.data.HuntData;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class HuntCompassItem extends CompassItem {
    
    public HuntCompassItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }
    
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide() && entity instanceof ServerPlayer player) {
            updateCompass(stack, player);
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }
    
    private void updateCompass(ItemStack stack, ServerPlayer hunter) {
        CompoundTag nbt = stack.getOrCreateTag();
        
        // 获取目标UUID
        if (!nbt.hasUUID("target")) {
            return;
        }
        
        UUID targetUUID = nbt.getUUID("target");
        ServerLevel serverLevel = hunter.serverLevel();
        HuntData huntData = HuntData.get(serverLevel);
        
        // 验证狩猎状态
        if (!huntData.isValidHunt(hunter.getUUID(), targetUUID)) {
            // 狩猎无效，清除指南针
            stack.shrink(1);
            return;
        }
        
        // 查找目标玩家
        ServerPlayer target = serverLevel.getServer().getPlayerList().getPlayer(targetUUID);
        if (target != null && target.level() == hunter.level()) {
            // 更新指南针指向目标位置
            BlockPos targetPos = target.blockPosition();
            nbt.putInt("LodestonePos.X", targetPos.getX());
            nbt.putInt("LodestonePos.Y", targetPos.getY());
            nbt.putInt("LodestonePos.Z", targetPos.getZ());
            nbt.putString("LodestoneDimension", target.level().dimension().location().toString());
            nbt.putBoolean("LodestoneTracked", false);
            
            // 更新目标信息显示
            nbt.putString("targetName", target.getDisplayName().getString());
            nbt.putDouble("distance", hunter.distanceTo(target));
        } else {
            // 目标不在线或不在同一世界
            nbt.putString("targetName", "目标离线");
            nbt.putDouble("distance", -1);
        }
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide()) {
            CompoundTag nbt = stack.getOrCreateTag();
            
            if (nbt.hasUUID("target")) {
                UUID targetUUID = nbt.getUUID("target");
                ServerLevel serverLevel = (ServerLevel) level;
                ServerPlayer target = serverLevel.getServer().getPlayerList().getPlayer(targetUUID);
                
                if (target != null) {
                    double distance = player.distanceTo(target);
                    player.sendSystemMessage(Component.literal("§c目标距离: §f" + String.format("%.1f", distance) + " 格"));
                    
                    if (distance <= 30) {
                        player.sendSystemMessage(Component.literal("§4⚠ 警告：目标就在附近！"));
                    }
                } else {
                    player.sendSystemMessage(Component.literal("§7目标离线或不在同一世界"));
                }
            }
        }
        
        return InteractionResultHolder.success(stack);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag nbt = stack.getOrCreateTag();
        
        if (nbt.hasUUID("target")) {
            String targetName = nbt.getString("targetName");
            double distance = nbt.getDouble("distance");
            
            tooltip.add(Component.literal("§6目标: §f" + targetName).withStyle(ChatFormatting.GOLD));
            
            if (distance >= 0) {
                tooltip.add(Component.literal("§6距离: §f" + String.format("%.1f", distance) + " 格").withStyle(ChatFormatting.GOLD));
                
                if (distance <= 30) {
                    tooltip.add(Component.literal("§4⚠ 目标就在附近！").withStyle(ChatFormatting.DARK_RED));
                }
            } else {
                tooltip.add(Component.literal("§7目标离线").withStyle(ChatFormatting.GRAY));
            }
        } else {
            tooltip.add(Component.literal("§7未绑定目标").withStyle(ChatFormatting.GRAY));
        }
        
        tooltip.add(Component.literal("§7右键查看详细信息").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("§c消失诅咒 - 无法丢弃").withStyle(ChatFormatting.RED));
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // 始终发光
    }
    
    @Override
    public boolean canBeDepleted() {
        return false; // 不会损坏
    }
    
    public static ItemStack createHuntCompass(UUID targetUUID, String targetName) {
        ItemStack compass = new ItemStack(org.lanstard.doomsday.common.items.ModItem.HUNT_COMPASS.get());
        CompoundTag nbt = compass.getOrCreateTag();
        
        // 设置目标信息
        nbt.putUUID("target", targetUUID);
        nbt.putString("targetName", targetName);
        nbt.putDouble("distance", -1);
        
        // 添加消失诅咒
        compass.enchant(Enchantments.VANISHING_CURSE, 1);
        
        return compass;
    }
}