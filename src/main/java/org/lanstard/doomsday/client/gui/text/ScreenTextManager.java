package org.lanstard.doomsday.client.gui.text;

import com.mojang.blaze3d.platform.Window;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.lanstard.doomsday.utils.ScreenTextData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ScreenTextManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenTextManager.class);
    private static final List<ScreenTextData> activeTexts = new ArrayList<>();
    private static final Random RANDOM = new Random();

    public static final IGuiOverlay SCREEN_TEXT = (
            (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player == null || mc.options.hideGui) return;

                render(guiGraphics,partialTick , screenWidth, screenHeight);
            });

    public static void addText(String text, ScreenTextPreset preset) {
        try {
            // 获取屏幕尺寸
            Window window = Minecraft.getInstance().getWindow();
            int screenWidth = window.getGuiScaledWidth();
            int screenHeight = window.getGuiScaledHeight();
            
            // 随机位置 - 确保文本不会太靠近屏幕边缘
            float margin = 50.0f; // 边距
            float x = margin + RANDOM.nextFloat() * (screenWidth - 2 * margin);
            float y = margin + RANDOM.nextFloat() * (screenHeight - 2 * margin);
            
            float dx = preset.getDirection().dx;
            float dy = preset.getDirection().dy;
            if (preset.getDirection() == ScreenTextPreset.Direction.RANDOM) {
                float angle = RANDOM.nextFloat() * (float)(Math.PI * 2);
                dx = (float)Math.cos(angle);
                dy = (float)Math.sin(angle);
            }
            
            ScreenTextData textData = new ScreenTextData(
                text, x, y,
                preset.getColor(),
                preset.getScale(),
                preset.getAlpha(),
                preset.isGlowing(),
                preset.getDuration(),
                preset.getFadeInTime(),
                preset.getFadeOutTime(),
                preset.getScaleStart(),
                preset.getScaleEnd(),
                preset.getMoveSpeed(),
                preset.getRotationSpeed(),
                dx, dy
            );
            
            activeTexts.add(textData);
            // LOGGER.info("Added text: '{}' at ({}, {}), direction: ({}, {})", 
            //    text, x, y, dx, dy);
        } catch (Exception e) {
            // LOGGER.error("Error adding screen text: ", e);
        }
    }

    public static void addCustomText(String text, int color, float scale, float alpha, boolean glowing,
                                   int duration, int fadeInTime, int fadeOutTime,
                                   float scaleStart, float scaleEnd, float moveSpeed,
                                   float rotationSpeed, ScreenTextPreset.Direction direction) {
        try {
            Window window = Minecraft.getInstance().getWindow();
            int screenWidth = window.getGuiScaledWidth();
            int screenHeight = window.getGuiScaledHeight();
            
            float margin = 50.0f;
            float x = margin + RANDOM.nextFloat() * (screenWidth - 2 * margin);
            float y = margin + RANDOM.nextFloat() * (screenHeight - 2 * margin);
            
            float dx = direction.dx;
            float dy = direction.dy;
            if (direction == ScreenTextPreset.Direction.RANDOM) {
                float angle = RANDOM.nextFloat() * (float)(Math.PI * 2);
                dx = (float)Math.cos(angle);
                dy = (float)Math.sin(angle);
            }
            
            ScreenTextData textData = new ScreenTextData(
                text, x, y, color, scale, alpha, glowing,
                duration, fadeInTime, fadeOutTime,
                scaleStart, scaleEnd, moveSpeed, rotationSpeed,
                dx, dy
            );
            
            activeTexts.add(textData);
        } catch (Exception e) {
            LOGGER.error("Error adding custom screen text: ", e);
        }
    }

    // 新增：添加固定位置的文本，并使用ID进行管理
    public static void addFixedText(String id, String text, float x, float y, ScreenTextPreset preset) {
        try {
            // 移除同ID的旧文本
            activeTexts.removeIf(textData -> id.equals(textData.getId()));
            
            ScreenTextData textData = new ScreenTextData(
                text, x, y,
                preset.getColor(),
                preset.getScale(),
                preset.getAlpha(),
                preset.isGlowing(),
                preset.getDuration(),
                preset.getFadeInTime(),
                preset.getFadeOutTime(),
                preset.getScaleStart(),
                preset.getScaleEnd(),
                preset.getMoveSpeed(),
                preset.getRotationSpeed(),
                preset.getDirection().dx,
                preset.getDirection().dy,
                id  // 传入ID
            );
            
            activeTexts.add(textData);
        } catch (Exception e) {
            LOGGER.error("Error adding fixed text: ", e);
        }
    }

    // 新增：清除指定ID的文本
    public static void clearText(String id) {
        activeTexts.removeIf(textData -> id.equals(textData.getId()));
    }

    private static void renderGlowingText(GuiGraphics guiGraphics, ScreenTextData textData, 
                                        float xOffset, float yOffset) {
        if (!textData.isGlowing()) return;
        
        float glowSize = 1.0f;
        int glowColor = (textData.getColor() & 0xFEFEFE) >> 1;
        float alpha = textData.getCurrentAlpha() * 0.5f;
        int glowAlphaColor = ((int)(alpha * 255) << 24) | (glowColor & 0x00FFFFFF);
        
        guiGraphics.drawString(
            Minecraft.getInstance().font,
            Component.literal(textData.getText()),
            (int)xOffset,
            (int)yOffset,
            glowAlphaColor,
            true
        );
    }

    public static void render(GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (activeTexts.isEmpty()) {
            return;
        }
        try {
            List<ScreenTextData> textsToRemove = new ArrayList<>();
            for (ScreenTextData textData : activeTexts) {
                if (textData.isExpired()) {
                    textsToRemove.add(textData);
                    continue;
                }
                
                textData.update();
                guiGraphics.pose().pushPose();
                // 应用变换
                guiGraphics.pose().translate(textData.getX(), textData.getY(), 0);
                float currentScale = textData.getCurrentScale();
                guiGraphics.pose().scale(currentScale, currentScale, 1.0f);
                guiGraphics.pose().mulPose(Axis.ZP.rotation(textData.getRotation()));
                
                // 计算文本位置偏移
                float textWidth = Minecraft.getInstance().font.width(Component.literal(textData.getText()));
                float textHeight = Minecraft.getInstance().font.lineHeight;
                float xOffset = -textWidth / 2f;
                float yOffset = -textHeight / 2f;
                
                if (textData.isGlowing()) {
                    renderGlowingText(guiGraphics, textData, xOffset, yOffset);
                }
                
                int color = textData.getColor();
                float alpha = textData.getCurrentAlpha();
                int alphaColor = ((int)(alpha * 255) << 24) | (color & 0x00FFFFFF);
                
                guiGraphics.drawString(
                    Minecraft.getInstance().font,
                    Component.literal(textData.getText()),
                    (int)xOffset,
                    (int)yOffset,
                    alphaColor,
                    false
                );
                
                // 恢复矩阵状态
                guiGraphics.pose().popPose();
            }
            if (!textsToRemove.isEmpty()) {
                activeTexts.removeAll(textsToRemove);
            }
        } catch (Exception e) {
            LOGGER.error("Error rendering screen texts: ", e);
        }
    }
    
    // 清除所有活动文本
    public static void clearAll() {
        activeTexts.clear();
        LOGGER.debug("Cleared all active screen texts");
    }
    
    // 获取当前活动文本数量
    public static int getActiveTextCount() {
        return activeTexts.size();
    }
}