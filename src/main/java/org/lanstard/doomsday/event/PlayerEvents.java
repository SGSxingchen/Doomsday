package org.lanstard.doomsday.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.echo.EchoManager;
import org.lanstard.doomsday.echo.Echo;
import org.lanstard.doomsday.echo.preset.ShuangShengHuaEcho;
import org.lanstard.doomsday.sanity.SanityManager;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class PlayerEvents {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // 同步回响数据
            EchoManager.syncToClient(player);
            // 同步理智值数据
            SanityManager.syncToClient(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EchoManager.syncToClient(player);
            SanityManager.syncToClient(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EchoManager.syncToClient(player);
            SanityManager.syncToClient(player);
        }
    }
    
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        
        // 检查受伤玩家是否拥有双生花回响
        for (Echo echo : EchoManager.getPlayerEchoes(player)) {
            if (echo instanceof ShuangShengHuaEcho shuangShengHuaEcho) {
                // 取消原始伤害
                event.setCanceled(true);
                // 应用分摊伤害
                shuangShengHuaEcho.onDamage(player, event.getAmount(), true);
                break;
            }
        }
        
        // 检查受伤玩家是否是其他玩家双生花的链接目标
        for (ServerPlayer otherPlayer : player.getServer().getPlayerList().getPlayers()) {
            if (otherPlayer == player) continue;
            
            for (Echo echo : EchoManager.getPlayerEchoes(otherPlayer)) {
                if (echo instanceof ShuangShengHuaEcho shuangShengHuaEcho) {
                    if (shuangShengHuaEcho.isLinkedWith(player)) {
                        // 取消原始伤害
                        event.setCanceled(true);
                        // 应用分摊伤害
                        shuangShengHuaEcho.onDamage(otherPlayer, event.getAmount(), false);
                        return;
                    }
                }
            }
        }
    }
} 