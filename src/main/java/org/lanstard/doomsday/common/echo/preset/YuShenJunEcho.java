package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import org.lanstard.doomsday.common.entities.ModEntities;
import org.lanstard.doomsday.common.entities.ShenJunEntity;
import org.lanstard.doomsday.common.items.ModItem;
import net.minecraft.nbt.CompoundTag;
import org.joml.Vector3f;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public class YuShenJunEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.YUSHENJUN;
    private static final int EFFECT_RANGE = 10;
    private static final int EFFECT_DURATION = 20 * 20; // 20秒
    private static final int SUMMON_DURATION = 20 * 20; // 20秒
    private static final int COOL_DOWN = 60 * 20; // 60秒
    private static final int ACTIVE_SANITY_COST = 200;
    private static final int TOGGLE_SANITY_COST = 30;
    private static final int CONTINUOUS_SANITY_COST = 1;
    
    // 粒子效果相关常量 - 改为黄色风格
    private static final float RED = 1.0F;
    private static final float GREEN = 0.9F;
    private static final float BLUE = 0.0F;
    private static final float PARTICLE_SIZE = 1.0F;
    
    private int tickCounter = 0;
    private int cooldownTicks = 0;
    private UUID summonedEntityId = null;
    private long summonEndTime = 0;
    private ServerPlayer owner = null;
    
    public YuShenJunEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            
            ACTIVE_SANITY_COST,
            CONTINUOUS_SANITY_COST
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...御神君之力在体内流转..."));
        equipShenJunArmor(player);
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 更新召唤的实体
        if (summonedEntityId != null && player.level() instanceof ServerLevel serverLevel) {
            if (System.currentTimeMillis() >= summonEndTime) {
                removeSummonedEntity(player);
                return;
            }
            
            Optional.ofNullable(serverLevel.getEntity(summonedEntityId))
                .ifPresent(entity -> {
                    // 计算新位置
                    Vec3 lookAngle = player.getLookAngle();
                    Vec3 newPos = player.position().add(-lookAngle.x * 2, 0, -lookAngle.z * 2);
                    
                    // 设置实体位置和朝向
                    entity.setPos(newPos.x, newPos.y, newPos.z);
                    entity.setYRot(player.getYRot());
                    entity.setXRot(player.getXRot());
                    
                    // 发送位置和旋转更新包
                    serverLevel.getServer().getPlayerList().broadcastAll(
                        new ClientboundTeleportEntityPacket(entity)
                    );
                });
        }

        // 更新冷却时间
        if (cooldownTicks > 0) {
            cooldownTicks--;
        }
        
        if (!isActive()) return;
        // 每秒(20刻)消耗理智并检查是否需要停用
        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            
            // 检查理智值是否足够继续维持效果
            int currentSanity = SanityManager.getSanity(player);
            if (currentSanity < CONTINUOUS_SANITY_COST) {
                // 理智不足，自动关闭效果
                setActiveAndUpdate(player, false);
                onDeactivate(player);
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...御神之力已竭，难以为继..."));
                return;
            }
            
            // 消耗理智值
            SanityManager.modifySanity(player, -CONTINUOUS_SANITY_COST);
        }

        
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...御神之力消散，神君庇护不再..."));
        removeSummonedEntity(player);
        // 移除护甲
        player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
        player.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
        player.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
        tickCounter = 0;
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        // 检查是否在冷却中
        if (cooldownTicks > 0) {
            int remainingSeconds = cooldownTicks / 20;
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...御神之力尚需积蓄，剩余" + remainingSeconds + "秒..."));
            return false;
        }
        
        // 检查理智值是否足够
        int currentSanity = SanityManager.getSanity(player);
        if (currentSanity < ACTIVE_SANITY_COST) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以召唤御神君..."));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 消耗理智值
        SanityManager.modifySanity(player, -ACTIVE_SANITY_COST);
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + ACTIVE_SANITY_COST + "点心神，召唤御神君..."));
        
        // 召唤神君实体
        if (player.level() instanceof ServerLevel serverLevel) {
            // 计算玩家背后的位置
            Vec3 lookAngle = player.getLookAngle();
            Vec3 pos = player.position().add(-lookAngle.x * 2, 0, -lookAngle.z * 2);
            
            // 使用ModEntities创建实体
            EntityType<ShenJunEntity> entityType = ModEntities.SHENJUN.get();
            ShenJunEntity shenJun = entityType.create(serverLevel);
            
            if (shenJun != null) {
                shenJun.setPos(pos.x, pos.y, pos.z);
                shenJun.setNoGravity(true);
                shenJun.setNoAi(true);
                shenJun.setScale(3.0f);
                
                serverLevel.addFreshEntity(shenJun);
                summonedEntityId = shenJun.getUUID();
                summonEndTime = System.currentTimeMillis() + SUMMON_DURATION * 50;
                owner = player;
                updateState(player); // 更新状态
                
                // 生成召唤特效
                spawnSummonParticles(serverLevel, pos);
                
                // 对范围内玩家施加效果
                AABB box = player.getBoundingBox().inflate(EFFECT_RANGE);
                List<ServerPlayer> nearbyPlayers = serverLevel.getEntitiesOfClass(ServerPlayer.class, box);
                
                for (ServerPlayer nearbyPlayer : nearbyPlayers) {
                    nearbyPlayer.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, EFFECT_DURATION, 4, false, true));
                    nearbyPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, EFFECT_DURATION, 4, false, true));
                }
            }
        }
        
        // 设置冷却
        cooldownTicks = COOL_DOWN;
        notifyEchoClocks(player);
        updateState(player);
    }

    private void removeSummonedEntity(ServerPlayer player) {
        if (summonedEntityId != null && player.level() instanceof ServerLevel serverLevel) {
            Optional.ofNullable(serverLevel.getEntity(summonedEntityId))
                .ifPresent(entity -> {
                    spawnDespawnParticles(serverLevel, entity.position());
                    entity.discard();
                });
            summonedEntityId = null;
            summonEndTime = 0;
            owner = null;
            updateState(player); // 更新状态
        }
    }

    private void equipShenJunArmor(ServerPlayer player) {
        // 装备完整的神君盔甲套装
        ItemStack helmet = new ItemStack(ModItem.SHENJUN_HELMET.get());
        ItemStack chestplate = new ItemStack(ModItem.SHENJUN_CHESTPLATE.get());
        ItemStack leggings = new ItemStack(ModItem.SHENJUN_LEGGINGS.get());
        ItemStack boots = new ItemStack(ModItem.SHENJUN_BOOTS.get());
        
        // 设置护甲不可移除标签
        CompoundTag helmetTag = helmet.getOrCreateTag();
        CompoundTag chestplateTag = chestplate.getOrCreateTag();
        CompoundTag leggingsTag = leggings.getOrCreateTag();
        CompoundTag bootsTag = boots.getOrCreateTag();
        
        helmetTag.putBoolean("Unbreakable", true);
        chestplateTag.putBoolean("Unbreakable", true);
        leggingsTag.putBoolean("Unbreakable", true);
        bootsTag.putBoolean("Unbreakable", true);
        
        // 添加Curse of Binding诅咒
        helmet.enchant(net.minecraft.world.item.enchantment.Enchantments.BINDING_CURSE, 1);
        chestplate.enchant(net.minecraft.world.item.enchantment.Enchantments.BINDING_CURSE, 1);
        leggings.enchant(net.minecraft.world.item.enchantment.Enchantments.BINDING_CURSE, 1);
        boots.enchant(net.minecraft.world.item.enchantment.Enchantments.BINDING_CURSE, 1);
        
        player.setItemSlot(EquipmentSlot.HEAD, helmet);
        player.setItemSlot(EquipmentSlot.CHEST, chestplate);
        player.setItemSlot(EquipmentSlot.LEGS, leggings);
        player.setItemSlot(EquipmentSlot.FEET, boots);
        
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...神君铠甲附着于身..."));
    }
    
    private void spawnSummonParticles(ServerLevel level, Vec3 pos) {
        DustParticleOptions redParticle = new DustParticleOptions(new Vector3f(RED, GREEN, BLUE), PARTICLE_SIZE);
        
        // 生成环形粒子效果
        for (int i = 0; i < 36; i++) {
            double angle = 2.0 * Math.PI * i / 36;
            double radius = 2.0;
            double x = pos.x + radius * Math.cos(angle);
            double z = pos.z + radius * Math.sin(angle);
            
            level.sendParticles(redParticle,
                x, pos.y + 1, z,
                1, 0, 0.1, 0, 0);
        }
    }
    
    private void spawnDespawnParticles(ServerLevel level, Vec3 pos) {
        DustParticleOptions redParticle = new DustParticleOptions(new Vector3f(RED, GREEN, BLUE), PARTICLE_SIZE);
        
        // 生成消散效果
        for (int i = 0; i < 20; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 2;
            double offsetY = level.random.nextDouble() * 3;
            double offsetZ = (level.random.nextDouble() - 0.5) * 2;
            
            level.sendParticles(redParticle,
                pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                1, 0, 0, 0, 0);
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putInt("tick_counter", tickCounter);
        tag.putInt("cooldown_ticks", cooldownTicks);
        if (summonedEntityId != null) {
            tag.putUUID("summoned_entity", summonedEntityId);
            tag.putLong("summon_end_time", summonEndTime);
        }
        return tag;
    }
    
    public static YuShenJunEcho fromNBT(CompoundTag tag) {
        YuShenJunEcho echo = new YuShenJunEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.tickCounter = tag.getInt("tick_counter");
        echo.cooldownTicks = tag.getInt("cooldown_ticks");
        if (tag.contains("summoned_entity")) {
            echo.summonedEntityId = tag.getUUID("summoned_entity");
            echo.summonEndTime = tag.getLong("summon_end_time");
        }
        return echo;
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        if (!isActive()) {
            // 检查理智值
            int currentSanity = SanityManager.getSanity(player);
            if (currentSanity < TOGGLE_SANITY_COST) {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...心神不足，难以施展御神之法..."));
                return;
            }
            
            // 消耗理智值
            SanityManager.modifySanity(player, -TOGGLE_SANITY_COST);
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...消耗" + TOGGLE_SANITY_COST + "点心神，施展御神之法..."));
            
            setActiveAndUpdate(player, true);
            onActivate(player);
        } else {
            setActiveAndUpdate(player, false);
            onDeactivate(player);
        }
    }
} 