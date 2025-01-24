package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.items.ModItem;
import org.lanstard.doomsday.common.sanity.SanityManager;
import net.minecraft.nbt.CompoundTag;

public class QiaoWuEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.QIAOWU;
    private static final int MIN_FAITH = 10;            // 最小信念要求
    private static final int FREE_COST_THRESHOLD = 300; // 免费释放阈值

    public QiaoWuEcho() {
        super(PRESET.name().toLowerCase(), PRESET.getDisplayName(), PRESET.getType(), PRESET.getActivationType(), 0, 0);
        setActive(true); // 始终处于激活状态
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...巧物回响在耳，工艺之力涌动..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 这是一个被动技能，不需要更新
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...巧物消散，工艺之力褪去..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 这是一个纯被动技能，不需要主动使用
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...巧物之力始终与你同在..."));
        return false;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 这是一个纯被动技能，不需要主动使用
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 这是一个纯被动技能，不需要切换状态
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...巧物之力始终与你同在..."));
    }

    @Override
    public CompoundTag toNBT() {
        return super.toNBT();
    }
    
    public static QiaoWuEcho fromNBT(CompoundTag tag) {
        QiaoWuEcho echo = new QiaoWuEcho();
        return echo;
    }
} 