package org.lanstard.doomsday.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.lanstard.doomsday.common.effects.ModEffects;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoManager;
import java.util.List;

public class ChiselItem extends Item {
    private static final String TAG_LEFT_EYE = "LeftEye";
    private static final String TAG_RIGHT_EYE = "RightEye";

    public ChiselItem(Properties properties) {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 40; // 2秒
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            // 检查玩家是否已经失去了两只眼睛
            CompoundTag persistentData = player.getPersistentData();
            boolean hasLeftEye = !persistentData.getBoolean(TAG_LEFT_EYE);
            boolean hasRightEye = !persistentData.getBoolean(TAG_RIGHT_EYE);

            if (!hasLeftEye && !hasRightEye) {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...已经没有眼睛可以挖了..."));
                return stack;
            }

            // 随机选择一只还存在的眼睛挖掉
            boolean removeLeft = hasLeftEye;
            if (hasLeftEye && hasRightEye) {
                removeLeft = level.random.nextBoolean();
            }

            // 标记眼睛被挖掉
            if (removeLeft) {
                persistentData.putBoolean(TAG_LEFT_EYE, true);
                player.addEffect(new MobEffectInstance(ModEffects.LEFT_EYE_BLIND.get(), Integer.MAX_VALUE, 0, false, false));
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...你的左眼被挖掉了..."));
            } else {
                persistentData.putBoolean(TAG_RIGHT_EYE, true);
                player.addEffect(new MobEffectInstance(ModEffects.RIGHT_EYE_BLIND.get(), Integer.MAX_VALUE, 0, false, false));
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...你的右眼被挖掉了..."));
            }

            // 如果两只眼睛都被挖掉了，给予完全失明效果
            if (persistentData.getBoolean(TAG_LEFT_EYE) && persistentData.getBoolean(TAG_RIGHT_EYE)) {
                player.addEffect(new MobEffectInstance(ModEffects.FULL_BLIND.get(), Integer.MAX_VALUE, 0, false, false));
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...你失去了所有的视觉..."));
            }

            // 给予一些负面效果
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0)); // 临时失明10秒
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0)); // 反胃10秒

            // 生成眼球物品
            ItemStack eyeStack = new ItemStack(ModItem.EYE.get());
            EyeItem eyeItem = (EyeItem) eyeStack.getItem();
            
            // 储存玩家名字
            eyeItem.setPlayerName(eyeStack, player.getName().getString());
            
            // 获取并储存玩家的回响
            List<Echo> echoes = EchoManager.getPlayerEchoes(player);
            for (Echo echo : echoes) {
                eyeItem.storeEcho(eyeStack, echo);
            }
            
            // 掉落眼球
            player.drop(eyeStack, true, false);

            // 损坏物品
            stack.hurtAndBreak(1, player, (p) -> {
                p.broadcastBreakEvent(p.getUsedItemHand());
            });
        }
        return stack;
    }

    // 获取玩家剩余的眼睛数量
    public static int getRemainingEyes(Player player) {
        CompoundTag persistentData = player.getPersistentData();
        int count = 2;
        if (persistentData.getBoolean(TAG_LEFT_EYE)) count--;
        if (persistentData.getBoolean(TAG_RIGHT_EYE)) count--;
        return count;
    }

    // 重置玩家的眼睛状态（用于死亡重生）
    public static void resetEyeState(Player player) {
        CompoundTag persistentData = player.getPersistentData();
        persistentData.putBoolean(TAG_LEFT_EYE, false);
        persistentData.putBoolean(TAG_RIGHT_EYE, false);
        
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.removeEffect(ModEffects.LEFT_EYE_BLIND.get());
            serverPlayer.removeEffect(ModEffects.RIGHT_EYE_BLIND.get());
            serverPlayer.removeEffect(ModEffects.FULL_BLIND.get());
        }
    }
} 