package org.lanstard.doomsday.common.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.lanstard.doomsday.client.renderer.world3DText.World3DTextPreset;
import org.lanstard.doomsday.common.echo.EchoManager;
import org.lanstard.doomsday.common.sounds.ModSounds;
import org.lanstard.doomsday.network.NetworkManager;
import org.lanstard.doomsday.network.packet.SpawnWorld3DTextPacket;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class EchoClockBlockEntity extends BlockEntity {
    private static final int TEXT_DISPLAY_DURATION = 3 * 60 * 1000; // 3分钟 = 3 * 60 * 20 ticks
    private static final float TEXT_SCALE = 5.0f;
    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final double EFFECT_RANGE = 64.0; // 64格范围
    
    // 添加随机偏移范围
    private static final double OFFSET_RANGE = 1.0; // 随机偏移±2格
    private static final java.util.Random random = new java.util.Random();

    public EchoClockBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ECHO_CLOCK.get(), pos, state);
    }

    @Override
    public void setLevel(Level level) {
        Level oldLevel = this.level;
        super.setLevel(level);
        
        // 如果是从无到有设置level，或者level发生改变，都需要重新注册
        if (level != null && !level.isClientSide && (oldLevel == null || oldLevel != level)) {
            EchoManager.addEchoClock(level, this.getBlockPos());
        }
    }

    @Override
    public void setRemoved() {
        if (this.level != null && !this.level.isClientSide) {
            EchoManager.removeEchoClock(this.level, this.getBlockPos());
        }
        super.setRemoved();
    }

    public void onEchoUsed(String echoName) {
        if (level != null && !level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            
            // 在多个位置播放钟声以扩大范围
            int[][] soundOffsets = {
                {0, 0, 0},    // 中心点
                {32, 0, 0},   // 东
                {-32, 0, 0},  // 西
                {0, 0, 32},   // 南
                {0, 0, -32}   // 北
            };
            
            for (int[] offset : soundOffsets) {
                level.playSound(
                    null,
                    worldPosition.getX() + 0.5 + offset[0],
                    worldPosition.getY() + 0.5 + offset[1],
                    worldPosition.getZ() + 0.5 + offset[2],
                    ModSounds.CLOCK.get(),
                    SoundSource.MASTER,
                    8.0F,  // 增加音量
                    1.0F   // 音调
                );
            }

            // 在四个方向生成文本
            spawnDirectionalTexts(serverLevel, echoName);
        }
    }

    private void spawnDirectionalTexts(ServerLevel level, String echoName) {
        Vec3 basePos = Vec3.atCenterOf(worldPosition);
        String message = "我听见了「" + echoName + "」的回响";
        
        // 基础偏移量
        int[][] baseOffsets = {
            {8, -3, 0},   // 东
            {-8, -3, 0},  // 西
            {0, -3, -8},  // 南
            {0, -3, 8}    // 北
        };
        
        float[][] rotations = {
            {0, -90, 0},  // 东
            {0, 90, 0},   // 西
            {0, 0, 0},    // 南
            {0, 180, 0}   // 北
        };

        List<ServerPlayer> nearbyPlayers = level.players();
        
        // 为每个方向生成文本，添加随机偏移
        for (int i = 0; i < baseOffsets.length; i++) {
            // 计算随机偏移
            double randomX = (random.nextDouble() - 0.5) * OFFSET_RANGE;
            double randomY = (random.nextDouble() - 0.5) * OFFSET_RANGE;
            double randomZ = (random.nextDouble() - 0.5) * OFFSET_RANGE;
            
            // 应用基础偏移和随机偏移
            Vec3 textPos = basePos.add(
                baseOffsets[i][0] + randomX,
                baseOffsets[i][1] + randomY,
                baseOffsets[i][2] + randomZ
            );
            
            float[] rotation = rotations[i];
            
            for (ServerPlayer player : nearbyPlayers) {
                NetworkManager.getChannel().send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SpawnWorld3DTextPacket(
                        message,
                        textPos,
                        0.2,
                        player.getUUID(),
                        TEXT_COLOR,
                        TEXT_SCALE,
                        1.0f,
                        true,
                        TEXT_DISPLAY_DURATION,
                        0f,
                        0f,
                        false,
                        rotation[0], rotation[1], rotation[2]
                    )
                );
            }
        }
    }
} 