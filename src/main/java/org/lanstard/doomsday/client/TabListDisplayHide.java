package org.lanstard.doomsday.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.network.NetworkManager;
import org.lanstard.doomsday.network.packet.OpPermissionPacket;

@Mod.EventBusSubscriber(modid = Doomsday.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class TabListDisplayHide {
    private static boolean hasOpPermissionFromServer = false;
    private static long lastCheckTime = 0;
    private static final long CHECK_INTERVAL = 100;

    public static void handleOpPermissionResponse(OpPermissionPacket msg) {
        hasOpPermissionFromServer = msg.isOp();
        // SnowRule.LOGGER.info("收到服务器OP权限响应: {}", msg.isOp());
    }
    
    @SubscribeEvent
    public static void onRenderPlayerList(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() == VanillaGuiOverlay.PLAYER_LIST.type()) {
            boolean hasPermission = hasOpPermission();
            if (!hasPermission) {
                event.setCanceled(true);
            }
        }
    }
    
    private static boolean hasOpPermission() {
        return hasOpPermissionFromServer;
    }

    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        NetworkManager.sendToServer(new OpPermissionPacket());
    }
} 