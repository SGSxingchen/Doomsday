package org.lanstard.doomsday.common.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.echo.EchoManager;
import org.lanstard.doomsday.common.sanity.SanityManager;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class PlayerSyncEvents {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // 同步回响数据
            EchoManager.syncToClient(player);
            // 同步理智值数据
            SanityManager.syncToClient(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EchoManager.syncToClient(player);
            SanityManager.syncToClient(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EchoManager.syncToClient(player);
            SanityManager.syncToClient(player);
        }
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        ServerPlayer player = event.getPlayer();
        if (player != null) {
            EchoManager.syncToClient(player);
            SanityManager.syncToClient(player);
        } else {
            // 同步到所有玩家
            EchoManager.syncToAllClients();
            SanityManager.syncToAllClients();
        }
    }
} 