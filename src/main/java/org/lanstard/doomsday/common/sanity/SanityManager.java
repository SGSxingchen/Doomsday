package org.lanstard.doomsday.common.sanity;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.network.NetworkManager;

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
        syncToClient(player);
    }
    
    public static void modifyFaith(ServerPlayer player, int delta) {
        if (sanityData == null) return;
        sanityData.modifyFaith(player.getUUID(), delta);
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
}