package org.lanstard.doomsday.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import org.lanstard.doomsday.echo.*;
import org.lanstard.doomsday.sanity.SanityManager;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

public class BreakAllEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.BREAKALL;
    private static final int RANGE = 10; // 影响范围
    private static final int DISABLE_DURATION = 5 * 60 * 20; // 5分钟的tick数
    private static final int COOLDOWN_DURATION = 30 * 60 * 20; // 30分钟的tick数
    private long lastUsedTime = 0;
    
    public BreakAllEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            PRESET.getSanityConsumption()
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        // 被动效果：无法被剥夺、窃取、复刻
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 不需要更新逻辑
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        // 不需要停用逻辑
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查冷却时间
        long remainingCooldown = (lastUsedTime + COOLDOWN_DURATION * 50) - System.currentTimeMillis();
        if (remainingCooldown > 0) {
            int remainingSeconds = (int) (remainingCooldown / 1000);
            player.sendSystemMessage(Component.literal(String.format(
                "§b[十日终焉] §f破万法还在冷却中，剩余时间：%d分%d秒",
                remainingSeconds / 60,
                remainingSeconds % 60
            )));
            return false;
        }
        
        // 检查理智值是否足够
        int currentSanity = SanityManager.getSanity(player);
        if (currentSanity < getSanityConsumption()) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f理智值不足，无法使用破万法！"));
            return false;
        }
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 记录使用时间
        lastUsedTime = System.currentTimeMillis();

        // 获取范围内的所有玩家
        AABB box = player.getBoundingBox().inflate(RANGE);
        List<ServerPlayer> nearbyPlayers = player.level().getEntitiesOfClass(ServerPlayer.class, box);

        // 播放粒子效果
        spawnRingParticles(player);

        // 对范围内的所有玩家应用效果
        for (ServerPlayer target : nearbyPlayers) {
            if (target == player) continue; // 不影响自己
            
            // 获取目标玩家的所有回响
            List<Echo> targetEchoes = EchoManager.getPlayerEchoes(target);
            boolean hasDisabledAny = false;
            
            // 使所有回响失效
            for (Echo echo : targetEchoes) {
                if (echo instanceof BreakAllEcho) continue; // 破万法不会被破万法影响
                echo.disable(DISABLE_DURATION);
                echo.onDeactivate(target);
                hasDisabledAny = true;
            }
            
            // 如果有回响被禁用，发送提示
            if (hasDisabledAny) {
                target.sendSystemMessage(Component.literal("§b[十日终焉] §f你的回响被破万法禁用了！"));
            }
        }
        
        // 发送使用成功消息
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f你使用了破万法，禁用了周围玩家的回响！"));
    }
    
    private void spawnRingParticles(ServerPlayer player) {
        Vec3 center = player.position();
        int particleCount = 72; // 每圈的粒子数
        int rings = 20; // 扩散的圈数
        
        for (int r = 0; r < rings; r++) {
            float radius = (r + 1) * (RANGE / (float)rings);
            float yOffset = r * 0.1f; // 每圈稍微升高一点
            
            for (int i = 0; i < particleCount; i++) {
                double angle = 2 * Math.PI * i / particleCount;
                double x = center.x + radius * Math.cos(angle);
                double y = center.y + yOffset;
                double z = center.z + radius * Math.sin(angle);
                
                // 直接在服务端生成粒子
                player.serverLevel().sendParticles(
                    ParticleTypes.END_ROD, // 使用末地烛的白色粒子
                    x, y, z, // position
                    1, // count
                    0, 0, 0, // offset
                    0 // speed
                );
            }
        }
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {}

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("last_used_time", lastUsedTime);
        return tag;
    }
    
    public static BreakAllEcho fromNBT(CompoundTag tag) {
        BreakAllEcho echo = new BreakAllEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.lastUsedTime = tag.getLong("last_used_time");
        return echo;
    }
}