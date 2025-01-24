package org.lanstard.doomsday.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.sanity.SanityManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class AfterglowLampBlock extends Block {
    protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D);
    protected static final VoxelShape COLLISION_SHAPE = Shapes.empty();
    private static final int HEALING_RANGE = 10;
    private static final int PARTICLE_RANGE = 5;
    private static final int SANITY_HEAL_AMOUNT = 1;
    private static final int MAX_DAILY_HEAL = 100;
    private static final int HEAL_INTERVAL_TICKS = 60; // 3秒 = 60tick
    
    // 记录每个玩家的每日恢复量
    private static final Map<UUID, Integer> dailyHealAmount = new HashMap<>();
    // 记录上一次检查的游戏日
    private static int lastCheckedDay = 0;

    public AfterglowLampBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    public static boolean isNearAfterglowLamp(Level level, BlockPos playerPos) {
        // 检查玩家是否在任何余念灯的范围内
        for (BlockPos checkPos : BlockPos.betweenClosed(
            playerPos.offset(-HEALING_RANGE, -HEALING_RANGE, -HEALING_RANGE),
            playerPos.offset(HEALING_RANGE, HEALING_RANGE, HEALING_RANGE))) {
            if (level.getBlockState(checkPos).getBlock() instanceof AfterglowLampBlock) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel serverLevel)) {
            return;
        }

        // 检查是否是新的一天
        int currentDay = (int) (serverLevel.getDayTime() / 24000L);
        if (currentDay != lastCheckedDay) {
            lastCheckedDay = currentDay;
            dailyHealAmount.clear(); // 重置每日恢复量
        }

        // 只在夜晚工作 (14000-22000)
        long timeOfDay = serverLevel.getDayTime() % 24000L;
        if (timeOfDay < 14000 || timeOfDay > 22000) {
            return;
        }

        // 每60tick（3秒）检查一次
        if (serverLevel.getGameTime() % HEAL_INTERVAL_TICKS != 0) {
            return;
        }

        for (ServerPlayer player : serverLevel.players()) {
            if (isNearAfterglowLamp(serverLevel, player.blockPosition())) {
                UUID playerId = player.getUUID();
                int healedToday = dailyHealAmount.getOrDefault(playerId, 0);
                
                if (healedToday < MAX_DAILY_HEAL) {
                    // 恢复理智值
                    SanityManager.modifySanity(player, SANITY_HEAL_AMOUNT);
                    dailyHealAmount.put(playerId, healedToday + SANITY_HEAL_AMOUNT);
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.3f) {
            // 在半径5格的半圆范围内生成粒子
            double angle = random.nextDouble() * Math.PI; // 0到π的角度，形成半圆
            double radius = random.nextDouble() * PARTICLE_RANGE;
            double height = random.nextDouble() * PARTICLE_RANGE;
            
            double x = pos.getX() + 0.5D + Math.cos(angle) * radius;
            double y = pos.getY() + 0.5D + height;
            double z = pos.getZ() + 0.5D + Math.sin(angle) * radius;
            
            // 添加发光粒子
            level.addParticle(
                ParticleTypes.END_ROD,
                x, y, z,
                0, 0.01D, 0
            );
            
            // 随机添加一些星光粒子
            if (random.nextFloat() < 0.2f) {
                level.addParticle(
                    ParticleTypes.FIREWORK,
                    x, y, z,
                    0, 0, 0
                );
            }
        }
    }

    @Override
    public void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        RandomSource random = level.getRandom();
        // 生成破坏时的特殊粒子效果
        for(int i = 0; i < 20; i++) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            
            // 添加末地传送门粒子
            level.addParticle(
                ParticleTypes.PORTAL,
                x, y, z,
                (random.nextDouble() - 0.5) * 0.5,
                (random.nextDouble() - 0.5) * 0.5,
                (random.nextDouble() - 0.5) * 0.5
            );
            
            // 添加发光粒子
            level.addParticle(
                ParticleTypes.END_ROD,
                x, y, z,
                0, 0.1D, 0
            );
        }
        
        // 调用原版的破坏粒子效果
        super.spawnDestroyParticles(level, player, pos, state);
    }
} 