package org.lanstard.doomsday.common.items;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import org.lanstard.doomsday.client.ClientPermissionManager;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoManager;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;
import javax.annotation.Nullable;
import java.util.List;
import top.theillusivec4.curios.api.CuriosCapability;
import org.lanstard.doomsday.config.DoomsdayConfig;
import org.lanstard.doomsday.common.sanity.SanityManager;
import java.util.ArrayList;

public class EchoBallItem extends Item implements ICurioItem {
    private static final String TAG_ECHOES = "StoredEchoes";
    private static final String TAG_PLAYER_NAME = "PlayerName";
    private static final String TAG_MODIFIER_UUID = "ModifierUUID";

    public EchoBallItem(Properties properties) {
        super(properties.rarity(Rarity.EPIC).stacksTo(1));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (player.level().isClientSide) {
            return InteractionResult.PASS;
        }


        // 尝试装备到目标玩家身上
        if (target instanceof ServerPlayer targetPlayer) {
            ItemStack echoBallStack = stack.copy();
            if (equipToPlayer(targetPlayer, echoBallStack)) {
                stack.shrink(1); // 消耗物品
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.displayClientMessage(Component.literal("§b...成功为目标装配了回响球..."), true);
                }
                targetPlayer.displayClientMessage(Component.literal("§b...你被装配了回响球..."), true);
                return InteractionResult.SUCCESS;
            } else {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.displayClientMessage(Component.literal("§c...目标无法装配更多回响球..."), true);
                }
                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.PASS;
    }

    private boolean equipToPlayer(ServerPlayer player, ItemStack stack) {
        return player.getCapability(CuriosCapability.INVENTORY).map(handler -> {
            // 尝试找到一个空的eyes槽位
            return handler.getStacksHandler("eyes").map(stacksHandler -> {
                // 遍历所有eyes槽位
                for (int i = 0; i < stacksHandler.getSlots(); i++) {
                    if (stacksHandler.getStacks().getStackInSlot(i).isEmpty()) {
                        // 找到空槽位，装备物品
                        stacksHandler.getStacks().setStackInSlot(i, stack);
                        return true;
                    }
                }
                return false;
            }).orElse(false);
        }).orElse(false);
    }

    private void reducePlayerStats(ServerPlayer player, ItemStack stack) {
        // 获取配置的减少值
        int healthReduction = DoomsdayConfig.ECHO_ITEM_HEALTH_REDUCTION.get();
        int sanityReduction = DoomsdayConfig.ECHO_ITEM_SANITY_REDUCTION.get();

        // 获取或生成UUID
        CompoundTag tag = stack.getOrCreateTag();
        String uuidString = tag.getString(TAG_MODIFIER_UUID);
        java.util.UUID modifierId;
        if (uuidString.isEmpty()) {
            modifierId = java.util.UUID.randomUUID();
            tag.putString(TAG_MODIFIER_UUID, modifierId.toString());
        } else {
            modifierId = java.util.UUID.fromString(uuidString);
        }

        // 使用属性修改器减少最大生命值
        var attribute = player.getAttribute(Attributes.MAX_HEALTH);
        
        // 移除旧的修改器（如果存在）
        if (attribute != null) {
            attribute.removePermanentModifier(modifierId);
        }

        // 添加新的修改器
        if (attribute != null) {
            attribute.addPermanentModifier(new AttributeModifier(
                modifierId,
                "Echo Item Health Reduction",
                -healthReduction,
                AttributeModifier.Operation.ADDITION
            ));
        }

        // 如果当前生命值超过新的最大值，设置为新的最大值
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }

        // 减少最大理智值
        SanityManager.modifyMaxSanity(player, -sanityReduction);

        player.displayClientMessage(Component.literal("§c...你的生命上限减少了" + healthReduction + "点..."), true);
        player.displayClientMessage(Component.literal("§c...你的理智上限减少了" + sanityReduction + "点..."), true);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity() instanceof ServerPlayer player && hasStoredEchoes(stack)) {
            // 减少最大生命值和理智值
            reducePlayerStats(player, stack);
            
            // 添加回响效果
            ListTag echoList = getStoredEchoes(stack);
            for (int i = 0; i < echoList.size(); i++) {
                CompoundTag echoTag = echoList.getCompound(i);
                Echo echo = Echo.fromNBT(echoTag);
                if (echo != null) {
                    EchoManager.addEcho(player, echo);
                }
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity() instanceof ServerPlayer player && hasStoredEchoes(stack)) {
            // 获取当前存储的回响ID列表
            ListTag echoList = getStoredEchoes(stack);
            List<Echo> echosToRemove = new ArrayList<>();
            
            // 从玩家当前的回响中找到匹配的回响
            List<Echo> playerEchoes = EchoManager.getPlayerEchoes(player);
            for (int i = 0; i < echoList.size(); i++) {
                CompoundTag echoTag = echoList.getCompound(i);
                String echoId = echoTag.getString("id");
                
                // 在玩家当前的回响中查找匹配的回响
                for (Echo echo : playerEchoes) {
                    if (echo.getId().equals(echoId)) {
                        echosToRemove.add(echo);
                        break;
                    }
                }
            }
            
            // 批量移除找到的回响
            if (!echosToRemove.isEmpty()) {
                EchoManager.removeEchoes(player, echosToRemove);
            }

            // 恢复生命值和理智值上限
            int healthReduction = DoomsdayConfig.ECHO_ITEM_HEALTH_REDUCTION.get();
            int sanityReduction = DoomsdayConfig.ECHO_ITEM_SANITY_REDUCTION.get();

            // 获取修改器UUID并移除
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains(TAG_MODIFIER_UUID)) {
                var modifierId = java.util.UUID.fromString(tag.getString(TAG_MODIFIER_UUID));
                var attribute = player.getAttribute(Attributes.MAX_HEALTH);
                attribute.removePermanentModifier(modifierId);
            }

            // 恢复最大理智值
            SanityManager.modifyMaxSanity(player, sanityReduction);

            player.displayClientMessage(Component.literal("§b...你的生命上限恢复了" + healthReduction + "点..."), true);
            player.displayClientMessage(Component.literal("§b...你的理智上限恢复了" + sanityReduction + "点..."), true);
        }
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof ServerPlayer player) {
            // 检查是否有嫁接回响
            if (!EchoManager.hasSpecificEcho(player, "jiajie")) {
                player.displayClientMessage(Component.literal("§c...你需要嫁接之力才能装配回响球..."), true);
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.doomsday.echo_ball.tooltip").withStyle(ChatFormatting.GRAY));
        
        // 显示玩家名字
        String playerName = getPlayerName(stack);
        if (playerName != null && !playerName.isEmpty()) {
            tooltip.add(Component.literal("来自: " + playerName).withStyle(ChatFormatting.YELLOW));
        }

        // 显示回响数量
        ListTag echoList = getStoredEchoes(stack);
        if (echoList != null && !echoList.isEmpty()) {
            int count = echoList.size();
            tooltip.add(Component.literal("储存了 " + count + " 种回响").withStyle(ChatFormatting.GOLD));

            // 只有管理员且按下shift才显示具体回响
            if (ClientPermissionManager.hasOpPermission() && Screen.hasShiftDown()) {
                tooltip.add(Component.literal("回响列表:").withStyle(ChatFormatting.AQUA));
                for (int i = 0; i < echoList.size(); i++) {
                    CompoundTag echoTag = echoList.getCompound(i);
                    String echoName = echoTag.getString("name");
                    tooltip.add(Component.literal("- " + echoName).withStyle(ChatFormatting.DARK_AQUA));
                }
            }
        }
    }

    // 储存回响到回响球中
    public void storeEcho(ItemStack stack, Echo echo) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag echoList = getStoredEchoes(stack);
        if (echoList == null) {
            echoList = new ListTag();
        }
        echoList.add(echo.toNBT());
        tag.put(TAG_ECHOES, echoList);
    }

    // 设置玩家名字
    public void setPlayerName(ItemStack stack, String name) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(TAG_PLAYER_NAME, name);
    }

    // 获取玩家名字
    @Nullable
    public String getPlayerName(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getString(TAG_PLAYER_NAME) : null;
    }

    // 获取储存的回响列表
    @Nullable
    public ListTag getStoredEchoes(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_ECHOES)) {
            return tag.getList(TAG_ECHOES, 10); // 10 是 CompoundTag 的 ID
        }
        return null;
    }

    // 检查是否包含回响
    public boolean hasStoredEchoes(ItemStack stack) {
        ListTag echoes = getStoredEchoes(stack);
        return echoes != null && !echoes.isEmpty();
    }
} 