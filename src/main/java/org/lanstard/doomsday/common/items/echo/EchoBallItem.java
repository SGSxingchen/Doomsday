package org.lanstard.doomsday.common.items.echo;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Rarity;

public class EchoBallItem extends AbstractEchoStorageItem {

    public EchoBallItem(Properties properties) {
        super(properties.rarity(Rarity.EPIC).stacksTo(1));
    }

    @Override
    protected Component getEquipSuccessMessage() {
        return Component.literal("§b...成功为目标装配了回响球...");
    }

    @Override
    protected Component getEquippedMessage() {
        return Component.literal("§b...你被装配了回响球...");
    }

    @Override
    protected Component getEquipFailMessage() {
        return Component.literal("§c...目标无法装配更多回响球...");
    }

    @Override
    protected Component getTooltipTranslation() {
        return Component.translatable("item.doomsday.echo_ball.tooltip");
    }
} 