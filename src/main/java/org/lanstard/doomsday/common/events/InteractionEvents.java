package org.lanstard.doomsday.common.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.echo.EchoManager;
import org.lanstard.doomsday.common.echo.preset.QiaoWuEcho;

@Mod.EventBusSubscriber(modid = Doomsday.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InteractionEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().getBlockState(event.getPos()).getBlock() instanceof CraftingTableBlock 
            && event.getEntity() instanceof ServerPlayer player) {
            
            Doomsday.LOGGER.debug("DoomsdayMod: Processing crafting table right click for player: " + player.getName().getString());
            
            // 如果玩家不是OP且没有巧物回响，则阻止使用工作台
            if (!player.hasPermissions(2) && !hasQiaoWuEcho(player)) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...缺乏巧物之力，无法使用工作台..."));
            }
        }
    }
    
    private static boolean hasQiaoWuEcho(ServerPlayer player) {
        return EchoManager.getPlayerEchoes(player).stream()
            .anyMatch(echo -> echo instanceof QiaoWuEcho);
    }
} 