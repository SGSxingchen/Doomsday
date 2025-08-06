package org.lanstard.doomsday.common.sanity;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.network.NetworkManager;
import org.lanstard.doomsday.common.sanity.config.SanityConfig;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class SanityManager {
    private static final String DATA_NAME = "doomsday_sanity";
    private static SanitySavedData sanityData;
    private static MinecraftServer server;
    
    public static void init(MinecraftServer server) {
        SanityManager.server = server;
        sanityData = server.overworld().getDataStorage().computeIfAbsent(
            SanitySavedData::new,
            SanitySavedData::new,
            DATA_NAME
        );
    }
    
    public static void modifySanity(ServerPlayer player, int delta) {
        if (sanityData == null) return;
        
        sanityData.modifySanity(player.getUUID(), delta);
        syncToClient(player);
    }
    
    public static int getSanity(ServerPlayer player) {
        if (sanityData == null) return 100;
        return sanityData.getSanity(player.getUUID());
    }
    
    public static void setSanity(ServerPlayer player, int value) {
        if (sanityData == null) return;
        sanityData.setSanity(player.getUUID(), value);
        syncToClient(player);
    }
    
    public static int getFaith(ServerPlayer player) {
        if (sanityData == null) return 0;
        return sanityData.getFaith(player.getUUID());
    }
    
    public static void setFaith(ServerPlayer player, int value) {
        if (sanityData == null) return;
        sanityData.setFaith(player.getUUID(), value);
        
        // 强制刷新属性以立即更新血条显示
        refreshPlayerAttributes(player);
        
        syncToClient(player);
    }
    
    public static void modifyFaith(ServerPlayer player, int delta) {
        if (sanityData == null) return;
        sanityData.modifyFaith(player.getUUID(), delta);
        
        // 如果是增加信念，同时增加100当前理智值
        if (delta > 0) {
            modifySanity(player, 100);
        }
        
        // 强制刷新属性以立即更新血条显示
        refreshPlayerAttributes(player);
        
        syncToClient(player);
    }
    
    public static int getBeliefLevel(ServerPlayer player) {
        if (sanityData == null) return 0;
        return sanityData.getFaith(player.getUUID());
    }
    
    public static int getMaxSanity(ServerPlayer player) {
        if (sanityData == null) return 100;
        return sanityData.getMaxSanity(player.getUUID());
    }
    
    public static boolean hasSufficientFaith(ServerPlayer player) {
        if (sanityData == null) return false;
        return sanityData.hasSufficientFaith(player.getUUID());
    }

    public static boolean hasSufficientSanity(ServerPlayer player, int requiredSanity) {
        if (sanityData == null) return false;
        return getSanity(player) >= requiredSanity;
    }
    
    public static void syncToClient(ServerPlayer player) {
        if (server != null && sanityData != null) {
            int currentSanity = sanityData.getSanity(player.getUUID());
            int currentFaith = sanityData.getFaith(player.getUUID());
            SanityUpdatePacket packet = new SanityUpdatePacket(currentSanity, currentFaith);
            NetworkManager.getChannel().sendTo(
                packet,
                player.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT
            );
        }
    }
    
    public static void syncToAllClients() {
        if (server != null) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                syncToClient(player);
            }
        }
    }
    
    public static void modifyMaxSanity(ServerPlayer player, int delta) {
        if (sanityData == null) return;
        sanityData.modifyMaxSanity(player.getUUID(), delta);
        
        int currentSanity = getSanity(player);
        int maxSanity = getMaxSanity(player);
        if (currentSanity > maxSanity) {
            setSanity(player, maxSanity);
        }

        syncToClient(player);
    }
    
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        init(event.getServer());
    }
    
    /**
     * 刷新玩家属性，强制客户端更新血条显示
     * 这个方法会立即应用信念值对血量上限的影响
     */
    private static void refreshPlayerAttributes(ServerPlayer player) {
        if (sanityData == null) return;
        
        int sanity = getSanity(player);
        int faith = getFaith(player);
        
        // 使用与SanityEffectManager相同的逻辑计算血量修改器
        var config = SanityConfig.getConfig();
        float totalHealthModifier = 0;

        // 应用所有匹配区间的效果
        for (var threshold : config.thresholds) {
            if (sanity >= threshold.range.min && sanity <= threshold.range.max) {
                totalHealthModifier += threshold.health_modifier;
            }
        }

        // 添加信念值带来的生命值加成（每1点信念提供2点生命值）
        totalHealthModifier += faith * 2;

        // 使用属性修改器来应用生命值变化
        var attribute = player.getAttribute(Attributes.MAX_HEALTH);
        var modifierId = java.util.UUID.fromString("b9c99a89-f5c9-4624-9d38-4a1f5d5a2e3a"); // 与SanityEffectManager使用相同的UUID
        
        // 移除旧的修改器（如果存在）
        if (attribute != null) {
            attribute.removePermanentModifier(modifierId);
        }

        // 只有在有修改时才添加修改器
        if (totalHealthModifier != 0) {
            if (attribute != null) {
                attribute.addPermanentModifier(new AttributeModifier(
                    modifierId,
                    "Sanity Health Modifier",
                    totalHealthModifier,
                    AttributeModifier.Operation.ADDITION
                ));
            }
        }
        
        // 如果当前血量超过新的最大血量，调整当前血量
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }
}