package org.lanstard.doomsday.common.echo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;

public class EchoSavedData extends SavedData {
    private static final String DATA_NAME = "doomsday_echo";
    private final Map<UUID, PlayerEchoData> playerEchoMap;
    private Map<ResourceKey<Level>, Set<BlockPos>> echoClockPositions = new HashMap<>();
    
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
        
        // 加载回响钟位置
        echoClockPositions.clear();
        if (tag.contains("echo_clocks")) {
            CompoundTag clocksTag = tag.getCompound("echo_clocks");
            clocksTag.getAllKeys().forEach(dimKey -> {
                ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimKey));
                Set<BlockPos> positions = new HashSet<>();
                ListTag posList = clocksTag.getList(dimKey, Tag.TAG_COMPOUND);
                posList.forEach(posTag -> {
                    CompoundTag pt = (CompoundTag) posTag;
                    positions.add(new BlockPos(
                        pt.getInt("X"),
                        pt.getInt("Y"),
                        pt.getInt("Z")
                    ));
                });
                echoClockPositions.put(dimension, positions);
            });
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
        
        // 保存回响钟位置
        CompoundTag clocksTag = new CompoundTag();
        echoClockPositions.forEach((dimension, positions) -> {
            ListTag posList = new ListTag();
            positions.forEach(pos -> {
                CompoundTag posTag = new CompoundTag();
                posTag.putInt("X", pos.getX());
                posTag.putInt("Y", pos.getY());
                posTag.putInt("Z", pos.getZ());
                posList.add(posTag);
            });
            clocksTag.put(dimension.location().toString(), posList);
        });
        tag.put("echo_clocks", clocksTag);
        
        return tag;
    }
    
    public PlayerEchoData getPlayerData(UUID playerId) {
        return playerEchoMap.computeIfAbsent(playerId, k -> new PlayerEchoData());
    }
    
    public void setPlayerData(UUID playerId, PlayerEchoData data) {
        playerEchoMap.put(playerId, data);
        this.setDirty();
    }
    
    public Map<ResourceKey<Level>, Set<BlockPos>> getEchoClockPositions() {
        return echoClockPositions;
    }
    
    public void setEchoClockPositions(Map<ResourceKey<Level>, Set<BlockPos>> positions) {
        this.echoClockPositions = positions;
        setDirty();
    }
} 