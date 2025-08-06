package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.AABB;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.config.EchoConfig;

import java.util.List;

public class JiaHuoEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.JIAHUO;

    public JiaHuoEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            EchoConfig.JIAHUO_SANITY_COST.get(),
            0
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f嫁祸回响已激活，伤害将转移给附近的其他玩家"));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 嫁祸回响的效果通过伤害事件处理，这里不需要持续更新
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f嫁祸回响已停用"));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        return true; // 嫁祸没有特殊的使用条件
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 嫁祸是被动效果，不需要主动使用
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f嫁祸回响是被动效果，请使用G键切换持续模式"));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        if (!isActive()) {
            // 开启持续模式
            setActiveAndUpdate(player, true);
            onActivate(player);
        } else {
            // 关闭持续模式
            setActiveAndUpdate(player, false);
            onDeactivate(player);
        }
    }

    /**
     * 处理玩家受到伤害时的伤害转移效果
     * 这个方法应该在伤害事件处理器中调用
     * 
     * @param player 受到伤害的玩家
     * @param damageSource 伤害来源
     * @param amount 伤害量
     * @return 是否成功转移伤害
     */
    public boolean tryTransferDamage(ServerPlayer player, DamageSource damageSource, float amount) {
        if (!isActive()) {
            return false;
        }
        
        // 检查玩家理智值是否足够
        int currentSanity = SanityManager.getSanity(player);
        if (currentSanity < EchoConfig.JIAHUO_DAMAGE_SANITY_COST.get()) {
            // 理智不足，停用嫁祸回响
            setActiveAndUpdate(player, false);
            onDeactivate(player);
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f理智不足，嫁祸回响自动停用"));
            return false;
        }
        
        // 寻找转移范围内的随机玩家
        AABB searchArea = new AABB(player.blockPosition()).inflate(EchoConfig.JIAHUO_TRANSFER_RANGE.get());
        List<ServerPlayer> nearbyPlayers = player.level().getEntitiesOfClass(ServerPlayer.class, searchArea,
            target -> target != player && target.isAlive() && target.isPickable());
        
        if (nearbyPlayers.isEmpty()) {
            // 没有可转移的目标，不转移伤害
            return false;
        }
        
        // 随机选择一个目标
        ServerPlayer target = nearbyPlayers.get(player.getRandom().nextInt(nearbyPlayers.size()));
        
        // 对目标造成伤害
        target.hurt(damageSource, amount);
        
        // 扣除自身理智值
        SanityManager.modifySanity(player, -EchoConfig.JIAHUO_DAMAGE_SANITY_COST.get());
        
        // 发送消息提示
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f嫁祸成功，伤害已转移给 " + target.getDisplayName().getString()));
        target.sendSystemMessage(Component.literal("§c[十日终焉] §f你承受了 " + player.getDisplayName().getString() + " 嫁祸的伤害"));
        
        // 更新状态
        updateState(player);
        notifyEchoClocks(player);
        
        return true; // 伤害转移成功
    }

    @Override
    public CompoundTag toNBT() {
        return super.toNBT();
    }

    public static JiaHuoEcho fromNBT(CompoundTag tag) {
        JiaHuoEcho echo = new JiaHuoEcho();
        echo.setActive(tag.getBoolean("isActive"));
        return echo;
    }
}