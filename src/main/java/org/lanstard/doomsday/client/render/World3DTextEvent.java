package org.lanstard.doomsday.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = Doomsday.MODID, value = Dist.CLIENT)
public class World3DTextEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(World3DTextEvent.class);
    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        // 确保在正确的渲染阶段进行渲染
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        // 保存当前矩阵状态
        poseStack.pushPose();
        
        // 调整渲染位置到相机位置
        double camX = minecraft.gameRenderer.getMainCamera().getPosition().x;
        double camY = minecraft.gameRenderer.getMainCamera().getPosition().y;
        double camZ = minecraft.gameRenderer.getMainCamera().getPosition().z;
        poseStack.translate(-camX, -camY, -camZ);

        // 渲染3D文本
        World3DTextRenderer.render(
            poseStack,
            bufferSource,
            minecraft.gameRenderer.getMainCamera(),
            event.getPartialTick()
        );

        // 确保所有缓冲区都被刷新
        bufferSource.endBatch();
        
        // 恢复矩阵状态
        poseStack.popPose();
    }
} 