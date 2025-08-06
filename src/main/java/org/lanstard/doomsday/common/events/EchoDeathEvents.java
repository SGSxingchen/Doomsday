package org.lanstard.doomsday.common.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoManager;
import org.lanstard.doomsday.common.items.ModItem;
import org.lanstard.doomsday.common.items.echo.EyeItem;
import org.lanstard.doomsday.common.items.echo.EchoBallItem;
import org.lanstard.doomsday.common.items.echo.AbstractEchoStorageItem;
import org.lanstard.doomsday.common.items.echo.HeartLockItem;
import org.lanstard.doomsday.common.items.tools.ChiselItem;
import org.lanstard.doomsday.common.effects.ModEffects;
import top.theillusivec4.curios.api.CuriosCapability;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class EchoDeathEvents {
    
    @SubscribeEvent
    public static void onPlayerDeath(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;
        
        if (event.getOriginal() instanceof ServerPlayer oldPlayer && event.getEntity() instanceof ServerPlayer newPlayer) {
            // 获取玩家的所有回响
            List<Echo> echoes = EchoManager.getPlayerEchoes(oldPlayer);
            if (echoes.isEmpty()) return;

            // 获取玩家剩余的眼睛数量
            int remainingEyes = ChiselItem.getRemainingEyes(oldPlayer);
            
            // 创建一个列表来跟踪已分配的回响
            List<Echo> allocatedEchoes = new ArrayList<>();
            
            // 首先处理已装备的眼球和回响球中的回响
            oldPlayer.getCapability(CuriosCapability.INVENTORY).ifPresent(handler -> {
                handler.getStacksHandler("eyes").ifPresent(stacksHandler -> {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (!stack.isEmpty() && (stack.getItem() instanceof EyeItem || stack.getItem() instanceof EchoBallItem)) {
                            // 记录这些物品中的回响为已分配
                            var storedEchoes = ((AbstractEchoStorageItem)stack.getItem()).getStoredEchoes(stack);
                            if (storedEchoes != null) {
                                for (int j = 0; j < storedEchoes.size(); j++) {
                                    var echoTag = storedEchoes.getCompound(j);
                                    String echoId = echoTag.getString("id");
                                    // 找到对应的回响并标记为已分配
                                    echoes.stream()
                                        .filter(e -> e.getId().equals(echoId))
                                        .findFirst()
                                        .ifPresent(allocatedEchoes::add);
                                }
                            }
                        }
                    }
                });
            });
            
            // 获取未分配的回响
            List<Echo> unallocatedEchoes = new ArrayList<>(echoes);
            unallocatedEchoes.removeAll(allocatedEchoes);
            
            // 如果还有未分配的回响，根据剩余眼睛数量创建眼球
            if (!unallocatedEchoes.isEmpty() && remainingEyes > 0) {
                // 创建remainingEyes个眼球，每个眼球都包含所有未分配的回响
                for (int i = 0; i < remainingEyes; i++) {
                    ItemStack eyeStack = new ItemStack(ModItem.EYE.get());
                    EyeItem eyeItem = (EyeItem) eyeStack.getItem();
                    
                    // 储存玩家名字
                    eyeItem.setPlayerName(eyeStack, oldPlayer.getName().getString());
                    
                    // 储存所有未分配的回响
                    for (Echo echo : unallocatedEchoes) {
                        eyeItem.storeEcho(eyeStack, echo);
                    }
                    
                    // 掉落眼球
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
    
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer deadPlayer)) return;
        
        // 检查死亡玩家是否有心之印效果
        MobEffectInstance heartMarkEffect = deadPlayer.getEffect(ModEffects.HEART_MARK.get());
        if (heartMarkEffect == null) return;
        
        // 从效果的NBT中获取施法者信息
        CompoundTag effectTag = new CompoundTag();
        heartMarkEffect.save(effectTag);
        
        if (!effectTag.contains("caster_uuid")) return;
        
        String casterUuidString = effectTag.getString("caster_uuid");
        String casterName = effectTag.getString("caster_name");
        
        UUID casterUuid;
        try {
            casterUuid = UUID.fromString(casterUuidString);
        } catch (IllegalArgumentException e) {
            return;
        }
        
        // 查找施法者
        ServerPlayer caster = deadPlayer.getServer().getPlayerList().getPlayer(casterUuid);
        if (caster == null) return;
        
        // 创建心锁道具并给予施法者
        ItemStack heartLock = HeartLockItem.createWithTarget(deadPlayer.getName().getString());
        
        // 尝试添加到施法者的背包
        if (!caster.getInventory().add(heartLock)) {
            // 如果背包满了，掉落到地面
            caster.drop(heartLock, false);
        }
        
        // 发送消息通知
        caster.sendSystemMessage(Component.translatable("message.doomsday.xinsuo.heart_lock_obtained")
            .append(deadPlayer.getDisplayName()));
        
        // 移除心之印效果（虽然玩家死亡时效果会自动清除，但为了确保）
        deadPlayer.removeEffect(ModEffects.HEART_MARK.get());
    }
} 