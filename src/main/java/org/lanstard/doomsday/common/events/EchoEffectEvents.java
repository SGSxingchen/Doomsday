package org.lanstard.doomsday.common.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoManager;
import org.lanstard.doomsday.common.echo.preset.BuMieEcho;
import org.lanstard.doomsday.common.echo.preset.ShuangShengHuaEcho;
import org.lanstard.doomsday.common.echo.preset.TiZuiEcho;

import java.util.List;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class EchoEffectEvents {
    
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        // 检查是否有不灭回响
        for (Echo echo : EchoManager.getPlayerEchoes(player)) {
            if (echo instanceof BuMieEcho buMieEcho && buMieEcho.isImmortal(player)) {
                // 取消死亡事件
                event.setCanceled(true);
                // 设置玩家生命值为1
                player.setHealth(1.0f);
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...不灭之力在危急时刻发动，你逃过了死亡..."));
                break;
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        
        // 检查受伤玩家是否拥有双生花回响
        for (Echo echo : EchoManager.getPlayerEchoes(player)) {
            if (echo instanceof ShuangShengHuaEcho shuangShengHuaEcho) {
                if(shuangShengHuaEcho.onDamage(player, event.getAmount(), true)){
                    event.setCanceled(true);
                }
                break;
            }
        }
        
        // 检查受伤玩家是否是其他玩家双生花的链接目标
        for (ServerPlayer otherPlayer : player.getServer().getPlayerList().getPlayers()) {
            if (otherPlayer == player) continue;
            
            for (Echo echo : EchoManager.getPlayerEchoes(otherPlayer)) {
                if (echo instanceof ShuangShengHuaEcho shuangShengHuaEcho) {
                    if (shuangShengHuaEcho.isLinkedWith(player)) {
                        if(shuangShengHuaEcho.onDamage(player, event.getAmount(), false)){
                            event.setCanceled(true);
                        }
                        return;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void tiZuiEcho(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer target)) return;

        // 检查是否有替罪者绑定了这个目标
        for (ServerPlayer player : target.getServer().getPlayerList().getPlayers()) {
            if (EchoManager.hasSpecificEcho(player, "tizui")) {
                List<Echo> echoes = EchoManager.getPlayerEchoes(player);
                for (Echo echo : echoes) {
                    if (echo instanceof TiZuiEcho tiZuiEcho) {
                        if (tiZuiEcho.isBoundTarget(target.getUUID())) {
                            // 触发替罪效果
                            event.setCanceled(true);  // 取消死亡事件
                            tiZuiEcho.onTargetDeath(target, player);
                            return;
                        }
                    }
                }
            }
        }
    }
} 