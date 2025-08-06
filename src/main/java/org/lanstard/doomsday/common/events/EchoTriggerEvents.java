package org.lanstard.doomsday.common.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoManager;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.echo.preset.*;
import org.lanstard.doomsday.common.items.ModItem;
import org.lanstard.doomsday.common.sanity.SanityManager;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class EchoTriggerEvents {
    
    // 记录玩家受到小伤害的次数和最后一次受伤时间
    private static final Map<UUID, Integer> smallDamageCounter = new HashMap<>();
    private static final Map<UUID, Long> lastSmallDamageTime = new HashMap<>();
    private static final int REQUIRED_SMALL_DAMAGE_COUNT = 10;
    private static final long SMALL_DAMAGE_TIMEOUT = 100; // 5秒 = 100tick


    @SubscribeEvent
    public static void onWangYouTrigger(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        
        // 忘忧回响触发
        if (event.getAmount() < 2.0f && !EchoManager.hasSpecificEcho(player, "wangyou")) {
            UUID playerId = player.getUUID();
            long currentTime = player.level().getGameTime();
            
            // 检查是否超时
            if (lastSmallDamageTime.containsKey(playerId)) {
                long timeDiff = currentTime - lastSmallDamageTime.get(playerId);
                if (timeDiff > SMALL_DAMAGE_TIMEOUT) {
                    // 超时重置计数
                    smallDamageCounter.put(playerId, 1);
                } else {
                    // 增加计数
                    int count = smallDamageCounter.getOrDefault(playerId, 0) + 1;
                    smallDamageCounter.put(playerId, count);
                    
                    // 达到所需次数
                    if (count >= REQUIRED_SMALL_DAMAGE_COUNT) {
                        checkAndGrantEcho(player, "wangyou", WangYouEcho.class,
                            "§b[十日终焉] §f...连续的微弱痛楚中，忘忧的回响在耳边响起...");
                        // 重置计数
                        smallDamageCounter.remove(playerId);
                        lastSmallDamageTime.remove(playerId);
                        return;
                    }
                }
            } else {
                // 第一次受到小伤害
                smallDamageCounter.put(playerId, 1);
            }
            
            // 更新最后受伤时间
            lastSmallDamageTime.put(playerId, currentTime);
        }
        
        // 忘忧回响效果处理
        if (EchoManager.hasSpecificEcho(player, "wangyou")) {
            for (Echo echo : EchoManager.getPlayerEchoes(player)) {
                if (echo instanceof WangYouEcho && echo.isActive()) {
                    int faith = SanityManager.getFaith(player);
                    float reduction = WangYouEcho.BASE_DAMAGE_REDUCTION + (faith / 2) * WangYouEcho.FAITH_DAMAGE_REDUCTION;
                    reduction = Math.min(0.9F, reduction);
                    
                    // 计算减免的伤害量
                    float originalDamage = event.getAmount();
                    float reducedDamage = originalDamage * (1 - reduction);
                    float blockedDamage = originalDamage - reducedDamage;
                    
                    // 设置减免后的伤害
                    event.setAmount(reducedDamage);
                    
                    // 消耗理智值（每减免1点伤害消耗2点理智）
                    int sanityCost = Math.round(blockedDamage * 2);
                    SanityManager.modifySanity(player, -sanityCost);
                    
                    // 显示减伤效果和理智消耗
                    player.sendSystemMessage(Component.literal("§b[十日终焉] §f...忘忧之力减免了")
                        .append(Component.literal(String.format("%.1f", reduction * 100)))
                        .append(Component.literal("%的伤害"))
                        .append(Component.literal(String.format("，消耗%d点心神...", sanityCost))));
                    break;
                }
            }
        }
    }

    // 通用的检查和授予回响方法
    private static <T extends Echo> void checkAndGrantEcho(ServerPlayer player, String echoId, Class<T> echoClass, String message) {
        // 检查玩家是否已经拥有该回响
        if (EchoManager.hasSpecificEcho(player, echoId)) {
            return;
        }
        
        // 检查其他玩家是否已经拥有该回响
        boolean otherHasEcho = player.getServer().getPlayerList().getPlayers().stream()
            .filter(p -> !p.equals(player) && !p.hasPermissions(2))
            .anyMatch(p -> EchoManager.hasSpecificEcho(p, echoId));
            
        if (!otherHasEcho) {
            Echo echo = EchoPreset.valueOf(echoId.toUpperCase()).createEcho();
            EchoManager.addEcho(player, echo);
            player.sendSystemMessage(Component.literal(message));
        }
    }
} 