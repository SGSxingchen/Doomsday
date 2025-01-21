package org.lanstard.doomsday.server.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class JoinLeaveMessage {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // 创建消息组件
            Component message = Component.translatable("message.doomsday.prefix")
                    .append(Component.translatable(
                            "message.doomsday.player_join",
                            player.getName().getString()
                    ));
            
            // 发送给所有OP
            player.getServer().getPlayerList().getPlayers().forEach(serverPlayer -> {
                if (serverPlayer.hasPermissions(2)) {
                    serverPlayer.sendSystemMessage(message);
                }
            });
            
            // 输出到控制台
            LOGGER.info(message.getString());
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // 创建消息组件
            Component message = Component.translatable("message.doomsday.prefix")
                    .append(Component.translatable(
                            "message.doomsday.player_leave",
                            player.getName().getString()
                    ));
            
            // 发送给所有OP
            player.getServer().getPlayerList().getPlayers().forEach(serverPlayer -> {
                if (serverPlayer.hasPermissions(2)) {
                    serverPlayer.sendSystemMessage(message);
                }
            });
            
            // 输出到控制台
            LOGGER.info(message.getString());
        }
    }
} 