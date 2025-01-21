package org.lanstard.doomsday.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class FieldBlock extends Block {
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final int FIELD_RANGE = 5;

    public FieldBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    public static boolean isProtectedByField(Level level, BlockPos pos) {
        // 检查指定位置是否在任何场地方块的保护范围内
        for (BlockPos checkPos : BlockPos.betweenClosed(
            pos.offset(-FIELD_RANGE, -FIELD_RANGE, -FIELD_RANGE),
            pos.offset(FIELD_RANGE, FIELD_RANGE, FIELD_RANGE))) {
            if (level.getBlockState(checkPos).getBlock() instanceof FieldBlock) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();
        
        // 如果是OP，允许破坏
        if (event.getPlayer().hasPermissions(2)) {
            return;
        }
        
        // 如果方块在保护范围内，取消破坏事件
        if (isProtectedByField(level, pos)) {
            event.setCanceled(true);
        }
    }

    @Override
    public void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        RandomSource random = level.getRandom();
        // 生成破坏时的特殊粒子效果
        for(int i = 0; i < 30; i++) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            
            // 添加屏障粒子（类似力场的效果）
            level.addParticle(
                ParticleTypes.ENCHANTED_HIT,
                x, y, z,
                (random.nextDouble() - 0.5) * 0.2,
                (random.nextDouble() - 0.5) * 0.2,
                (random.nextDouble() - 0.5) * 0.2
            );
            
            // 添加爆炸粒子
            if (random.nextFloat() < 0.3f) {
                level.addParticle(
                    ParticleTypes.EXPLOSION,
                    x, y, z,
                    0, 0, 0
                );
            }
        }
        
        // 调用原版的破坏粒子效果
        super.spawnDestroyParticles(level, player, pos, state);
    }
} 