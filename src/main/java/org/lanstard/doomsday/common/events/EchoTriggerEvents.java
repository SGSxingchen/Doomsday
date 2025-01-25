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
    public static void onInventoryChange(PlayerEvent.ItemPickupEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        
        ItemStack stack = event.getStack();
        
        // 巧物回响触发
        if (stack.is(ModItem.WRENCH.get()) && !EchoManager.hasSpecificEcho(player, "qiaowu")) {
            checkAndGrantEcho(player, "qiaowu", QiaoWuEcho.class, 
                "§b[十日终焉] §f...获得扳手的瞬间，巧物回响在耳...");
        } 
        // 跃迁回响触发
        else if (stack.is(ModItem.STAR_TICKET.get()) && !EchoManager.hasSpecificEcho(player, "yueqian")) {
            checkAndGrantEcho(player, "yueqian", YueQianEcho.class,
                "§b[十日终焉] §f...获得星穹车票的瞬间，跃迁回响在耳...");
        }
        // 爆燃回响触发
        else if (stack.is(Items.FLINT_AND_STEEL) && !EchoManager.hasSpecificEcho(player, "baoran")) {
            checkAndGrantEcho(player, "baoran", BaoRanEcho.class,
                "§b[十日终焉] §f...获得打火石的瞬间，爆燃回响在耳...");
        }
        // 治愈回响触发
        else if (stack.is(ModItem.MEDKIT.get()) && !EchoManager.hasSpecificEcho(player, "zhiyu")) {
            checkAndGrantEcho(player, "zhiyu", ZhiYuEcho.class,
                "§b[十日终焉] §f...获得治疗包的瞬间，治愈的回响在耳...");
        }
        // 惊雷回响触发
        else if (stack.is(Items.LIGHTNING_ROD) && !EchoManager.hasSpecificEcho(player, "jinglei")) {
            checkAndGrantEcho(player, "jinglei", JingLeiEcho.class,
                "§b[十日终焉] §f...获得避雷针的瞬间，惊雷的回响在耳...");
        }
        // 寒冰回响触发
        else if (stack.is(Items.BLUE_ICE) && !EchoManager.hasSpecificEcho(player, "hanbing")) {
            checkAndGrantEcho(player, "hanbing", HanBingEcho.class,
                "§b[十日终焉] §f...获得蓝冰的瞬间,寒冰的回响在耳...");
        }
        // 原物回响触发
        else if (stack.is(Items.COBBLESTONE) && stack.getCount() >= 64 && !EchoManager.hasSpecificEcho(player, "yuanwu")) {
            checkAndGrantEcho(player, "yuanwu", YuanWuEcho.class,
                "§b[十日终焉] §f...获得大量圆石的瞬间,原物的回响在耳...");
        }
        // 无垢回响触发
        else if (stack.is(ModItem.WHITE_COAT.get()) && !EchoManager.hasSpecificEcho(player, "wugu")) {
            checkAndGrantEcho(player, "wugu", WuGouEcho.class,
                "§b[十日终焉] §f...获得白大褂的瞬间,无垢的回响在耳...");
        }
        // 茂木回响触发
        else if (stack.is(ModItem.GROWTH_SEED.get()) && !EchoManager.hasSpecificEcho(player, "maomu")) {
            checkAndGrantEcho(player, "maomu", MaoMuEcho.class,
                "§b[十日终焉] §f...获得催熟之种的瞬间,茂木的回响在耳...");
        }
    }

    @SubscribeEvent
    public static void onTiZuiTrigger(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        // 替罪回响触发
        float newHealth = player.getHealth() - event.getAmount();
        if (newHealth <= 5.0f && !EchoManager.hasSpecificEcho(player, "tizui")) {
            checkAndGrantEcho(player, "tizui", TiZuiEcho.class,
                "§b[十日终焉] §f...生命垂危之际，替罪的回响在耳边响起...");
        }
    }

    @SubscribeEvent
    public static void onBuMieTrigger(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        // 不灭回响触发
        if (event.getAmount() >= 10.0f && !EchoManager.hasSpecificEcho(player, "bumie")) {
            checkAndGrantEcho(player, "bumie", BuMieEcho.class,
                "§b[十日终焉] §f...遭受重创之际，不灭的回响在耳边响起...");
        }
    }

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
                    event.setCanceled(true);
                    SanityManager.modifySanity(player, -20);
                    player.sendSystemMessage(Component.literal("§b[十日终焉] §f...忘忧之力将伤害转化为心神之耗..."));
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