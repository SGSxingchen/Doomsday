package org.lanstard.doomsday.sanity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.sanity.config.SanityConfig;
import org.lanstard.doomsday.sanity.config.SanityConfigData;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class SanityEffectManager {
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide) {
            ServerPlayer player = (ServerPlayer) event.player;
            int sanity = SanityManager.getSanity(player);
            
            applyEffects(player, sanity);
            handleNaturalChange(player, sanity);
        }
    }
    
    private static void applyEffects(ServerPlayer player, int sanity) {
//        SanityConfigData config = SanityConfig.getConfig();
//        float baseMaxHealth = 20.0f;
//        float totalHealthModifier = 0;
//
//        // 应用所有匹配区间的效果
//        for (SanityConfigData.ThresholdEffect threshold : config.thresholds) {
//            if (sanity >= threshold.range.min && sanity <= threshold.range.max) {
//                // 应用效果
//                for (SanityConfigData.EffectEntry effect : threshold.effects) {
//                    player.addEffect(new MobEffectInstance(
//                        effect.getMobEffect(),
//                        effect.duration,
//                        effect.amplifier
//                    ));
//                }
//
//                // 累加生命值修改器
//                totalHealthModifier += threshold.health_modifier;
//            }
//        }
//
//        // 一次性应用最终的生命值上限修改
//        float newMaxHealth = Math.max(1.0f, baseMaxHealth + totalHealthModifier); // 确保生命值上限不小于1
//        if (player.getAttribute(Attributes.MAX_HEALTH).getBaseValue() != newMaxHealth) {
//            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(newMaxHealth);
//        }
    }
    
    private static void handleNaturalChange(ServerPlayer player, int sanity) {
        SanityConfigData.NaturalChange config = SanityConfig.getConfig().natural_change;
        
        if (sanity < SanityConfig.getConfig().sanity_limits.max 
            && player.tickCount % config.regen_interval == 0) {
            SanityManager.modifySanity(player, config.regen_rate);
        }
        
        if (sanity > SanityConfig.getConfig().sanity_limits.min 
            && player.tickCount % config.drain_interval == 0) {
            SanityManager.modifySanity(player, -config.drain_rate);
        }
    }

} 