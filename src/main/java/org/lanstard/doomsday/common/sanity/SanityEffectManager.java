package org.lanstard.doomsday.common.sanity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.sanity.config.SanityConfig;
import org.lanstard.doomsday.common.sanity.config.SanityConfigData;

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
       SanityConfigData config = SanityConfig.getConfig();
       float totalHealthModifier = 0;

       // 应用所有匹配区间的效果
       for (SanityConfigData.ThresholdEffect threshold : config.thresholds) {
           if (sanity >= threshold.range.min && sanity <= threshold.range.max) {
               // 应用效果
               for (SanityConfigData.EffectEntry effect : threshold.effects) {
                   player.addEffect(new MobEffectInstance(
                       effect.getMobEffect(),
                       effect.duration,
                       effect.amplifier
                   ));
               }

               // 累加生命值修改器
               totalHealthModifier += threshold.health_modifier;
           }
       }

       // 使用属性修改器来应用生命值变化
       var attribute = player.getAttribute(Attributes.MAX_HEALTH);
       var modifierId = java.util.UUID.fromString("b9c99a89-f5c9-4624-9d38-4a1f5d5a2e3a"); // 固定UUID用于识别这个修改器
       
       // 移除旧的修改器（如果存在）
        if (attribute != null) {
            attribute.removePermanentModifier(modifierId);
        }

        // 只有在有修改时才添加修改器
       if (totalHealthModifier != 0) {
           if (attribute != null) {
               attribute.addPermanentModifier(new AttributeModifier(
                   modifierId,
                   "Sanity Health Modifier",
                   totalHealthModifier,
                   AttributeModifier.Operation.ADDITION
               ));
           }
       }
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