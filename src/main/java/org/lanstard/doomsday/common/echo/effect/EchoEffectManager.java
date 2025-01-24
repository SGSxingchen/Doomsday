package org.lanstard.doomsday.common.echo.effect;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;

import java.util.*;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class EchoEffectManager {
    private static final Map<UUID, List<ActiveEffect>> activeEffects = new HashMap<>();
    
    public static void addEffect(ServerPlayer player, EchoEffect effect) {
        ActiveEffect activeEffect = new ActiveEffect(effect, effect.getDuration());
        activeEffects.computeIfAbsent(player.getUUID(), k -> new ArrayList<>())
                    .add(activeEffect);
        
        effect.onActivate(player);
    }
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide 
            && event.player instanceof ServerPlayer player) {
            
            List<ActiveEffect> effects = activeEffects.get(player.getUUID());
            if (effects == null) return;
            
            Iterator<ActiveEffect> iterator = effects.iterator();
            while (iterator.hasNext()) {
                ActiveEffect activeEffect = iterator.next();
                activeEffect.effect.onUpdate(player);
                
                if (--activeEffect.remainingDuration <= 0) {
                    activeEffect.effect.onDeactivate(player);
                    iterator.remove();
                }
            }
            
            if (effects.isEmpty()) {
                activeEffects.remove(player.getUUID());
            }
        }
    }
    
    private static class ActiveEffect {
        final EchoEffect effect;
        int remainingDuration;
        
        ActiveEffect(EchoEffect effect, int duration) {
            this.effect = effect;
            this.remainingDuration = duration;
        }
    }
} 