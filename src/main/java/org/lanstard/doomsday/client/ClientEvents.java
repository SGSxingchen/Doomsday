package org.lanstard.doomsday.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.echo.ClientEchoManager;
import org.lanstard.doomsday.echo.Echo;
import org.lanstard.doomsday.echo.EchoType;
import org.lanstard.doomsday.network.NetworkManager;
import org.lanstard.doomsday.network.packet.OpenEchoScreenPacket;
import org.lanstard.doomsday.network.packet.UseEchoPacket;
import org.lanstard.doomsday.network.packet.ToggleContinuousEchoPacket;

@Mod.EventBusSubscriber(modid = Doomsday.MODID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;
        
        // 检查打开回响界面的按键
        if (KeyBindings.OPEN_ECHO_SCREEN.consumeClick()) {
            NetworkManager.sendToServer(new OpenEchoScreenPacket());
        }
        
        // 检查切换回响的按键
        if (KeyBindings.NEXT_ECHO.consumeClick()) {
            ClientEchoSelector.nextEcho(ClientEchoManager.getEchoes());
            Echo currentEcho = ClientEchoSelector.getCurrentEcho(ClientEchoManager.getEchoes());
            if (currentEcho != null) {
                minecraft.player.displayClientMessage(
                    Component.translatable("message.doomsday.echo_selected", currentEcho.getName()),
                    true
                );
            }
        }
        
        if (KeyBindings.PREVIOUS_ECHO.consumeClick()) {
            ClientEchoSelector.previousEcho(ClientEchoManager.getEchoes());
            Echo currentEcho = ClientEchoSelector.getCurrentEcho(ClientEchoManager.getEchoes());
            if (currentEcho != null) {
                minecraft.player.displayClientMessage(
                    Component.translatable("message.doomsday.echo_selected", currentEcho.getName()),
                    true
                );
            }
        }
        
        // 检查使用回响的按键
        if (KeyBindings.USE_ECHO.consumeClick()) {
            Echo currentEcho = ClientEchoSelector.getCurrentEcho(ClientEchoManager.getEchoes());
            if (currentEcho != null) {
                if (currentEcho.canUse(minecraft.player)) {
                    NetworkManager.sendToServer(new UseEchoPacket(currentEcho.getId()));
                } else {
                    minecraft.player.displayClientMessage(
                        Component.translatable("message.doomsday.echo_cannot_use", currentEcho.getName()),
                        true
                    );
                }
            } else {
                minecraft.player.displayClientMessage(
                    Component.translatable("message.doomsday.no_echo_selected"),
                    true
                );
            }
        }
        
        // 检查切换持续性回响的按键
        if (KeyBindings.TOGGLE_CONTINUOUS_ECHO.consumeClick()) {
            Echo currentEcho = ClientEchoSelector.getCurrentEcho(ClientEchoManager.getEchoes());
            if (currentEcho != null && currentEcho.getType() == EchoType.CONTINUOUS) {
                if (currentEcho.canUse(minecraft.player)) {
                    NetworkManager.sendToServer(new ToggleContinuousEchoPacket(currentEcho.getId()));
                } else {
                    minecraft.player.displayClientMessage(
                        Component.translatable("message.doomsday.echo_cannot_use", currentEcho.getName()),
                        true
                    );
                }
            } else if (currentEcho != null) {
                minecraft.player.displayClientMessage(
                    Component.translatable("message.doomsday.echo_not_continuous", currentEcho.getName()),
                    true
                );
            } else {
                minecraft.player.displayClientMessage(
                    Component.translatable("message.doomsday.no_echo_selected"),
                    true
                );
            }
        }
    }
} 