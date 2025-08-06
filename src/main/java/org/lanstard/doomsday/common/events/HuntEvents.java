package org.lanstard.doomsday.common.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.data.HuntData;
import org.lanstard.doomsday.common.effects.ModEffects;
import org.lanstard.doomsday.config.EchoConfig;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Doomsday.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HuntEvents {
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) {
            return;
        }
        
        ServerPlayer player = (ServerPlayer) event.player;
        ServerLevel serverLevel = player.serverLevel();
        HuntData huntData = HuntData.get(serverLevel);
        
        // 检查被狩猎者的警告显示
        if (player.hasEffect(ModEffects.HUNTED_MARK.get())) {
            UUID hunterUUID = huntData.getHunterOf(player.getUUID());
            if (hunterUUID != null) {
                ServerPlayer hunter = serverLevel.getServer().getPlayerList().getPlayer(hunterUUID);
                if (hunter != null && hunter.level() == player.level()) {
                    double distance = player.distanceTo(hunter);
                    
                    // 在30格内显示警告
                    if (distance <= EchoConfig.WUZHONGSHOU_WARNING_DISTANCE.get()) {
                        // 每5秒显示一次警告
                        if (serverLevel.getGameTime() % 100 == 0) {
                            player.sendSystemMessage(Component.translatable("message.doomsday.wuzhongshou.hunt_warning"));
                        }
                    }
                }
            }
        }
        
        // 检查狩猎时间是否过期
        if (serverLevel.getGameTime() % 200 == 0) { // 每10秒检查一次
            HuntData.HuntInfo huntInfo = huntData.getHuntInfo(player.getUUID());
            if (huntInfo != null) {
                long remainingTicks = huntInfo.getRemainingTicks();
                if (remainingTicks <= 0) {
                    // 狩猎时间到，自动失败
                    huntData.endHunt(serverLevel, player.getUUID(), false);
                    
                    // 移除目标身上的狩猎标记
                    ServerPlayer target = serverLevel.getServer().getPlayerList().getPlayer(huntInfo.targetUUID);
                    if (target != null) {
                        target.removeEffect(ModEffects.HUNTED_MARK.get());
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide() || !(event.getEntity() instanceof ServerPlayer deadPlayer)) {
            return;
        }
        
        ServerLevel serverLevel = deadPlayer.serverLevel();
        HuntData huntData = HuntData.get(serverLevel);
        
        // 检查死者是否是被狩猎的目标
        if (deadPlayer.hasEffect(ModEffects.HUNTED_MARK.get())) {
            UUID hunterUUID = huntData.getHunterOf(deadPlayer.getUUID());
            if (hunterUUID != null) {
                // 检查击杀者是否是狩猎者
                ServerPlayer killer = null;
                if (event.getSource().getEntity() instanceof ServerPlayer) {
                    killer = (ServerPlayer) event.getSource().getEntity();
                }
                
                if (killer != null && killer.getUUID().equals(hunterUUID)) {
                    // 狩猎成功
                    huntData.endHunt(serverLevel, hunterUUID, true);
                    
                    killer.sendSystemMessage(Component.literal("§a成功完成狩猎！"));
                    deadPlayer.sendSystemMessage(Component.literal("§c你被狩猎者击败了..."));
                } else {
                    // 被其他人击杀，狩猎失败
                    huntData.endHunt(serverLevel, hunterUUID, false);
                    
                    ServerPlayer hunter = serverLevel.getServer().getPlayerList().getPlayer(hunterUUID);
                    if (hunter != null) {
                        hunter.sendSystemMessage(Component.literal("§c狩猎失败 - 目标被他人击杀"));
                    }
                }
            }
        }
        
        // 如果死者是狩猎者，取消其狩猎
        HuntData.HuntInfo huntInfo = huntData.getHuntInfo(deadPlayer.getUUID());
        if (huntInfo != null) {
            huntData.endHunt(serverLevel, deadPlayer.getUUID(), false);
            deadPlayer.sendSystemMessage(Component.literal("§c死亡导致狩猎失败"));
        }
    }
}