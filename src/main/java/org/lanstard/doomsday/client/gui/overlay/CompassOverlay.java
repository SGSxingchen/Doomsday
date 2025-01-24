package org.lanstard.doomsday.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.lanstard.doomsday.common.items.ModItem;
import top.theillusivec4.curios.api.CuriosApi;

public class CompassOverlay {
    public static final IGuiOverlay HUD_COMPASS = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        
        boolean hasSoulCompass = CuriosApi.getCuriosInventory(mc.player)
            .map(handler -> handler.findFirstCurio(item -> 
                item.getItem() == ModItem.SOUL_COMPASS.get()).isPresent())
            .orElse(false);
            
        if (!hasSoulCompass) return;
        
        render(guiGraphics, screenWidth, screenHeight);
    });
    
    private static void render(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        
        // 这里是原来的渲染代码
        int compassWidth = (int)(screenWidth * 0.6);
        int startX = (screenWidth - compassWidth) / 2;
        int endX = startX + compassWidth;
        int height = 18;
        int topMargin = 3;
        
        float playerYaw = (mc.player.getYRot() % 360 + 360) % 360;

        // 渲染边框装饰
        // 主背景
        guiGraphics.fill(startX, topMargin, endX, topMargin + height, 0x80000000);

        // 边框装饰
        int borderColor = 0xFF444444;
        int highlightColor = 0xFF666666;

        // 上边框
        guiGraphics.fill(startX - 1, topMargin - 1, endX + 1, topMargin, borderColor); // 上
        // 下边框
        guiGraphics.fill(startX - 1, topMargin + height, endX + 1, topMargin + height + 1, highlightColor); // 下
        // 左边框
        guiGraphics.fill(startX - 1, topMargin - 1, startX, topMargin + height + 1, borderColor); // 左
        // 右边框
        guiGraphics.fill(endX, topMargin - 1, endX + 1, topMargin + height + 1, highlightColor); // 右

        // 边角装饰
        int cornerSize = 3;
        // 左上角
        guiGraphics.fill(startX - 1, topMargin - 1, startX + cornerSize, topMargin, 0xFFFFFFFF);
        guiGraphics.fill(startX - 1, topMargin, startX, topMargin + cornerSize, 0xFFFFFFFF);
        // 右上角
        guiGraphics.fill(endX - cornerSize, topMargin - 1, endX + 1, topMargin, 0xFFFFFFFF);
        guiGraphics.fill(endX, topMargin, endX + 1, topMargin + cornerSize, 0xFFFFFFFF);
        // 左下角
        guiGraphics.fill(startX - 1, topMargin + height - cornerSize, startX, topMargin + height, 0xFFFFFFFF);
        guiGraphics.fill(startX - 1, topMargin + height, startX + cornerSize, topMargin + height + 1, 0xFFFFFFFF);
        // 右下角
        guiGraphics.fill(endX, topMargin + height - cornerSize, endX + 1, topMargin + height, 0xFFFFFFFF);
        guiGraphics.fill(endX - cornerSize, topMargin + height, endX + 1, topMargin + height + 1, 0xFFFFFFFF);

        // 渲染刻度和数字
        for (int degree = -180; degree <= 180; degree += 5) {
            float relativePosition = degree - playerYaw;
            while (relativePosition < -180) relativePosition += 360;
            while (relativePosition > 180) relativePosition -= 360;
            
            int x = startX + compassWidth / 2 + (int)(relativePosition * 2);
            
            if (x >= startX && x <= endX) {
                int displayDegree = (degree + 360) % 360;
                
                int lineHeight;
                int lineColor;
                
                if (displayDegree % 90 == 0) {
                    lineHeight = 8;
                    lineColor = 0xFFFFFFFF;
                } else if (displayDegree % 45 == 0) {
                    lineHeight = 6;
                    lineColor = 0xFFCCCCCC;
                } else if (displayDegree % 15 == 0) {
                    lineHeight = 4;
                    lineColor = 0xFFAAAAAA;
                } else {
                    lineHeight = 2;
                    lineColor = 0xFF888888;
                }
                
                // 从底部向上画刻度线
                guiGraphics.fill(x, topMargin + height - lineHeight, x + 1, topMargin + height, lineColor);
                
                if (displayDegree % 90 == 0) {
                    String direction = switch(displayDegree) {
                        case 0 -> "N";
                        case 90 -> "E";
                        case 180 -> "S";
                        case 270 -> "W";
                        default -> "";
                    };
                    guiGraphics.drawString(mc.font, direction, x - mc.font.width(direction) / 2, topMargin + 1, 0xFFFFFF);
                } else if (displayDegree % 45 == 0) {
                    String degreeText = String.valueOf(displayDegree);
                    guiGraphics.drawString(mc.font, degreeText, x - mc.font.width(degreeText) / 2, topMargin + 1, 0xFFCCCCCC);
                }
            }
        }

        // 渲染中心指示器
        int centerX = screenWidth / 2;
        guiGraphics.fill(centerX - 1, topMargin, centerX + 1, topMargin + height, 0xFFFF0000);

        // 当前角度显示
        String yawText = String.format("%.1f°", playerYaw);
        guiGraphics.drawString(mc.font, yawText, 
                centerX - mc.font.width(yawText) / 2, 
                topMargin + height + 1, 0xFFFFFF);
    }
}