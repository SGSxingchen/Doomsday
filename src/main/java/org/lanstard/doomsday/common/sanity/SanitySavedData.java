package org.lanstard.doomsday.common.sanity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.lanstard.doomsday.common.sanity.config.SanityConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SanitySavedData extends SavedData {
    private static final String DATA_NAME = "doomsday_sanity";
    private final Map<UUID, Integer> playerSanity;
    private final Map<UUID, Integer> playerFaith;
    private final Map<UUID, Integer> playerMaxSanityModifier;
    private final Map<UUID, Integer> playerKidneyCount;
    private static final int BASE_MAX_SANITY = SanityConfig.getConfig().sanity_limits.max;
    private static final int MIN_SANITY = SanityConfig.getConfig().sanity_limits.min;
    private static final int FAITH_SANITY_BONUS = 100;
    
    public SanitySavedData() {
        this.playerSanity = new HashMap<>();
        this.playerFaith = new HashMap<>();
        this.playerMaxSanityModifier = new HashMap<>();
        this.playerKidneyCount = new HashMap<>();
    }
    
    public SanitySavedData(CompoundTag tag) {
        this();
        ListTag playerList = tag.getList("players", 10);
        for (int i = 0; i < playerList.size(); i++) {
            CompoundTag playerTag = playerList.getCompound(i);
            UUID playerId = playerTag.getUUID("playerId");
            int sanity = playerTag.getInt("sanity");
            int faith = playerTag.getInt("faith");
            int maxSanityMod = playerTag.getInt("maxSanityMod");
            int kidneyCount = playerTag.getInt("kidneyCount");
            playerSanity.put(playerId, sanity);
            playerFaith.put(playerId, faith);
            playerMaxSanityModifier.put(playerId, maxSanityMod);
            playerKidneyCount.put(playerId, kidneyCount);
        }
    }
    
    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag playerList = new ListTag();
        for (Map.Entry<UUID, Integer> entry : playerSanity.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            UUID playerId = entry.getKey();
            playerTag.putUUID("playerId", playerId);
            playerTag.putInt("sanity", entry.getValue());
            playerTag.putInt("faith", playerFaith.getOrDefault(playerId, 0));
            playerTag.putInt("maxSanityMod", playerMaxSanityModifier.getOrDefault(playerId, 0));
            playerTag.putInt("kidneyCount", playerKidneyCount.getOrDefault(playerId, 0));
            playerList.add(playerTag);
        }
        tag.put("players", playerList);
        return tag;
    }
    
    public int getFaith(UUID playerId) {
        return playerFaith.getOrDefault(playerId, 0);
    }
    
    public void setFaith(UUID playerId, int value) {
        playerFaith.put(playerId, Math.max(0, value));
        this.setDirty();
    }
    
    public void modifyFaith(UUID playerId, int delta) {
        int currentFaith = getFaith(playerId);
        setFaith(playerId, currentFaith + delta);
    }
    
    public int getMaxSanity(UUID playerId) {
        int faith = getFaith(playerId);
        int modifier = playerMaxSanityModifier.getOrDefault(playerId, 0);
        return Math.max(100, BASE_MAX_SANITY + (faith * FAITH_SANITY_BONUS) + modifier);
    }
    
    public int getSanity(UUID playerId) {
        return playerSanity.getOrDefault(playerId, getMaxSanity(playerId));
    }
    
    public void setSanity(UUID playerId, int value) {
        int maxSanity = getMaxSanity(playerId);
        playerSanity.put(playerId, Math.max(MIN_SANITY, Math.min(maxSanity, value)));
        this.setDirty();
    }
    
    public void modifySanity(UUID playerId, int delta) {
        int currentSanity = getSanity(playerId);
        setSanity(playerId, currentSanity + delta);
    }
    
    public boolean hasSufficientFaith(UUID playerId) {
        return getFaith(playerId) >= 10;
    }
    
    public void modifyMaxSanity(UUID playerId, int delta) {
        int currentMod = playerMaxSanityModifier.getOrDefault(playerId, 0);
        playerMaxSanityModifier.put(playerId, currentMod + delta);
        this.setDirty();
    }
    
    public int getKidneyCount(UUID playerId) {
        return playerKidneyCount.getOrDefault(playerId, 0);
    }
    
    public void addKidneyCount(UUID playerId) {
        int currentCount = getKidneyCount(playerId);
        playerKidneyCount.put(playerId, currentCount + 1);
        this.setDirty();
    }
    
    public boolean canHarvestKidney(UUID playerId, int maxKidneyCount) {
        return getKidneyCount(playerId) < maxKidneyCount;
    }
} 