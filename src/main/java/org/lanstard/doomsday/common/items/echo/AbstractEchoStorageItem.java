package org.lanstard.doomsday.common.items.echo;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.level.ServerPlayer;
import org.lanstard.doomsday.client.ClientPermissionManager;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoManager;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;
import org.lanstard.doomsday.config.DoomsdayConfig;
import org.lanstard.doomsday.common.sanity.SanityManager;
import java.util.List;
import java.util.ArrayList;
import top.theillusivec4.curios.api.CuriosCapability;

public abstract class AbstractEchoStorageItem extends Item implements ICurioItem {
    protected static final String TAG_ECHOES = "StoredEchoes";
    protected static final String TAG_PLAYER_NAME = "PlayerName";
    protected static final String TAG_MODIFIER_UUID = "ModifierUUID";

    public AbstractEchoStorageItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (player.level().isClientSide) {
            return InteractionResult.PASS;
        }

        if (target instanceof ServerPlayer && player instanceof ServerPlayer serverPlayer) {
            return InteractionResult.FAIL;
        }

        return InteractionResult.PASS;
    }

    protected abstract Component getEquipSuccessMessage();
    protected abstract Component getEquippedMessage();
    protected abstract Component getEquipFailMessage();

    protected boolean equipToPlayer(ServerPlayer player, ItemStack stack) {
        return player.getCapability(CuriosCapability.INVENTORY).map(handler -> {
            return handler.getStacksHandler("eyes").map(stacksHandler -> {
                for (int i = 0; i < stacksHandler.getSlots(); i++) {
                    if (stacksHandler.getStacks().getStackInSlot(i).isEmpty()) {
                        stacksHandler.getStacks().setStackInSlot(i, stack);
                        return true;
                    }
                }
                return false;
            }).orElse(false);
        }).orElse(false);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity() instanceof ServerPlayer player && hasStoredEchoes(stack)) {
            reducePlayerStats(player, stack);
            
            ListTag echoList = getStoredEchoes(stack);
            if (echoList != null) {
                for (int i = 0; i < echoList.size(); i++) {
                    CompoundTag echoTag = echoList.getCompound(i);
                    Echo echo = Echo.fromNBT(echoTag);
                    if (echo != null) {
                        EchoManager.addEcho(player, echo);
                    }
                }
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity() instanceof ServerPlayer player && hasStoredEchoes(stack)) {
            ListTag echoList = getStoredEchoes(stack);
            List<Echo> echosToRemove = new ArrayList<>();
            
            List<Echo> playerEchoes = EchoManager.getPlayerEchoes(player);
            if (echoList != null) {
                for (int i = 0; i < echoList.size(); i++) {
                    CompoundTag echoTag = echoList.getCompound(i);
                    String echoId = echoTag.getString("id");
                    
                    for (Echo echo : playerEchoes) {
                        if (echo.getId().equals(echoId)) {
                            echosToRemove.add(echo);
                            break;
                        }
                    }
                }
            }
            
            if (!echosToRemove.isEmpty()) {
                EchoManager.removeEchoes(player, echosToRemove);
            }

            restorePlayerStats(player, stack);
        }
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof ServerPlayer player) {
            player.displayClientMessage(Component.literal("§c...眼球装配功能已被禁用..."), true);
            return false;
        }
        return false;
    }

    protected void reducePlayerStats(ServerPlayer player, ItemStack stack) {
        int healthReduction = DoomsdayConfig.ECHO_ITEM_HEALTH_REDUCTION.get();
        int sanityReduction = DoomsdayConfig.ECHO_ITEM_SANITY_REDUCTION.get();

        CompoundTag tag = stack.getOrCreateTag();
        String uuidString = tag.getString(TAG_MODIFIER_UUID);
        java.util.UUID modifierId;
        if (uuidString.isEmpty()) {
            modifierId = java.util.UUID.randomUUID();
            tag.putString(TAG_MODIFIER_UUID, modifierId.toString());
        } else {
            modifierId = java.util.UUID.fromString(uuidString);
        }

        var attribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (attribute != null) {
            attribute.removePermanentModifier(modifierId);
            attribute.addPermanentModifier(new AttributeModifier(
                modifierId,
                "Echo Item Health Reduction",
                -healthReduction,
                AttributeModifier.Operation.ADDITION
            ));
        }

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }

        SanityManager.modifyMaxSanity(player, -sanityReduction);

        player.displayClientMessage(Component.literal("§c...你的生命上限减少了" + healthReduction + "点..."), true);
        player.displayClientMessage(Component.literal("§c...你的理智上限减少了" + sanityReduction + "点..."), true);
    }

    protected void restorePlayerStats(ServerPlayer player, ItemStack stack) {
        int healthReduction = DoomsdayConfig.ECHO_ITEM_HEALTH_REDUCTION.get();
        int sanityReduction = DoomsdayConfig.ECHO_ITEM_SANITY_REDUCTION.get();

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_MODIFIER_UUID)) {
            var modifierId = java.util.UUID.fromString(tag.getString(TAG_MODIFIER_UUID));
            var attribute = player.getAttribute(Attributes.MAX_HEALTH);
            if (attribute != null) {
                attribute.removePermanentModifier(modifierId);
            }
        }

        SanityManager.modifyMaxSanity(player, sanityReduction);

        player.displayClientMessage(Component.literal("§b...你的生命上限恢复了" + healthReduction + "点..."), true);
        player.displayClientMessage(Component.literal("§b...你的理智上限恢复了" + sanityReduction + "点..."), true);
    }

    public void storeEcho(ItemStack stack, Echo echo) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag echoList = getStoredEchoes(stack);
        if (echoList == null) {
            echoList = new ListTag();
        }
        echoList.add(echo.toNBT());
        tag.put(TAG_ECHOES, echoList);
    }

    public void setPlayerName(ItemStack stack, String name) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(TAG_PLAYER_NAME, name);
    }

    public String getPlayerName(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getString(TAG_PLAYER_NAME) : null;
    }

    public ListTag getStoredEchoes(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_ECHOES)) {
            return tag.getList(TAG_ECHOES, 10);
        }
        return null;
    }

    public boolean hasStoredEchoes(ItemStack stack) {
        ListTag echoes = getStoredEchoes(stack);
        return echoes != null && !echoes.isEmpty();
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(getTooltipTranslation().getString()).withStyle(ChatFormatting.GRAY));
        
        String playerName = getPlayerName(stack);
        if (playerName != null && !playerName.isEmpty()) {
            tooltip.add(Component.literal("来自: " + playerName).withStyle(ChatFormatting.YELLOW));
        }

        ListTag echoList = getStoredEchoes(stack);
        if (echoList != null && !echoList.isEmpty()) {
            int count = echoList.size();
            tooltip.add(Component.literal("储存了 " + count + " 种回响").withStyle(ChatFormatting.GOLD));

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

    protected abstract Component getTooltipTranslation();
} 