package org.lanstard.doomsday.echo.effect;

import net.minecraft.server.level.ServerPlayer;
import org.lanstard.doomsday.sanity.SanityManager;

public class SanityDrainEffect extends EchoEffect {
    private final int drainRate;
    
    public SanityDrainEffect(String id, String name, int duration, int amplifier, int drainRate) {
        super(id, name, duration, amplifier);
        this.drainRate = drainRate;
    }
    
    @Override
    public void onActivate(ServerPlayer player) {
        // 效果开始时的初始化
    }
    
    @Override
    public void onUpdate(ServerPlayer player) {
        // 每tick消耗理智值
        if (player.tickCount % 20 == 0) { // 每秒执行一次
            SanityManager.modifySanity(player, -drainRate * getAmplifier());
        }
    }
    
    @Override
    public void onDeactivate(ServerPlayer player) {
        // 效果结束时的清理
    }
}