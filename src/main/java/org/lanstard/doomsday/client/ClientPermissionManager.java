package org.lanstard.doomsday.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.network.NetworkManager;
import org.lanstard.doomsday.network.packet.OpPermissionPacket;

@Mod.EventBusSubscriber(modid = Doomsday.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ClientPermissionManager {
    private static boolean isJoinServer = false;
    private static boolean hasOpPermission = false;
    private static int tickCounter = 0;
    private static final int CHECK_INTERVAL = 1200; // 60秒 = 20tick/s * 60s



    public static void handleOpPermissionResponse(OpPermissionPacket msg) {
        hasOpPermission = msg.isOp();
    }

    public static boolean hasOpPermission() {
        return hasOpPermission;
    }

    @SubscribeEvent
    public static void onRenderPlayerList(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() == VanillaGuiOverlay.PLAYER_LIST.type()) {
            if (!hasOpPermission()) {
                event.setCanceled(true);
            }
        }
    }


    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        NetworkManager.sendToServer(new OpPermissionPacket());
        isJoinServer = true;
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        isJoinServer = false;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (!isJoinServer) return;
        if (event.phase == TickEvent.Phase.END) {
            tickCounter++;
            if (tickCounter >= CHECK_INTERVAL) {
                tickCounter = 0;
                NetworkManager.sendToServer(new OpPermissionPacket());
            }
        }
    }
} 