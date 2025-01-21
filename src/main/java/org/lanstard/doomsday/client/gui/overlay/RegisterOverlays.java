package org.lanstard.doomsday.client.gui.overlay;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.client.gui.text.ScreenTextManager;
import net.minecraftforge.client.gui.overlay.ForgeGui;

@Mod.EventBusSubscriber(modid = Doomsday.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class RegisterOverlays {
    
    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("time_hud", TimeOverlay.HUD_TIME);
        event.registerAboveAll("compass_hud", CompassOverlay.HUD_COMPASS);
        event.registerAboveAll("screen_text", ScreenTextManager.SCREEN_TEXT);
        event.registerAboveAll("doomsday_sanity", SanityOverlay.HUD_SANITY);
        event.registerAboveAll("screen_effects", ScreenEffectOverlay.HUD_SCREEN_EFFECTS);
    }
} 