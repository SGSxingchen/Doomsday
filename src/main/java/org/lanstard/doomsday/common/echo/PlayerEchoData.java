package org.lanstard.doomsday.common.echo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        return activeEchoes;
    }
    
    public void addEcho(Echo echo) {
        Optional<Echo> existing = activeEchoes.stream()
            .filter(e -> e.getId().equals(echo.getId()))
            .findFirst();
        existing.ifPresent(activeEchoes::remove);
        
        activeEchoes.add(echo);
    }
    
    public void removeEcho(Echo echo) {
        activeEchoes.remove(echo);
    }
    
    public boolean updateEcho(Echo echo) {
        for (int i = 0; i < activeEchoes.size(); i++) {
            if (activeEchoes.get(i).getId().equals(echo.getId())) {
                activeEchoes.set(i, echo);
                return true;
            }
        }
        return false;
    }
    
    public Optional<Echo> getEcho(String echoId) {
        return activeEchoes.stream()
            .filter(echo -> echo.getId().equals(echoId))
            .findFirst();
    }
} 