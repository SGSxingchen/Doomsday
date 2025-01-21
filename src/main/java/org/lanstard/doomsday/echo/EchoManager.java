package org.lanstard.doomsday.echo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.echo.preset.BreakAllEcho;
import org.lanstard.doomsday.network.NetworkManager;
import org.lanstard.doomsday.event.EchoTriggerEvents;
import org.lanstard.doomsday.sanity.SanityManager;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class EchoManager {
    private static final String DATA_NAME = "doomsday_echo";
    private static EchoSavedData echoData;
    private static MinecraftServer server;
    
    // 新增：用于跟踪回响禁用状态
    private static Map<UUID, Map<String, Long>> disabledEchoes = new HashMap<>();

    public static void init(MinecraftServer server) {
        EchoManager.server = server;
        echoData = server.overworld().getDataStorage().computeIfAbsent(
            EchoSavedData::new,
            EchoSavedData::new,
            DATA_NAME
        );
    }

    // 获取玩家的回响数据
    public static PlayerEchoData getPlayerEchoData(UUID playerId) {
        if (echoData == null) return new PlayerEchoData();
        return echoData.getPlayerData(playerId);
    }

    // 添加回响
    public static void addEcho(ServerPlayer player, Echo echo) {
        if (echoData == null) return;
        
        PlayerEchoData playerData = getPlayerEchoData(player.getUUID());
        playerData.addEcho(echo);
        echoData.setPlayerData(player.getUUID(), playerData);
        
        // 触发回响获得事件
        EchoTriggerEvents.onEchoGained(player, echo);
        
        // 同步到客户端
        syncToClient(player);
    }

    // 移除回响
    public static void removeEcho(ServerPlayer player, Echo echo) {
        if (echoData == null) return;
        
        PlayerEchoData playerData = getPlayerEchoData(player.getUUID());
        playerData.removeEcho(echo);
        echoData.setPlayerData(player.getUUID(), playerData);
        
        // 触发回响失去事件
        EchoTriggerEvents.onEchoLost(player, echo);
        
        // 同步到客户端
        syncToClient(player);
    }

    // 同步数据到指定客户端
    public static void syncToClient(ServerPlayer player) {
        if (server != null && echoData != null) {
            PlayerEchoData playerData = getPlayerEchoData(player.getUUID());
            EchoUpdatePacket packet = new EchoUpdatePacket(playerData);
            NetworkManager.getChannel().sendTo(
                    packet,
                    player.connection.connection,
                    NetworkDirection.PLAY_TO_CLIENT
            );
        }
    }

    // 同步数据到所有客户端
    public static void syncToAllClients() {
        if (server != null && echoData != null) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                syncToClient(player);
            }
        }
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        init(event.getServer());
    }

    public static boolean hasEcho(ServerPlayer player) {
        if (echoData == null) return false;
        PlayerEchoData playerData = getPlayerEchoData(player.getUUID());
        return !playerData.getActiveEchoes().isEmpty();
    }

    public static boolean hasEchoType(ServerPlayer player, EchoType type) {
        if (echoData == null) return false;
        PlayerEchoData playerData = getPlayerEchoData(player.getUUID());
        return playerData.getActiveEchoes().stream()
            .anyMatch(echo -> echo.getType() == type);
    }

    public static boolean hasSpecificEcho(ServerPlayer player, String echoId) {
        if (echoData == null) return false;
        PlayerEchoData playerData = getPlayerEchoData(player.getUUID());
        return playerData.getActiveEchoes().stream()
            .anyMatch(echo -> echo.getId().equals(echoId));
    }

    public static List<Echo> getPlayerEchoes(ServerPlayer player) {
        if (echoData == null) return Collections.emptyList();
        return getPlayerEchoData(player.getUUID()).getActiveEchoes();
    }

    public static List<Echo> getPlayerEchoesOfType(ServerPlayer player, EchoType type) {
        if (echoData == null) return Collections.emptyList();
        return getPlayerEchoData(player.getUUID()).getActiveEchoes().stream()
            .filter(echo -> echo.getType() == type)
            .collect(Collectors.toList());
    }

    // 在玩家使用回响时扣除理智值
    public static boolean consumeSanity(ServerPlayer player, Echo echo) {
        int currentSanity = SanityManager.getSanity(player);
        int cost = echo.getSanityConsumption();
        
        if (currentSanity >= cost) {
            SanityManager.modifySanity(player, -cost);
            return true;
        }
        return false;
    }

    // 新增：禁用玩家的回响
    public static void disableEchoes(ServerPlayer player, int duration) {
        Map<String, Long> playerDisabled = disabledEchoes.computeIfAbsent(player.getUUID(), k -> new HashMap<>());
        long endTime = System.currentTimeMillis() + duration * 50; // 转换游戏刻到毫秒
        
        // 获取玩家所有回响
        List<Echo> echoes = getPlayerEchoes(player);
        for (Echo echo : echoes) {
            if (!(echo instanceof BreakAllEcho)) { // 破万法不会被禁用
                playerDisabled.put(echo.getId(), endTime);
                echo.onDeactivate(player); // 临时停用回响
            }
        }
        
        // 同步到客户端
        syncToClient(player);
    }
    
    // 新增：检查回响是否被禁用
    public static boolean isEchoDisabled(ServerPlayer player, Echo echo) {
        Map<String, Long> playerDisabled = disabledEchoes.get(player.getUUID());
        if (playerDisabled == null) return false;
        
        Long endTime = playerDisabled.get(echo.getId());
        if (endTime == null) return false;
        
        if (System.currentTimeMillis() > endTime) {
            // 禁用时间已过，移除禁用状态
            playerDisabled.remove(echo.getId());
            return false;
        }
        
        return true;
    }
    
    // 修改：在canUse检查中加入禁用状态检查
    public static boolean canUseEcho(ServerPlayer player, Echo echo) {
        if (isEchoDisabled(player, echo)) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...该回响当前处于禁用状态！"));
            return false;
        }
        
        // 检查理智值是否足够
        int currentSanity = SanityManager.getSanity(player);
        if (currentSanity < echo.getSanityConsumption()) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...理智值不足，无法使用该回响！"));
            return false;
        }
        
        return true;
    }
} 