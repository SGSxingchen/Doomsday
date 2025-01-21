package org.lanstard.doomsday.server.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.network.NetworkManager;
import org.lanstard.doomsday.network.packet.DisplaySettingsPacket;
import org.lanstard.doomsday.server.data.DisplayData;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class SyncPlayerDisplay {
    
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            boolean showHealth;
            if (player.hasPermissions(2)) {
                showHealth = DisplayData.getPlayerSettings(player);
            } else {
                showHealth = DisplayData.getGlobalSettings();
            }
            
            NetworkManager.getChannel().sendTo(
                new DisplaySettingsPacket(showHealth, player.hasPermissions(2)),
                player.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT
            );
        }
    }
}