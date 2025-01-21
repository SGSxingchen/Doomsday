package org.lanstard.doomsday.echo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EchoSavedData extends SavedData {
    private static final String DATA_NAME = "doomsday_echo";
    private final Map<UUID, PlayerEchoData> playerEchoMap;
    
    public EchoSavedData() {
        this.playerEchoMap = new HashMap<>();
    }
    
    public EchoSavedData(CompoundTag tag) {
        this();
        ListTag playerList = tag.getList("players", 10);
        for (int i = 0; i < playerList.size(); i++) {
            CompoundTag playerTag = playerList.getCompound(i);
            UUID playerId = playerTag.getUUID("playerId");
            PlayerEchoData echoData = PlayerEchoData.fromNBT(playerTag.getCompound("echoData"));
            playerEchoMap.put(playerId, echoData);
        }
    }
    
    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag playerList = new ListTag();
        for (Map.Entry<UUID, PlayerEchoData> entry : playerEchoMap.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("playerId", entry.getKey());
            playerTag.put("echoData", entry.getValue().toNBT());
            playerList.add(playerTag);
        }
        tag.put("players", playerList);
        return tag;
    }
    
    public PlayerEchoData getPlayerData(UUID playerId) {
        return playerEchoMap.computeIfAbsent(playerId, k -> new PlayerEchoData());
    }
    
    public void setPlayerData(UUID playerId, PlayerEchoData data) {
        playerEchoMap.put(playerId, data);
        this.setDirty();
    }
} 