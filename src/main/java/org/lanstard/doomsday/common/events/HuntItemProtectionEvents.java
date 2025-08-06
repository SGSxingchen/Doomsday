package org.lanstard.doomsday.common.events;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.items.hunt.HuntCompassItem;
import org.lanstard.doomsday.common.items.hunt.EndBladeItem;
import org.lanstard.doomsday.common.items.hunt.InvisibilityCloakItem;

@Mod.EventBusSubscriber(modid = Doomsday.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HuntItemProtectionEvents {
    
    /**
     * 防止狩猎道具被丢弃
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onItemToss(ItemTossEvent event) {
        ItemStack stack = event.getEntity().getItem();
        
        if (isHuntItem(stack)) {
            // 取消丢弃事件
            event.setCanceled(true);
            
            // 给玩家发送消息
            Player player = event.getPlayer();
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.literal("§c狩猎道具无法丢弃！"));
                
                // 将物品返回玩家背包
                if (!player.getInventory().add(stack)) {
                    // 如果背包满了，强制放到第一个空位
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        if (player.getInventory().getItem(i).isEmpty()) {
                            player.getInventory().setItem(i, stack);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 防止狩猎道具被放入容器
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        // 检查玩家背包中的狩猎道具，防止被移动到容器中
        // 这个事件主要用于提醒，实际的移动限制在GUI层面处理
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            boolean hasHuntItems = false;
            
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (isHuntItem(stack)) {
                    hasHuntItems = true;
                    break;
                }
            }
            
            if (hasHuntItems) {
                serverPlayer.sendSystemMessage(Component.literal("§6提醒：狩猎道具无法被移动或交易"));
            }
        }
    }
    
    /**
     * 防止狩猎道具在玩家死亡时掉落（额外保险）
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            Player originalPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();
            
            // 检查原玩家是否有狩猎道具，如果有则保留到新玩家
            for (int i = 0; i < originalPlayer.getInventory().getContainerSize(); i++) {
                ItemStack stack = originalPlayer.getInventory().getItem(i);
                if (isHuntItem(stack)) {
                    // 在新玩家背包中恢复狩猎道具
                    newPlayer.getInventory().add(stack.copy());
                }
            }
        }
    }
    
    /**
     * 检查是否是狩猎道具
     */
    private static boolean isHuntItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        
        net.minecraft.world.item.Item item = stack.getItem();
        return item instanceof HuntCompassItem ||
               item instanceof EndBladeItem ||
               item instanceof InvisibilityCloakItem ||
               (item == net.minecraft.world.item.Items.ENDER_PEARL && stack.hasTag() && stack.getTag().getBoolean("hunt_item"));
    }
}