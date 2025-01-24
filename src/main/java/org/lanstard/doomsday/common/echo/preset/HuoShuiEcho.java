package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;
import net.minecraft.nbt.CompoundTag;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;

public class HuoShuiEcho extends Echo {
    private static final EchoPreset PRESET = EchoPreset.HUOSHUI;
    private static final int RANGE = 10;                       // 影响范围
    private static final int FIRE_COUNT = 30;                  // 火焰数量
    private static final int SANITY_REDUCTION = 50;           // 理智降低量
    private static final int NORMAL_COOLDOWN = 72000;         // 60分钟冷却
    private static final int LOW_SANITY_COOLDOWN = 12000;     // 10分钟冷却
    private static final int LOW_SANITY_THRESHOLD = 200;      // 低理智阈值
    private static final long NIGHT_TIME = 18000;             // 夜晚时间点
    private static final long DAY_TIME = 6000;                // 白天时间点
    
    // 粒子效果相关
    private static final float DARK_RED = 0.8F;
    private static final float DARK_GREEN = 0.2F;
    private static final float DARK_BLUE = 0.2F;
    private static final float PARTICLE_SIZE = 1.0F;
    
    private long lastDayTime = 0;
    private long cooldownEndTime = 0;
    private final Random random = new Random();

    public HuoShuiEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            0,  // 无主动技能消耗
            0   // 无被动消耗
        );
        setActive(true); // 默认激活
    }

    @Override
    public void onActivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...祸水之力涌动，灾厄将至..."));
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        Level level = player.level();
        long currentTime = level.getDayTime() % 24000;
        
        // 检查昼夜变化
        if (lastDayTime != currentTime) {
            // 夜晚降临
            if (isTimeAround(currentTime, NIGHT_TIME)) {
                spawnRandomFires(player);
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...夜幕降临，祸水引火..."));
            }
            // 白天降临
            else if (isTimeAround(currentTime, DAY_TIME)) {
                reduceSanityForNearbyPlayers(player);
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...黎明将至，祸水扰心..."));
            }
            lastDayTime = currentTime;
        }
    }

    private boolean isTimeAround(long currentTime, long targetTime) {
        // 检查时间是否在目标时间点附近
        long diff = Math.abs(currentTime - targetTime);
        return diff <= 100 || diff >= 23900;  // 允许100tick的误差
    }

    private void spawnRandomFires(ServerPlayer player) {
        Level level = player.level();
        if (!(level instanceof ServerLevel)) return;

        BlockPos playerPos = player.blockPosition();
        for (int i = 0; i < FIRE_COUNT; i++) {
            int x = playerPos.getX() + random.nextInt(RANGE * 2) - RANGE;
            int z = playerPos.getZ() + random.nextInt(RANGE * 2) - RANGE;
            
            // 找到最上面的实体方块
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, playerPos.getY() + RANGE, z);
            while (pos.getY() > playerPos.getY() - RANGE) {
                pos.setY(pos.getY() - 1);
                if (level.getBlockState(pos).isSolidRender(level, pos)) {
                    BlockPos firePos = pos.above();
                    if (level.getBlockState(firePos).isAir()) {
                        level.setBlock(firePos, Blocks.FIRE.defaultBlockState(), 3);
                        break;
                    }
                }
            }
        }
        
        // 生成粒子效果
        spawnEffectParticles((ServerLevel)level, player.position());
    }

    private void reduceSanityForNearbyPlayers(ServerPlayer player) {
        Level level = player.level();
        AABB box = player.getBoundingBox().inflate(RANGE);
        List<ServerPlayer> nearbyPlayers = level.getEntitiesOfClass(ServerPlayer.class, box);
        
        for (ServerPlayer target : nearbyPlayers) {
            if (target != player) {
                SanityManager.modifySanity(target, -SANITY_REDUCTION);
                target.sendSystemMessage(Component.literal("§c[十日终焉] §f...祸水扰心，理智涣散(" + SANITY_REDUCTION + ")..."));
            }
        }
        
        // 生成粒子效果
        if (level instanceof ServerLevel serverLevel) {
            spawnEffectParticles(serverLevel, player.position());
        }
    }

    private void spawnEffectParticles(ServerLevel level, Vec3 pos) {
        DustParticleOptions darkParticle = new DustParticleOptions(
            new Vector3f(DARK_RED, DARK_GREEN, DARK_BLUE),
            PARTICLE_SIZE
        );
        
        // 生成环形粒子效果
        for (int i = 0; i < 36; i++) {
            double angle = 2.0 * Math.PI * i / 36;
            double radius = RANGE;
            double x = pos.x + radius * Math.cos(angle);
            double z = pos.z + radius * Math.sin(angle);
            
            level.sendParticles(darkParticle,
                x, pos.y + 0.1, z,
                1, 0, 0.1, 0, 0);
        }
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...祸水之力暂歇..."));
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        // 这是一个纯被动技能，不需要手动开关
        player.sendSystemMessage(Component.literal("§c[十日终焉] §f...祸水之力无法人为控制..."));
    }

    @Override
    protected boolean doCanUse(ServerPlayer player) {
        if (System.currentTimeMillis() < cooldownEndTime) {
            long remainingSeconds = (cooldownEndTime - System.currentTimeMillis()) / 1000;
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...祸水之力尚未恢复，剩余" + remainingSeconds + "秒..."));
            return false;
        }
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 获取玩家指向的实体
        var target = player.level().getNearestPlayer(player, 10);
        if (target == null) {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...未找到目标..."));
            return;
        }

        // 恢复目标理智
        int maxSanity = SanityManager.getMaxSanity((ServerPlayer)target);
        int targetSanity = SanityManager.getSanity((ServerPlayer)target);
        int recoveryAmount = maxSanity - targetSanity;
        
        if (recoveryAmount > 0) {
            SanityManager.modifySanity((ServerPlayer)target, recoveryAmount);
            target.sendSystemMessage(Component.literal("§b[十日终焉] §f...祸水之力涌入，理智恢复(" + recoveryAmount + ")..."));
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...祸水之力已助" + target.getName().getString() + "恢复理智..."));
            
            // 设置冷却时间
            int playerSanity = SanityManager.getSanity(player);
            long cooldown = playerSanity < LOW_SANITY_THRESHOLD ? LOW_SANITY_COOLDOWN : NORMAL_COOLDOWN;
            cooldownEndTime = System.currentTimeMillis() + (cooldown * 50); // 转换为毫秒
        } else {
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...目标理智已满..."));
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("lastDayTime", lastDayTime);
        tag.putLong("cooldownEndTime", cooldownEndTime);
        return tag;
    }
    
    public static HuoShuiEcho fromNBT(CompoundTag tag) {
        HuoShuiEcho echo = new HuoShuiEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.lastDayTime = tag.getLong("lastDayTime");
        echo.cooldownEndTime = tag.getLong("cooldownEndTime");
        return echo;
    }
} 