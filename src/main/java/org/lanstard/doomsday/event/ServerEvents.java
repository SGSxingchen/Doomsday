package org.lanstard.doomsday.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.echo.EchoManager;
import org.lanstard.doomsday.sanity.SanityManager;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class ServerEvents {
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