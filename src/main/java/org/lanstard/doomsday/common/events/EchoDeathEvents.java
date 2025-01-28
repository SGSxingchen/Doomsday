package org.lanstard.doomsday.common.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoManager;
import org.lanstard.doomsday.common.items.ModItem;
import org.lanstard.doomsday.common.items.echo.EyeItem;
import org.lanstard.doomsday.common.items.tools.ChiselItem;

import java.util.List;
import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class EchoDeathEvents {
    
    @SubscribeEvent
    public static void onPlayerDeath(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;
        
        if (event.getOriginal() instanceof ServerPlayer oldPlayer && event.getEntity() instanceof ServerPlayer newPlayer) {
            // 获取玩家的所有回响
            List<Echo> echoes = EchoManager.getPlayerEchoes(oldPlayer);
            
            // 获取玩家剩余的眼睛数量
            int remainingEyes = ChiselItem.getRemainingEyes(oldPlayer);
            
            if (!echoes.isEmpty() && remainingEyes > 0) {
                // 只掉落剩余的眼睛数量
                for (int i = 0; i < remainingEyes; i++) {
                    ItemStack eyeStack = new ItemStack(ModItem.EYE.get());
                    EyeItem eyeItem = (EyeItem) eyeStack.getItem();
                    
                    // 储存玩家名字
                    eyeItem.setPlayerName(eyeStack, oldPlayer.getName().getString());
                    
                    // 将所有回响储存到眼球中
                    for (Echo echo : echoes) {
                        eyeItem.storeEcho(eyeStack, echo);
                    }
                    
                    // 在玩家死亡位置生成眼球
                    if (oldPlayer.level() != null) {
                        oldPlayer.drop(eyeStack, true, false);
                    }
                }
            }

            // 重置新玩家的眼睛状态
            ChiselItem.resetEyeState(newPlayer);
            
            // 清除所有回响
            EchoManager.removeEchoes(oldPlayer, echoes);
        }
    }
} 