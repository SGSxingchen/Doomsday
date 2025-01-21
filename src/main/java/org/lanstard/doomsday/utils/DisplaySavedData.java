package org.lanstard.doomsday.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DisplaySavedData extends SavedData {
    private static final String DATA_NAME = "doomsday_display";
    private boolean globalHealthDisplay;
    private final HashMap<UUID, Boolean> playerSettings;
    
    public DisplaySavedData() {
        this.globalHealthDisplay = false;
        this.playerSettings = new HashMap<>();
    }
    
    public DisplaySavedData(CompoundTag tag) {
        this();
        this.globalHealthDisplay = tag.getBoolean("globalDisplay");
        
        ListTag playerList = tag.getList("playerSettings", 10);
        for(int i = 0; i < playerList.size(); i++) {
            CompoundTag playerTag = playerList.getCompound(i);
            UUID uuid = playerTag.getUUID("uuid");
            boolean setting = playerTag.getBoolean("setting");
            this.playerSettings.put(uuid, setting);
        }
    }
    
    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("globalDisplay", globalHealthDisplay);
        
        ListTag playerList = new ListTag();
        for(Map.Entry<UUID, Boolean> entry : playerSettings.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("uuid", entry.getKey());
            playerTag.putBoolean("setting", entry.getValue());
            playerList.add(playerTag);
        }
        tag.put("playerSettings", playerList);
        
        return tag;
    }
    
    public void setGlobalSettings(boolean showHealth) {
        this.globalHealthDisplay = showHealth;
        this.setDirty();
    }
    
    public void setPlayerSettings(UUID playerUUID, boolean showHealth) {
        this.playerSettings.put(playerUUID, showHealth);
        this.setDirty();
    }
    
    public boolean getGlobalSettings() {
        return globalHealthDisplay;
    }
    
    public boolean getPlayerSettings(UUID playerUUID) {
        return playerSettings.getOrDefault(playerUUID, true);
    }
} 