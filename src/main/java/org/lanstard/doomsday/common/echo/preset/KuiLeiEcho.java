package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.common.entities.ModEntities;
import org.lanstard.doomsday.common.entities.PuppetEntity;
import net.minecraft.nbt.CompoundTag;

public class KuiLeiEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.KUILEI;
    private static final int SANITY_COST = 150;              // 理智消耗
    private static final int COOL_DOWN = 1200;                // 1分钟冷却
    private static final int FREE_COST_THRESHOLD = 300;      // 免费释放阈值
    private static final int MIN_FAITH_REQUIREMENT = 10;     // 最低信念要求
    private static final float PUPPET_SCALE = 1.0f;          // 傀儡大小
    
    private long cooldownEndTime = 0;

    public KuiLeiEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            SANITY_COST,  // 主动技能消耗
            0            // 无被动消耗
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...我听到了傀儡的回响..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 纯主动技能，无需更新
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...你的回响消散了..."));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 这是一个主动技能，不需要切换
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...傀儡只能主动引导..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        long timeMs = cooldownEndTime - System.currentTimeMillis();
        if (timeMs > 0) {
            long remainingSeconds = timeMs / 20 / 50;
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...傀儡之力尚需" + remainingSeconds + "秒恢复..."));
            return false;
        }
        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = faith >= MIN_FAITH_REQUIREMENT && currentSanity < FREE_COST_THRESHOLD;

        // 如果不是免费释放且理智不足
        if (!freeCost && currentSanity < SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以引动傀儡之力..."));
            return false;
        }

        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 检查理智值和信念值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCost = faith >= MIN_FAITH_REQUIREMENT && currentSanity < FREE_COST_THRESHOLD;

        // 获取玩家位置和朝向
        Vec3 pos = player.position();
        Vec3 lookVec = player.getLookAngle();
        
        // 在玩家前方生成傀儡
        double spawnX = pos.x + lookVec.x * 2;
        double spawnY = pos.y;
        double spawnZ = pos.z + lookVec.z * 2;
        
        // 创建傀儡实体
        PuppetEntity puppet = new PuppetEntity(ModEntities.PUPPET.get(), player.level());
        puppet.setPos(spawnX, spawnY, spawnZ);
        puppet.setOwner(player);  // 设置使用者为傀儡的主人
        
        // 将傀儡添加到世界
        player.level().addFreshEntity(puppet);

        // 消耗理智
        if (!freeCost) {
            SanityManager.modifySanity(player, -SANITY_COST);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + SANITY_COST + "点心神，傀儡已成..."));
        } else {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...信念引导，傀儡已成..."));
        }

        // 设置冷却
        cooldownEndTime = System.currentTimeMillis() + (COOL_DOWN * 50);
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("cooldownEndTime", cooldownEndTime);
        return tag;
    }
    
    public static KuiLeiEcho fromNBT(CompoundTag tag) {
        KuiLeiEcho echo = new KuiLeiEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.cooldownEndTime = tag.getLong("cooldownEndTime");
        return echo;
    }
} 