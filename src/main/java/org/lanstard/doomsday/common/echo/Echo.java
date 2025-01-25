package org.lanstard.doomsday.common.echo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
public abstract class Echo {
    private final String id;
    private final String name;
    private final EchoType type;
    private final ActivationType activationType;
    private final int sanityConsumption; // 主动动作理智消耗（仅作为参考值）
    private final int continuousSanityConsumption; // 持续消耗的理智值（仅作为参考值）
    private boolean isActive; //用于辨别回响是否被激活
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
    
    /**
     * 当回响被激活时调用
     * 子类需要在此方法中处理激活时的理智消耗
     */
    public abstract void onActivate(ServerPlayer player);
    
    /**
     * 每tick都会调用的更新方法
     * 子类需要在此方法中处理持续性理智消耗
     */
    public abstract void onUpdate(ServerPlayer player);
    
    /**
     * 当回响被停用时调用
     * 用于清理效果、重置状态等
     */
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
        
        // 尝试使用反射调用子类的fromNBT方法
        try {
            Class<?> echoClass = echo.getClass();
            java.lang.reflect.Method fromNBTMethod = echoClass.getMethod("fromNBT", CompoundTag.class);
            if (fromNBTMethod.getDeclaringClass() != Echo.class) {
                // 如果子类有自己的fromNBT实现，使用它
                return (Echo) fromNBTMethod.invoke(null, tag);
            }
        } catch (Exception ignored) {
            // 如果没有找到fromNBT方法或调用失败，使用默认的echo实例
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
    
    /**
     * 设置回声的激活状态
     * 注意：此方法需要在ServerPlayer上下文中调用才能正确保存状态
     */
    protected void setActive(boolean active) {
        if (this.isActive != active) {
            this.isActive = active;
            // 注意：这里不直接调用updateState，因为我们可能在非ServerPlayer上下文中调用此方法
            // 状态更新应该由调用者在合适的时机触发
        }
    }
    
    /**
     * 设置回声的激活状态并立即更新
     * 此方法必须在服务器端调用
     */
    protected void setActiveAndUpdate(ServerPlayer player, boolean active) {
        setActive(active);
        updateState(player);
    }
    
    /**
     * 更新回声状态
     * 子类在修改状态后应调用此方法以确保数据被保存
     */
    protected void updateState(ServerPlayer player) {
        EchoManager.updateEcho(player, this);
    }
    
    /**
     * 主动使用回响时调用
     * 子类需要在 doUse 中自行处理理智消耗和提示
     */
    public void use(ServerPlayer player) {
        if (!this.canUse(player)) {
            return;
        }
        
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

    // 新增：启用回响
    public void enable() {
        this.disabledUntil = 0;
        if (!isActive) {
            isActive = true;
        }
    }
    
    // 新增：检查是否被禁用
    public boolean isDisabled() {
        if (System.currentTimeMillis() > disabledUntil) {
            disabledUntil = 0;
            enable();
            return false;
        }
        return disabledUntil > 0;
    }
    
    
    /**
     * 检查是否可以使用回响
     * 子类需要实现具体的检查逻辑，包括理智值检查
     */
    protected abstract boolean doCanUse(ServerPlayer player);
    
    /**
     * 实现回响的具体使用效果
     * 子类需要在此方法中处理理智消耗
     */
    protected abstract void doUse(ServerPlayer player);

    /**
     * 切换持续性效果的开关
     * 子类需要自行处理开启/关闭时的理智消耗
     */
    public abstract void toggleContinuous(ServerPlayer player);
} 