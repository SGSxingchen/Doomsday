package org.lanstard.doomsday.echo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import java.util.ArrayList;
import java.util.List;

public class PlayerEchoData {
    private final List<Echo> activeEchoes;
    
    public PlayerEchoData() {
        this.activeEchoes = new ArrayList<>();
    }
    
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag echoList = new ListTag();
        for (Echo echo : activeEchoes) {
            echoList.add(echo.toNBT());
        }
        tag.put("echoes", echoList);
        return tag;
    }
    
    public static PlayerEchoData fromNBT(CompoundTag tag) {
        PlayerEchoData data = new PlayerEchoData();
        ListTag echoList = tag.getList("echoes", 10);
        for (int i = 0; i < echoList.size(); i++) {
            CompoundTag echoTag = echoList.getCompound(i);
            data.activeEchoes.add(Echo.fromNBT(echoTag));
        }
        return data;
    }
    
    public List<Echo> getActiveEchoes() {
        return new ArrayList<>(activeEchoes);
    }
    
    public void addEcho(Echo echo) {
        activeEchoes.add(echo);
    }
    
    public void removeEcho(Echo echo) {
        activeEchoes.remove(echo);
    }
} 