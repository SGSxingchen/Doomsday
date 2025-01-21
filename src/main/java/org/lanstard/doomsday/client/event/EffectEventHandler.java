package org.lanstard.doomsday.client.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.client.gui.overlay.ScreenEffectOverlay;
import org.lanstard.doomsday.common.effects.ModEffects;

@Mod.EventBusSubscriber(modid = Doomsday.MODID, value = Dist.CLIENT)
public class EffectEventHandler {
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = net.minecraft.client.Minecraft.getInstance().player;
            if (player != null) {
                // 检查左眼失明效果
                boolean hasLeftBlind = player.hasEffect(ModEffects.LEFT_EYE_BLIND.get());
                ScreenEffectOverlay.setLeftEyeBlind(hasLeftBlind);
                
                // 检查右眼失明效果
                boolean hasRightBlind = player.hasEffect(ModEffects.RIGHT_EYE_BLIND.get());
                ScreenEffectOverlay.setRightEyeBlind(hasRightBlind);
                
                // 检查全盲效果
                boolean hasFullBlind = player.hasEffect(ModEffects.FULL_BLIND.get());
                ScreenEffectOverlay.setFullBlind(hasFullBlind);
                
                // 检查爆闪效果
                boolean hasFlash = player.hasEffect(ModEffects.FLASH.get());
                ScreenEffectOverlay.setFlashing(hasFlash);
            }
        }
    }
} 