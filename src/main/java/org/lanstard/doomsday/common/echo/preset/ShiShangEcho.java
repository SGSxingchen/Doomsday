package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.lanstard.doomsday.common.echo.BasicEcho;
import org.lanstard.doomsday.common.echo.EchoType;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.config.EchoConfig;

public class ShiShangEcho extends BasicEcho {
    private long lastUseTime = 0;

    public ShiShangEcho() {
        super("shishang", "失熵", EchoType.ACTIVE, EchoConfig.SHISHANG_SANITY_COST.get());
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查冷却时间
        long currentTime = player.level().getGameTime();
        int cooldown = EchoConfig.SHISHANG_COOLDOWN_TICKS.get();
        if (currentTime - lastUseTime < cooldown) {
            int remainingSeconds = (int)((cooldown - (currentTime - lastUseTime)) / 20);
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f失熵之法尚需" + remainingSeconds + "秒冷却..."));
            return false;
        }

        // 检查理智值是否足够
        int currentSanity = SanityManager.getSanity(player);
        int sanityCost = EchoConfig.SHISHANG_SANITY_COST.get();
        if (currentSanity < sanityCost) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f心神不足，无法施展失熵之法...（需要" + sanityCost + "点理智）"));
            return false;
        }

        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 更新最后使用时间
        lastUseTime = player.level().getGameTime();
        
        // 消耗理智值
        int sanityCost = EchoConfig.SHISHANG_SANITY_COST.get();
        SanityManager.modifySanity(player, -sanityCost);
        
        // 发送死亡警告消息
        player.sendSystemMessage(Component.literal("§4[十日终焉] §c失熵之法启动...存在即是痛苦，消散即是解脱..."));
        
        // 造成致命伤害
        double deathDamage = EchoConfig.SHISHANG_DEATH_DAMAGE.get();
        player.hurt(player.damageSources().magic(), (float)deathDamage);
        
        // 更新状态
        updateState(player);
        notifyEchoClocks(player);
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("lastUseTime", lastUseTime);
        return tag;
    }

    public static ShiShangEcho fromNBT(CompoundTag tag) {
        ShiShangEcho echo = new ShiShangEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.lastUseTime = tag.getLong("lastUseTime");
        return echo;
    }
}