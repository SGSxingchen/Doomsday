package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.lanstard.doomsday.common.echo.BasicEcho;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.echo.EchoType;
import org.lanstard.doomsday.common.echo.EchoManager;
import org.lanstard.doomsday.common.echo.PlayerEchoData;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.config.EchoConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WuChangEcho extends BasicEcho {
    private long lastUseTime = 0;
    private String temporaryEchoId = null;
    private long temporaryEchoEndTime = 0;
    private int tickCounter = 0;
    
    private static final Random RANDOM = new Random();

    public WuChangEcho() {
        super("wuchang", "无常", EchoType.ACTIVE, EchoConfig.WUCHANG_SANITY_COST.get());
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 检查临时回响是否过期
        if (temporaryEchoId != null && temporaryEchoEndTime > 0) {
            tickCounter++;
            
            // 每秒检查一次
            if (tickCounter >= 20) {
                tickCounter = 0;
                long currentTime = player.level().getGameTime();
                
                if (currentTime >= temporaryEchoEndTime) {
                    // 移除临时回响
                    removeTemporaryEcho(player);
                }
            }
        }
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查冷却时间
        long currentTime = player.level().getGameTime();
        int faith = SanityManager.getFaith(player);
        int threshold = EchoConfig.WUCHANG_HIGH_FAITH_THRESHOLD.get();
        int cooldown = faith >= threshold ? 
            EchoConfig.WUCHANG_HIGH_FAITH_COOLDOWN_TICKS.get() : 
            EchoConfig.WUCHANG_COOLDOWN_TICKS.get();
            
        if (currentTime - lastUseTime < cooldown) {
            int remainingMinutes = (int)((cooldown - (currentTime - lastUseTime)) / 1200);
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f无常之法尚需" + remainingMinutes + "分钟冷却..."));
            return false;
        }

        // 检查理智值是否足够
        int currentSanity = SanityManager.getSanity(player);
        int sanityCost = EchoConfig.WUCHANG_SANITY_COST.get();
        if (currentSanity < sanityCost) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f心神不足，无法施展无常之法...（需要" + sanityCost + "点理智）"));
            return false;
        }

        // 检查是否已经有临时回响
        if (temporaryEchoId != null && temporaryEchoEndTime > currentTime) {
            int remainingMinutes = (int)((temporaryEchoEndTime - currentTime) / 1200);
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f无常之法赋予的回响仍在生效中...（剩余" + remainingMinutes + "分钟）"));
            return false;
        }

        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 更新最后使用时间
        lastUseTime = player.level().getGameTime();
        
        // 消耗理智值
        int sanityCost = EchoConfig.WUCHANG_SANITY_COST.get();
        SanityManager.modifySanity(player, -sanityCost);
        
        // 获取可用的回响列表（排除当前玩家已有的回响和无常自身）
        List<EchoPreset> availableEchoes = getAvailableEchoes(player);
        
        if (availableEchoes.isEmpty()) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f无常之法未能找到合适的回响..."));
            return;
        }
        
        // 随机选择一个回响
        EchoPreset selectedPreset = availableEchoes.get(RANDOM.nextInt(availableEchoes.size()));
        
        // 创建临时回响
        grantTemporaryEcho(player, selectedPreset);
        
        // 更新状态
        updateState(player);
        notifyEchoClocks(player);
    }
    
    private List<EchoPreset> getAvailableEchoes(ServerPlayer player) {
        List<EchoPreset> available = new ArrayList<>();
        
        // 获取玩家当前拥有的所有回响ID
        List<String> playerEchoIds = new ArrayList<>();
        if (EchoManager.hasEcho(player)) {
            PlayerEchoData playerData = EchoManager.getPlayerEchoData(player.getUUID());
            for (Echo echo : playerData.getActiveEchoes()) {
                playerEchoIds.add(echo.getId());
            }
        }
        
        for (EchoPreset preset : EchoPreset.values()) {
            // 排除无常自身
            if (preset == EchoPreset.WUCHANG) {
                continue;
            }
            
            // 检查玩家是否已经拥有这个回响
            String presetId = preset.name().toLowerCase();
            if (!playerEchoIds.contains(presetId)) {
                available.add(preset);
            }
        }
        
        return available;
    }
    
    private void grantTemporaryEcho(ServerPlayer player, EchoPreset preset) {
        // 记录临时回响信息
        temporaryEchoId = preset.name().toLowerCase();
        temporaryEchoEndTime = player.level().getGameTime() + EchoConfig.WUCHANG_DURATION_TICKS.get();
        tickCounter = 0;
        
        // 创建并给予玩家临时回响
        Echo temporaryEcho = preset.createEcho();
        EchoManager.addEcho(player, temporaryEcho);
        
        int durationMinutes = EchoConfig.WUCHANG_DURATION_TICKS.get() / 1200;
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f无常之法赋予了你回响「" + preset.getDisplayName() + "」，将在" + durationMinutes + "分钟后消散..."));
    }
    
    private void removeTemporaryEcho(ServerPlayer player) {
        if (temporaryEchoId != null) {
            // 找到并移除临时回响
            PlayerEchoData playerData = EchoManager.getPlayerEchoData(player.getUUID());
            Echo echoToRemove = null;
            
            for (Echo echo : playerData.getActiveEchoes()) {
                if (echo.getId().equals(temporaryEchoId)) {
                    echoToRemove = echo;
                    break;
                }
            }
            
            if (echoToRemove != null) {
                EchoManager.removeEcho(player, echoToRemove);
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f无常之法赋予的回响已消散..."));
            }
            
            // 重置临时回响状态
            temporaryEchoId = null;
            temporaryEchoEndTime = 0;
            tickCounter = 0;
            
            // 更新状态
            updateState(player);
            notifyEchoClocks(player);
        }
    }
    
    public boolean hasTemporaryEcho() {
        return temporaryEchoId != null && temporaryEchoEndTime > 0;
    }
    
    public String getTemporaryEchoId() {
        return temporaryEchoId;
    }
    
    public int getRemainingMinutes(ServerPlayer player) {
        if (temporaryEchoEndTime <= 0) return 0;
        long currentTime = player.level().getGameTime();
        return (int)Math.max(0, (temporaryEchoEndTime - currentTime) / 1200);
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("lastUseTime", lastUseTime);
        if (temporaryEchoId != null) {
            tag.putString("temporaryEchoId", temporaryEchoId);
        }
        tag.putLong("temporaryEchoEndTime", temporaryEchoEndTime);
        tag.putInt("tickCounter", tickCounter);
        return tag;
    }

    public static WuChangEcho fromNBT(CompoundTag tag) {
        WuChangEcho echo = new WuChangEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.lastUseTime = tag.getLong("lastUseTime");
        if (tag.contains("temporaryEchoId")) {
            echo.temporaryEchoId = tag.getString("temporaryEchoId");
        }
        echo.temporaryEchoEndTime = tag.getLong("temporaryEchoEndTime");
        echo.tickCounter = tag.getInt("tickCounter");
        return echo;
    }
}