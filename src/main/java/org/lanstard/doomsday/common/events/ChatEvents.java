package org.lanstard.doomsday.common.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.config.DoomsdayConfig;

@Mod.EventBusSubscriber(modid = Doomsday.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChatEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onServerChat(ServerChatEvent event) {
        // 添加日志以便调试
        Doomsday.LOGGER.debug("DoomsdayMod: Processing chat event for player: " + event.getPlayer().getName().getString());
        
        // 如果本地聊天功能未启用，不处理事件
        if (!DoomsdayConfig.ENABLE_LOCAL_CHAT.get()) {
            return;
        }
        
        // 取消原始消息的发送
        event.setCanceled(true);
        
        // 获取发送消息的玩家
        ServerPlayer sender = event.getPlayer();
        String message = event.getMessage().getString();
        
        // 创建聊天消息
        Component chatMessage = Component.literal("§7[本地] §r").append(sender.getDisplayName()).append("§r: " + message);
        
        // 获取发送者的位置
        Vec3 senderPos = sender.position();
        
        // 获取配置的聊天范围
        double chatRange = DoomsdayConfig.LOCAL_CHAT_RANGE.get();
        
        // 向所有在线玩家发送消息
        sender.getServer().getPlayerList().getPlayers().forEach(player -> {
            // 如果是管理员或者是发送者本人，直接发送消息
            if (player.hasPermissions(2) || player == sender) {
                player.sendSystemMessage(chatMessage);
                return;
            }
            
            // 检查距离
            if (player.level() == sender.level()) { // 确保在同一维度
                Vec3 receiverPos = player.position();
                double distance = senderPos.distanceTo(receiverPos);
                
                // 如果在范围内，发送消息
                if (distance <= chatRange) {
                    player.sendSystemMessage(chatMessage);
                }
            }
        });
    }
} 