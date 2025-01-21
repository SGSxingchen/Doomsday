package org.lanstard.doomsday.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.echo.Echo;
import org.lanstard.doomsday.echo.EchoManager;
import org.lanstard.doomsday.echo.EchoType;

import java.util.List;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class EchoTriggerEvents {

    // 当玩家获得回响时触发
    public static void onEchoGained(ServerPlayer player, Echo echo) {
        // 只有被动型回响会立即激活
        if (echo.getType() == EchoType.PASSIVE) {
            echo.onActivate(player);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...你获得了回响 ")
                .append(Component.literal(echo.getName()).withStyle(ChatFormatting.YELLOW))
                .append(Component.literal("，它将持续影响着你")));
        } else {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...你获得了回响 ")
                .append(Component.literal(echo.getName()).withStyle(ChatFormatting.YELLOW)));
        }
    }

    // 当玩家失去回响时触发
    public static void onEchoLost(ServerPlayer player, Echo echo) {
        echo.onDeactivate(player);
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...你失去了回响 ")
            .append(Component.literal(echo.getName()).withStyle(ChatFormatting.YELLOW)));
    }

    // 每tick更新回响效果
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide 
            && event.player instanceof ServerPlayer player) {
            List<Echo> echoes = EchoManager.getPlayerEchoes(player);

            // for (Echo echo : echoes) {
            //     player.sendSystemMessage(Component.literal("§b[十日终焉] §f回响: " + echo.getName()));
            // }
            
            for (Echo echo : echoes) {
                // 只更新已激活的回响
                if (echo.isActive()) {
                    echo.onUpdate(player);
                }
            }
        }
    }
} 