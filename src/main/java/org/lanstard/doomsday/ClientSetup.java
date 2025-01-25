package org.lanstard.doomsday;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lanstard.doomsday.client.gui.text.ScreenTextManager;
import org.lanstard.doomsday.client.renderer.world3DText.World3DTextRenderer;
import org.lanstard.doomsday.client.gui.overlay.ScreenEffectOverlay;
import org.lanstard.doomsday.client.renderer.entities.*;
import org.lanstard.doomsday.common.entities.ModEntities;

@Mod.EventBusSubscriber(modid = Doomsday.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(ScreenTextManager.class);
        MinecraftForge.EVENT_BUS.register(World3DTextRenderer.class);
        MinecraftForge.EVENT_BUS.register(ScreenEffectOverlay.class);
    }
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.BOMBS.get(), BombsRenderer::new);
        event.registerEntityRenderer(ModEntities.SHENJUN.get(), ShenJunRenderer::new);
        event.registerEntityRenderer(ModEntities.PUPPET.get(), PuppetRenderer::new);
        event.registerEntityRenderer(ModEntities.MUA.get(), MuaRenderer::new);
        event.registerEntityRenderer(ModEntities.LOUYI.get(), LouyiRenderer::new);
        event.registerEntityRenderer(ModEntities.FIRE_BOMB.get(), FireBombRenderer::new);
        event.registerEntityRenderer(ModEntities.ICE_BLOCK.get(), IceBlockRenderer::new);
        event.registerEntityRenderer(ModEntities.QIHEI_SWORD.get(), QiheiSwordRenderer::new);
    }
} 