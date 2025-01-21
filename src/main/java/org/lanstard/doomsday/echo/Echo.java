package org.lanstard.doomsday.echo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.lanstard.doomsday.sanity.SanityManager;
import org.lanstard.doomsday.echo.preset.*;

public abstract class Echo {
    private final String id;
    private final String name;
    private final EchoType type;
    private final ActivationType activationType;
    private final int sanityConsumption;
    private final int continuousSanityConsumption; // 持续消耗的理智值
    private boolean isActive;
    private long disabledUntil = 0; // 禁用状态持续到的时间戳
    
    public Echo(String id, String name, EchoType type, ActivationType activationType, int sanityConsumption, int continuousSanityConsumption) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.activationType = activationType;
        this.sanityConsumption = sanityConsumption;
        this.continuousSanityConsumption = continuousSanityConsumption;
    }
    
    public Echo(String id, String name, EchoType type, ActivationType activationType, int sanityConsumption) {
        this(id, name, type, activationType, sanityConsumption, 0);
    }
    
    public abstract void onActivate(ServerPlayer player);
    public abstract void onUpdate(ServerPlayer player);
    public abstract void onDeactivate(ServerPlayer player);
    
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id);
        tag.putString("name", name);
        tag.putString("type", type.name());
        tag.putString("activationType", activationType.name());
        tag.putInt("sanityConsumption", sanityConsumption);
        tag.putInt("continuousSanityConsumption", continuousSanityConsumption);
        tag.putBoolean("isActive", isActive);
        tag.putLong("disabledUntil", disabledUntil);
        return tag;
    }
    
    public static Echo fromNBT(CompoundTag tag) {
        // 根据id获取对应的EchoPreset
        String id = tag.getString("id");
        EchoPreset preset = EchoPreset.getByName(tag.getString("name"));
        if (preset == null) {
            // 如果找不到预设，返回一个基础回响
            return new BasicEcho(
                id,
                tag.getString("name"),
                EchoType.valueOf(tag.getString("type")),
                ActivationType.valueOf(tag.getString("activationType")),
                tag.getInt("sanityConsumption"),
                tag.getInt("continuousSanityConsumption")
            );
        }
        
        // 创建对应的回响实例
        Echo echo = preset.createEcho();
        // 恢复状态
        echo.isActive = tag.getBoolean("isActive");
        echo.disabledUntil = tag.getLong("disabledUntil");
        
        // 如果是特殊回响，调用其特定的fromNBT方法
        if (echo instanceof DuoXinPoEcho) {
            return DuoXinPoEcho.fromNBT(tag);
        } else if (echo instanceof TianXingJianEcho) {
            return TianXingJianEcho.fromNBT(tag);
        } else if (echo instanceof ShengShengBuXiEcho) {
            return ShengShengBuXiEcho.fromNBT(tag);
        } else if (echo instanceof BreakAllEcho) {
            return BreakAllEcho.fromNBT(tag);
        }
        
        return echo;
    }
    
    // Getters
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public EchoType getType() {
        return type;
    }
    
    public ActivationType getActivationType() {
        return activationType;
    }
    
    public int getSanityConsumption() {
        return sanityConsumption;
    }
    
    public int getContinuousSanityConsumption() {
        return continuousSanityConsumption;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    protected void setActive(boolean active) {
        this.isActive = active;
    }
    

    
    public void use(ServerPlayer player) {
        if (!EchoManager.canUseEcho(player, this)) {
            return;
        }
        
        // 扣除理智值
        SanityManager.modifySanity(player, -getSanityConsumption());
        doUse(player);
    }
    
    public boolean canUse(ServerPlayer player) {
        if (isDisabled()) return false;
        return doCanUse(player);
    }
    
    // 新增：客户端检查方法
    public boolean canUse(Player player) {
        return !isDisabled(); // 客户端只检查禁用状态
    }
    
    // 新增：禁用回响
    public void disable(int duration) {
        this.disabledUntil = System.currentTimeMillis() + duration * 50; // 转换游戏刻到毫秒
        if (isActive) {
            isActive = false;
        }
    }
    
    // 新增：检查是否被禁用
    public boolean isDisabled() {
        if (System.currentTimeMillis() > disabledUntil) {
            disabledUntil = 0;
            return false;
        }
        return disabledUntil > 0;
    }
    
    // 新增：解除禁用状态
    public void enable() {
        this.disabledUntil = 0;
    }
    
    protected abstract boolean doCanUse(ServerPlayer player);
    protected abstract void doUse(ServerPlayer player);

    public abstract void toggleContinuous(ServerPlayer player);
} 