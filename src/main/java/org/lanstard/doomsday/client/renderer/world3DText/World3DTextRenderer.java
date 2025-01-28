package org.lanstard.doomsday.client.renderer.world3DText;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lanstard.doomsday.utils.World3DTextData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class World3DTextRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger(World3DTextRenderer.class);
    private static final List<World3DTextData> activeTexts = new ArrayList<>();
    private static final Random RANDOM = new Random();
    
    public static void render(PoseStack poseStack, MultiBufferSource buffer, Camera camera, float partialTicks) {
        removeExpiredTexts();

        if (!activeTexts.isEmpty()) {
            Vec3 cameraPos = camera.getPosition();
            for (World3DTextData textData : activeTexts) {
                textData.update();
                renderText(textData, poseStack, buffer, camera, cameraPos, partialTicks);
            }
        }
    }
    
    private static void removeExpiredTexts() {
        int beforeCount = activeTexts.size();
        activeTexts.removeIf(World3DTextData::isExpired);
        int afterCount = activeTexts.size();
        
        if (beforeCount != afterCount) {
            // LOGGER.debug("移除过期文本: {} -> {}", beforeCount, afterCount);
        }
    }
    
    private static void renderText(World3DTextData textData, PoseStack poseStack, 
                                 MultiBufferSource buffer, Camera camera, Vec3 cameraPos, 
                                 float partialTicks) {
        try {
            float alpha = textData.getCurrentAlpha();
            if (alpha < 0.05f) return;

            poseStack.pushPose();

            poseStack.translate(textData.getX(), textData.getY(), textData.getZ());

            if (textData.isFacingPlayer()) {
                applyFacingRotation(poseStack, textData, cameraPos, camera);
            } else {
                poseStack.mulPose(Axis.XP.rotationDegrees(textData.getRotationX()));
                poseStack.mulPose(Axis.YP.rotationDegrees(textData.getRotationY()));
                poseStack.mulPose(Axis.ZP.rotationDegrees(textData.getRotationZ()));
            }

            float scale = -0.025F * textData.getScale();
            poseStack.scale(scale, scale, Math.abs(scale));
            
            // 渲染文本
            Font font = Minecraft.getInstance().font;
            float width = font.width(textData.getText());
            int color = textData.getColor() | ((int)(textData.getCurrentAlpha() * 255.0F) << 24);
            // LOGGER.info("渲染文本属性: width={}, color=0x{}, glowing={}",
            //         width,
            //         Integer.toHexString(color),
            //         textData.isGlowing()
            // );

            font.drawInBatch(
                    textData.getText(),     // 要渲染的文本内容
                    -width / 2,            // x坐标位置（将文本居中，所以用负的宽度除以2）
                    0,                     // y坐标位置
                    color,                 // 文本颜色（ARGB格式的整数）
                    false,                 // 是否启用阴影效果
                    poseStack.last().pose(), // 变换矩阵，控制文本的位置、旋转等
                    buffer,               // 渲染缓冲区，用于批量渲染
                    textData.isGlowing() ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL,  // 渲染模式：发光或普通
                    0,                    // 背景色（0表示透明）
                    15728880             // 光照值（15728880 = 0xF000F0，代表最大亮度）
            );
            
            poseStack.popPose();
        } catch (Exception e) {
            LOGGER.error("渲染文本时发生错误: ", e);
        }
    }
    
    private static void applyFacingRotation(PoseStack poseStack, World3DTextData textData, 
                                          Vec3 cameraPos, Camera camera) {
        // 直接使用相机的旋转
        poseStack.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
    }

    
    public static void spawnText(String text, World3DTextPreset preset, Vec3 position, 
                               double radius, UUID targetPlayerId) {
        if (!Minecraft.getInstance().player.getUUID().equals(targetPlayerId)) {
            return;
        }
        
        Vec3 finalPos = generateRandomPosition(position, radius);
        List<Vec3> pathPoints = preset.generatePath(finalPos, 
            Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());
        
        World3DTextData textData = new World3DTextData(
            text,
            finalPos.x, finalPos.y, finalPos.z,
            preset.getColor(),
            preset.getScale(),
            preset.getAlpha(),
            preset.isGlowing(),
            preset.getDuration(),
            preset.getFadeInTime(),
            preset.getFadeOutTime(),
            pathPoints,
            preset.isFacingPlayer(),
            0, 0, 0  // 预设模式下默认旋转角度为0
        );
        
        activeTexts.add(textData);
    }

    private static Vec3 generateRandomPosition(Vec3 center, double radius) {
        try {
            double angle = RANDOM.nextDouble() * Math.PI * 2;
            double offsetX = Math.cos(angle) * radius * RANDOM.nextDouble();
            double offsetZ = Math.sin(angle) * radius * RANDOM.nextDouble();
            double offsetY = 1.5 + (RANDOM.nextDouble() - 0.5) * 2;

            return new Vec3(
                center.x + offsetX,
                center.y + offsetY,
                center.z + offsetZ
            );
        } catch (Exception e) {
            LOGGER.error("生成随机位置时发生错误: ", e);
            return center.add(0, 2, 0);
        }
    }
    
    public static void clearAll(UUID targetPlayerId) {
        if (!Minecraft.getInstance().player.getUUID().equals(targetPlayerId)) {
            return;
        }
        activeTexts.clear();
    }
    
    public static int getActiveTextCount() {
        return activeTexts.size();
    }
    
    public static boolean hasActiveTexts() {
        return !activeTexts.isEmpty();
    }
    
    public static List<World3DTextData> getActiveTexts() {
        return new ArrayList<>(activeTexts);
    }

    public static void spawnCustomText(
            String text, Vec3 position, double radius, UUID targetPlayerId,
            int color, float scale, float alpha, boolean glowing,
            int duration, float fadeInTime, float fadeOutTime,
            boolean facingPlayer, float rotationX, float rotationY, float rotationZ) {
        
        Vec3 finalPos = generateRandomPosition(position, radius);
        List<Vec3> pathPoints = new ArrayList<>();
        
        World3DTextData textData = new World3DTextData(
            text,
            finalPos.x, finalPos.y, finalPos.z,
            color, scale, alpha, glowing,
            duration, fadeInTime, fadeOutTime,
            pathPoints, facingPlayer,
            rotationX, rotationY, rotationZ
        );
        
        activeTexts.add(textData);
    }
}