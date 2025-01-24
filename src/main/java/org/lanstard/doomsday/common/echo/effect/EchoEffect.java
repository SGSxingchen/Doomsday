package org.lanstard.doomsday.common.echo.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public abstract class EchoEffect {
    private final String id;
    private final String name;
    private final int duration;
    private final int amplifier;
    
    public EchoEffect(String id, String name, int duration, int amplifier) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.amplifier = amplifier;
    }
    
    // 效果激活时调用
    public abstract void onActivate(ServerPlayer player);
    
    // 效果持续时调用
    public abstract void onUpdate(ServerPlayer player);
    
    // 效果结束时调用
    public abstract void onDeactivate(ServerPlayer player);
    
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id);
        tag.putString("name", name);
        tag.putInt("duration", duration);
        tag.putInt("amplifier", amplifier);
        return tag;
    }
    
    public static EchoEffect fromNBT(CompoundTag tag) {
        // 由子类实现具体的反序列化逻辑
        return null;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getDuration() { return duration; }
    public int getAmplifier() { return amplifier; }
} 