package org.lanstard.doomsday.common.echo;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.blocks.entity.EchoClockBlockEntity;
import org.lanstard.doomsday.common.echo.preset.BreakAllEcho;
import org.lanstard.doomsday.common.events.EchoLifecycleEvents;
import org.lanstard.doomsday.network.NetworkManager;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class EchoManager {
    private static final String DATA_NAME = "doomsday_echo";
    private static EchoSavedData echoData;
    private static MinecraftServer server;
    // 存储每个维度中的回响钟位置
    private static final Map<ResourceKey<Level>, Set<BlockPos>> echoClocksPos = new java.util.HashMap<>();

    public static void init(MinecraftServer server) {
        EchoManager.server = server;
        echoData = server.overworld().getDataStorage().computeIfAbsent(
            EchoSavedData::new,
            EchoSavedData::new,
            DATA_NAME
        );
        // 从保存的数据中恢复回响钟位置
        echoClocksPos.clear();
        echoClocksPos.putAll(echoData.getEchoClockPositions());
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
        EchoLifecycleEvents.onEchoGained(player, echo);
        
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
        EchoLifecycleEvents.onEchoLost(player, echo);
        
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

    // 修改：移除理智检查，只保留禁用检查
    public static boolean canUseEcho(ServerPlayer player, Echo echo) {
        if (echo.isDisabled()) {
            player.sendSystemMessage(Component.translatable("message.doomsday.prefix")
                .append(Component.translatable("message.doomsday.echo_disabled")));
            return false;
        }
        
        updateEcho(player, echo);
        return true;
    }

    // 修改：禁用玩家的回响
    public static void disableEchoes(ServerPlayer player, int duration) {
        // 获取玩家所有回响
        List<Echo> echoes = getPlayerEchoes(player);
        for (Echo echo : echoes) {
            if (!(echo instanceof BreakAllEcho)) { // 破万法不会被禁用
                echo.disable(duration);
                echo.onDeactivate(player); // 临时停用回响
                updateEcho(player, echo); // 更新状态
            }
        }
        
        // 发送整体禁用提示
        player.sendSystemMessage(Component.translatable("message.doomsday.prefix")
            .append(Component.translatable("message.doomsday.echo_disabled_all")));
        
        // 同步到客户端
        syncToClient(player);
    }

    // 新增：启用回响
    public static void enableEcho(ServerPlayer player, Echo echo) {
        echo.enable();
        updateEcho(player, echo); // 更新状态
        player.sendSystemMessage(Component.translatable("message.doomsday.prefix")
            .append(Component.translatable("message.doomsday.echo_enable", echo.getName())));
        syncToClient(player);
    }

    /**
     * 更新回声状态
     * 当回声的内部状态发生变化时调用此方法
     */
    public static void updateEcho(ServerPlayer player, Echo echo) {
        if (echoData == null) return;
        
        PlayerEchoData playerData = getPlayerEchoData(player.getUUID());
        if (playerData.updateEcho(echo)) {
            echoData.setPlayerData(player.getUUID(), playerData);
            // 同步到客户端
            syncToClient(player);
        }
    }

    public static void removeEchoes(ServerPlayer player, List<Echo> echoes) {
        if (echoData == null) return;
        
        PlayerEchoData playerData = getPlayerEchoData(player.getUUID());
        for (Echo echo : new ArrayList<>(echoes)) {
            playerData.removeEcho(echo);
            // 触发回响失去事件
            EchoLifecycleEvents.onEchoLost(player, echo);
        }
        echoData.setPlayerData(player.getUUID(), playerData);
        
        // 同步到客户端
        syncToClient(player);
    }

    // 修改添加回响钟位置的方法
    public static void addEchoClock(Level level, BlockPos pos) {
        echoClocksPos.computeIfAbsent(level.dimension(), k -> new java.util.HashSet<>()).add(pos.immutable());
        // 保存数据
        if (echoData != null) {
            echoData.setEchoClockPositions(echoClocksPos);
            echoData.setDirty();
        }
    }

    // 修改移除回响钟位置的方法
    public static void removeEchoClock(Level level, BlockPos pos) {
        Set<BlockPos> positions = echoClocksPos.get(level.dimension());
        if (positions != null) {
            positions.remove(pos);
            // 保存数据
            if (echoData != null) {
                echoData.setEchoClockPositions(echoClocksPos);
                echoData.setDirty();
            }
        }
    }

    // 通知范围内的所有回响钟
    public static void notifyEchoClocks(ServerPlayer player, String echoName) {
        Set<BlockPos> positions = echoClocksPos.get(player.level().dimension());
        if (positions == null) return;
        positions.forEach(pos -> {
            BlockEntity blockEntity = player.level().getBlockEntity(pos);
            if (blockEntity instanceof EchoClockBlockEntity clockEntity) {
                clockEntity.onEchoUsed(echoName);
            }
        });
    }

} 