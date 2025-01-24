package org.lanstard.doomsday.server.data;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.lanstard.doomsday.utils.DisplaySavedData;

public class DisplayData {
    private static DisplaySavedData savedData;
    
    public static void init(MinecraftServer server) {
        savedData = server.overworld().getDataStorage().computeIfAbsent(
            DisplaySavedData::new,
            DisplaySavedData::new,
            "doomsday_display"
        );
    }
    
    public static void setGlobalSettings(boolean showHealth) {
        if (savedData != null) {
            savedData.setGlobalSettings(showHealth);
        }
    }
    
    public static void setPlayerSettings(ServerPlayer player, boolean showHealth) {
        if (savedData != null) {
            savedData.setPlayerSettings(player.getUUID(), showHealth);
        }
    }
    
    public static boolean getGlobalSettings() {
        return savedData != null && savedData.getGlobalSettings();
    }
    
    public static boolean getPlayerSettings(ServerPlayer player) {
        return savedData != null && savedData.getPlayerSettings(player.getUUID());
    }
} 