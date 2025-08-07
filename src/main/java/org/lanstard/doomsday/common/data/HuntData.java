package org.lanstard.doomsday.common.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.config.EchoConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HuntData extends SavedData {
    
    // 存储狩猎数据的映射 hunter UUID -> HuntInfo
    private final Map<UUID, HuntInfo> activeHunts = new HashMap<>();
    
    // 存储标记冷却数据的映射 hunter UUID -> 冷却结束时间戳
    private final Map<UUID, Long> markCooldowns = new HashMap<>();
    
    public HuntData() {
        super();
    }
    
    public static HuntData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            HuntData::load, 
            HuntData::new, 
            "hunt_data"
        );
    }
    
    public static HuntData load(CompoundTag tag) {
        HuntData huntData = new HuntData();
        
        ListTag huntList = tag.getList("hunts", Tag.TAG_COMPOUND);
        for (int i = 0; i < huntList.size(); i++) {
            CompoundTag huntTag = huntList.getCompound(i);
            
            UUID hunterUUID = huntTag.getUUID("hunter");
            UUID targetUUID = huntTag.getUUID("target");
            String targetName = huntTag.getString("targetName");
            long startTime = huntTag.getLong("startTime");
            
            HuntInfo huntInfo = new HuntInfo(hunterUUID, targetUUID, targetName, startTime);
            huntData.activeHunts.put(hunterUUID, huntInfo);
        }
        
        // 加载标记冷却数据
        ListTag cooldownList = tag.getList("cooldowns", Tag.TAG_COMPOUND);
        for (int i = 0; i < cooldownList.size(); i++) {
            CompoundTag cooldownTag = cooldownList.getCompound(i);
            
            UUID hunterUUID = cooldownTag.getUUID("hunter");
            long endTime = cooldownTag.getLong("endTime");
            
            huntData.markCooldowns.put(hunterUUID, endTime);
        }
        
        return huntData;
    }
    
    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag huntList = new ListTag();
        
        for (HuntInfo huntInfo : activeHunts.values()) {
            CompoundTag huntTag = new CompoundTag();
            huntTag.putUUID("hunter", huntInfo.hunterUUID);
            huntTag.putUUID("target", huntInfo.targetUUID);
            huntTag.putString("targetName", huntInfo.targetName);
            huntTag.putLong("startTime", huntInfo.startTime);
            huntList.add(huntTag);
        }
        
        tag.put("hunts", huntList);
        
        // 保存标记冷却数据
        ListTag cooldownList = new ListTag();
        for (Map.Entry<UUID, Long> cooldownEntry : markCooldowns.entrySet()) {
            CompoundTag cooldownTag = new CompoundTag();
            cooldownTag.putUUID("hunter", cooldownEntry.getKey());
            cooldownTag.putLong("endTime", cooldownEntry.getValue());
            cooldownList.add(cooldownTag);
        }
        tag.put("cooldowns", cooldownList);
        
        return tag;
    }
    
    /**
     * 检查玩家是否处于标记冷却中
     */
    public boolean isOnMarkCooldown(UUID hunterUUID) {
        Long endTime = markCooldowns.get(hunterUUID);
        if (endTime == null) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime >= endTime) {
            // 冷却已结束，清除记录
            markCooldowns.remove(hunterUUID);
            setDirty();
            return false;
        }
        
        return true;
    }
    
    /**
     * 获取标记冷却剩余时间（毫秒）
     */
    public long getMarkCooldownRemaining(UUID hunterUUID) {
        Long endTime = markCooldowns.get(hunterUUID);
        if (endTime == null) {
            return 0;
        }
        
        long currentTime = System.currentTimeMillis();
        return Math.max(0, endTime - currentTime);
    }
    
    /**
     * 设置标记冷却
     */
    public void setMarkCooldown(UUID hunterUUID) {
        long currentTime = System.currentTimeMillis();
        long cooldownDuration = EchoConfig.WUZHONGSHOU_MARK_COOLDOWN_TICKS.get() * 50; // 转换为毫秒
        long endTime = currentTime + cooldownDuration;
        
        markCooldowns.put(hunterUUID, endTime);
        setDirty();
    }
    
    /**
     * 开始狩猎
     */
    public void startHunt(UUID hunterUUID, UUID targetUUID, String targetName, long startTime) {
        HuntInfo huntInfo = new HuntInfo(hunterUUID, targetUUID, targetName, startTime);
        activeHunts.put(hunterUUID, huntInfo);
        setDirty();
    }
    
    /**
     * 结束狩猎
     * @param hunterUUID 狩猎者UUID
     * @param success 是否成功
     */
    public void endHunt(UUID hunterUUID, boolean success) {
        activeHunts.remove(hunterUUID);
        setDirty();
    }
    
    /**
     * 结束狩猎（带服务器等级参数）
     */
    public void endHunt(ServerLevel level, UUID hunterUUID, boolean success) {
        HuntInfo huntInfo = activeHunts.remove(hunterUUID);
        if (huntInfo != null) {
            // 根据成功与否给予奖励或惩罚
            ServerPlayer hunter = level.getServer().getPlayerList().getPlayer(hunterUUID);
            if (hunter != null) {
                if (success) {
                    // 狩猎成功：获得2点信念
                    SanityManager.modifyFaith(hunter, EchoConfig.WUZHONGSHOU_HUNT_SUCCESS_FAITH.get());
                    hunter.sendSystemMessage(net.minecraft.network.chat.Component.literal("§a狩猎成功！获得了" + EchoConfig.WUZHONGSHOU_HUNT_SUCCESS_FAITH.get() + "点信念值"));
                } else {
                    // 狩猎失败：扣除400理智
                    SanityManager.modifySanity(hunter, -EchoConfig.WUZHONGSHOU_HUNT_FAILURE_SANITY_LOSS.get());
                    hunter.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c狩猎失败...扣除了" + EchoConfig.WUZHONGSHOU_HUNT_FAILURE_SANITY_LOSS.get() + "点理智值"));
                }
                
                // 清理狩猎道具
                clearHuntItems(hunter);
            }
            setDirty();
        }
    }
    
    /**
     * 通过目标死亡结束狩猎
     */
    public void endHuntByTargetDeath(UUID targetUUID) {
        for (Map.Entry<UUID, HuntInfo> entry : activeHunts.entrySet()) {
            HuntInfo huntInfo = entry.getValue();
            if (huntInfo.targetUUID.equals(targetUUID)) {
                endHunt(huntInfo.hunterUUID, true); // 狩猎成功
                break;
            }
        }
    }
    
    /**
     * 检查狩猎是否有效
     */
    public boolean isValidHunt(UUID hunterUUID, UUID targetUUID) {
        HuntInfo huntInfo = activeHunts.get(hunterUUID);
        if (huntInfo == null) {
            return false;
        }
        
        // 检查目标是否匹配
        if (!huntInfo.targetUUID.equals(targetUUID)) {
            return false;
        }
        
        // 检查时间是否过期
        long currentTime = System.currentTimeMillis();
        long elapsedTicks = (currentTime - huntInfo.startTime) / 50; // 转换为游戏tick
        
        if (elapsedTicks >= EchoConfig.WUZHONGSHOU_HUNT_DURATION_TICKS.get()) {
            // 时间到了，狩猎失败
            endHunt(hunterUUID, false);
            return false;
        }
        
        return true;
    }
    
    /**
     * 获取狩猎信息
     */
    public HuntInfo getHuntInfo(UUID hunterUUID) {
        return activeHunts.get(hunterUUID);
    }
    
    /**
     * 检查玩家是否被狩猎
     */
    public UUID getHunterOf(UUID targetUUID) {
        for (HuntInfo huntInfo : activeHunts.values()) {
            if (huntInfo.targetUUID.equals(targetUUID)) {
                return huntInfo.hunterUUID;
            }
        }
        return null;
    }
    
    /**
     * 清理狩猎道具
     */
    private void clearHuntItems(ServerPlayer hunter) {
        // 清理背包中的狩猎道具（有消失诅咒的特殊物品）
        for (int i = 0; i < hunter.getInventory().getContainerSize(); i++) {
            net.minecraft.world.item.ItemStack stack = hunter.getInventory().getItem(i);
            
            // 检查是否是狩猎道具（有消失诅咒）
            if (stack.isEnchanted() && stack.getEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.VANISHING_CURSE) > 0) {
                // 检查是否是我们的狩猎道具
                if (isHuntItem(stack)) {
                    hunter.getInventory().setItem(i, net.minecraft.world.item.ItemStack.EMPTY);
                }
            }
        }
        
        // 移除狩猎本能效果
        hunter.removeEffect(org.lanstard.doomsday.common.effects.ModEffects.HUNT_INSTINCT.get());
    }
    
    /**
     * 检查物品是否是狩猎道具
     */
    private boolean isHuntItem(net.minecraft.world.item.ItemStack stack) {
        net.minecraft.world.item.Item item = stack.getItem();
        return item instanceof org.lanstard.doomsday.common.items.hunt.HuntCompassItem ||
               item instanceof org.lanstard.doomsday.common.items.hunt.EndBladeItem ||
               item instanceof org.lanstard.doomsday.common.items.hunt.InvisibilityCloakItem ||
               (item == net.minecraft.world.item.Items.ENDER_PEARL && stack.hasTag() && stack.getTag().getBoolean("hunt_item"));
    }
    
    
    /**
     * 狩猎信息内部类
     */
    public static class HuntInfo {
        public final UUID hunterUUID;
        public final UUID targetUUID;
        public final String targetName;
        public final long startTime; // 开始时间戳（毫秒）
        
        public HuntInfo(UUID hunterUUID, UUID targetUUID, String targetName, long startTime) {
            this.hunterUUID = hunterUUID;
            this.targetUUID = targetUUID;
            this.targetName = targetName;
            this.startTime = startTime;
        }
        
        public long getRemainingTicks() {
            long elapsedMs = System.currentTimeMillis() - startTime;
            long elapsedTicks = elapsedMs / 50;
            return Math.max(0, EchoConfig.WUZHONGSHOU_HUNT_DURATION_TICKS.get() - elapsedTicks);
        }
    }
}