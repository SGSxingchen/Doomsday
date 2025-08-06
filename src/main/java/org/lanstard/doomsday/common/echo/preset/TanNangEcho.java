package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.lanstard.doomsday.common.echo.BasicEcho;
import org.lanstard.doomsday.common.echo.EchoType;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.config.EchoConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TanNangEcho extends BasicEcho {
    private long lastUseTime = 0;
    
    private static final Random RANDOM = new Random();

    public TanNangEcho() {
        super("tannang", "探囊", EchoType.ACTIVE, EchoConfig.TANNANG_SANITY_COST.get());
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查冷却时间
        long currentTime = player.level().getGameTime();
        int cooldown = EchoConfig.TANNANG_COOLDOWN_TICKS.get();
        if (currentTime - lastUseTime < cooldown) {
            int remainingSeconds = (int)((cooldown - (currentTime - lastUseTime)) / 20);
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f探囊之法尚需" + remainingSeconds + "秒冷却..."));
            return false;
        }

        // 检查理智值是否足够
        int currentSanity = SanityManager.getSanity(player);
        int sanityCost = EchoConfig.TANNANG_SANITY_COST.get();
        if (currentSanity < sanityCost) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f心神不足，无法施展探囊之法...（需要" + sanityCost + "点理智）"));
            return false;
        }

        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 寻找目标玩家
        ServerPlayer target = findTarget(player);
        
        if (target == null) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f未找到合适的目标..."));
            return;
        }
        
        if (target == player) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f无法对自己使用探囊之法..."));
            return;
        }
        
        // 更新最后使用时间
        lastUseTime = player.level().getGameTime();
        
        // 消耗理智值
        int sanityCost = EchoConfig.TANNANG_SANITY_COST.get();
        SanityManager.modifySanity(player, -sanityCost);
        
        // 获取目标玩家的随机物品
        ItemStack stolenItem = stealRandomItem(target);
        
        if (stolenItem.isEmpty()) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f目标身上没有可以获取的物品..."));
            // 虽然没有获得物品，但理智已经消耗了
        } else {
            // 将物品添加到施法者背包
            if (!player.getInventory().add(stolenItem)) {
                // 如果背包满了，将物品掉落在地上
                player.drop(stolenItem, false);
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f探囊之法成功获得了" + stolenItem.getDisplayName().getString() + "，但背包已满，物品掉落在地上..."));
            } else {
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f探囊之法成功获得了" + stolenItem.getDisplayName().getString() + "..."));
            }
            
            // 通知目标玩家
            target.sendSystemMessage(Component.literal("§c[十日终焉] §f你感觉到有什么东西被" + player.getDisplayName().getString() + "的探囊之法取走了..."));
        }
        
        // 更新状态
        updateState(player);
        notifyEchoClocks(player);
    }
    
    private ServerPlayer findTarget(ServerPlayer caster) {
        Vec3 start = caster.getEyePosition();
        Vec3 direction = caster.getLookAngle();
        double reach = EchoConfig.TANNANG_BASE_REACH.get();
        Vec3 end = start.add(direction.scale(reach));
        
        // 首先检查射线路径上是否有方块阻挡
        BlockHitResult blockHit = caster.level().clip(new ClipContext(start, end, 
            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster));
        
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            // 如果有方块阻挡，将终点设置为碰撞点
            end = blockHit.getLocation();
        }
        
        // 寻找射线路径上的玩家
        double inflate = EchoConfig.TANNANG_TARGET_BOX_INFLATE.get();
        AABB searchBox = new AABB(start, end).inflate(inflate);
        
        List<Player> nearbyPlayers = caster.level().getEntitiesOfClass(Player.class, searchBox);
        
        ServerPlayer closestTarget = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (Player player : nearbyPlayers) {
            if (player == caster) continue;
            if (!(player instanceof ServerPlayer)) continue;
            
            // 检查玩家是否在射线路径上
            AABB playerBounds = player.getBoundingBox();
            if (playerBounds.clip(start, end).isPresent()) {
                double distance = caster.distanceTo(player);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestTarget = (ServerPlayer) player;
                }
            }
        }
        
        return closestTarget;
    }
    
    private ItemStack stealRandomItem(ServerPlayer target) {
        List<ItemStack> availableItems = new ArrayList<>();
        
        // 收集目标玩家的所有非空物品（包括主背包和快捷栏）
        for (int i = 0; i < target.getInventory().getContainerSize(); i++) {
            ItemStack stack = target.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                availableItems.add(stack);
            }
        }
        
        if (availableItems.isEmpty()) {
            return ItemStack.EMPTY;
        }
        
        // 随机选择一个物品
        ItemStack selectedStack = availableItems.get(RANDOM.nextInt(availableItems.size()));
        
        // 创建被偷取物品的副本（随机数量1-原数量）
        int maxCount = selectedStack.getCount();
        int stolenCount = 1 + RANDOM.nextInt(maxCount);
        ItemStack stolenItem = selectedStack.copy();
        stolenItem.setCount(stolenCount);
        
        // 从目标玩家背包中移除对应数量的物品
        for (int i = 0; i < target.getInventory().getContainerSize(); i++) {
            ItemStack stack = target.getInventory().getItem(i);
            if (ItemStack.isSameItemSameTags(stack, selectedStack)) {
                if (stack.getCount() <= stolenCount) {
                    // 如果物品数量不足或刚好，直接移除整个物品堆
                    stolenItem.setCount(stack.getCount());
                    target.getInventory().setItem(i, ItemStack.EMPTY);
                } else {
                    // 如果物品数量足够，减少对应数量
                    stack.shrink(stolenCount);
                    target.getInventory().setItem(i, stack);
                }
                break;
            }
        }
        
        return stolenItem;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("lastUseTime", lastUseTime);
        return tag;
    }

    public static TanNangEcho fromNBT(CompoundTag tag) {
        TanNangEcho echo = new TanNangEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.lastUseTime = tag.getLong("lastUseTime");
        return echo;
    }
}