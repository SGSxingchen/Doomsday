package org.lanstard.doomsday.server.manage;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.network.NetworkManager;
import org.lanstard.doomsday.network.packet.TimeUpdatePacket;
import org.lanstard.doomsday.utils.TimeSavedData;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class TimeManager {
    private static final String DATA_NAME = "doomsday_time";
    private static TimeSavedData timeData;
    private static MinecraftServer server;

    public static void init(MinecraftServer server) {
        TimeManager.server = server;
        // 从服务器获取或创建 TimeData
        timeData = server.overworld().getDataStorage().computeIfAbsent(
            TimeSavedData::new,
            TimeSavedData::new,
            DATA_NAME
        );
    }

    public static void setDays(int days) {
        if (timeData != null) {
            timeData.setDays(days);
        }
    }

    public static void addDay() {
        if (timeData != null) {
            timeData.setDays(timeData.getDays() + 1);
        }
    }

    public static String getTimeString() {
        if (timeData == null) return "第 0 天";
        return "第 " + timeData.getDays() + " 天";
    }

    // 同步数据到所有客户端
    public static void syncToClients() {
        if (server != null && timeData != null) {
            TimeUpdatePacket packet = new TimeUpdatePacket(timeData.getDays(), 
                server.overworld().getDayTime());
            
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                NetworkManager.getChannel().sendTo(
                        packet,
                        player.connection.connection,
                        NetworkDirection.PLAY_TO_CLIENT
                );
            }
        }
    }

    public static void syncToClient(ServerPlayer player) {
        if (server != null && timeData != null) {
            TimeUpdatePacket packet = new TimeUpdatePacket(timeData.getDays(), 
                server.overworld().getDayTime());
            NetworkManager.getChannel().sendTo(
                    packet,
                    player.connection.connection,
                    NetworkDirection.PLAY_TO_CLIENT
            );
        }
    }

    // 获取当前世界时间
    public static long getWorldTime() {
        return server != null ? server.overworld().getDayTime() : 0;
    }

    // 检查是否已初始化
    public static boolean isInitialized() {
        return timeData != null && server != null;
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        TimeManager.init(event.getServer());
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END &&
                !event.level.isClientSide() &&
                event.level.dimension() == Level.OVERWORLD) {
            Level level = event.level;
            long worldTime = level.getDayTime();
            int currentTicks = (int)(worldTime % 24000);

            if (currentTicks == 0 && worldTime > 0) {
                addDay();
                syncToClients();
            }
            else{
                syncToClients();
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            syncToClients();
        }
    }
}