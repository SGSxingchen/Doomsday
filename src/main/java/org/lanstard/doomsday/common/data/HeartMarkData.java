package org.lanstard.doomsday.common.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HeartMarkData extends SavedData {
    private static final String DATA_NAME = "doomsday_heart_marks";
    private final Map<UUID, UUID> markedPlayers = new HashMap<>(); // target -> caster
    
    public HeartMarkData() {
        super();
    }
    
    public static HeartMarkData create() {
        return new HeartMarkData();
    }
    
    public static HeartMarkData load(CompoundTag tag) {
        HeartMarkData data = create();
        ListTag list = tag.getList("marks", 10); // 10 = CompoundTag
        for (int i = 0; i < list.size(); i++) {
            CompoundTag markTag = list.getCompound(i);
            UUID target = UUID.fromString(markTag.getString("target"));
            UUID caster = UUID.fromString(markTag.getString("caster"));
            data.markedPlayers.put(target, caster);
        }
        return data;
    }
    
    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        markedPlayers.forEach((target, caster) -> {
            CompoundTag markTag = new CompoundTag();
            markTag.putString("target", target.toString());
            markTag.putString("caster", caster.toString());
            list.add(markTag);
        });
        tag.put("marks", list);
        return tag;
    }
    
    public void markPlayer(UUID target, UUID caster) {
        markedPlayers.put(target, caster);
        setDirty();
    }
    
    public void unmarkPlayer(UUID target) {
        markedPlayers.remove(target);
        setDirty();
    }
    
    public UUID getCaster(UUID target) {
        return markedPlayers.get(target);
    }
    
    public boolean isMarked(UUID target) {
        return markedPlayers.containsKey(target);
    }
    
    public static HeartMarkData get(ServerLevel level) {
        DimensionDataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(HeartMarkData::load, HeartMarkData::create, DATA_NAME);
    }
}