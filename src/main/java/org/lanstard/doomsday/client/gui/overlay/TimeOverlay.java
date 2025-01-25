package org.lanstard.doomsday.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.lanstard.doomsday.Doomsday;
import top.theillusivec4.curios.api.CuriosApi;
import net.minecraft.resources.ResourceLocation;
import org.lanstard.doomsday.client.manage.ClientTimeManager;
import org.lanstard.doomsday.common.items.ModItem;

public class TimeOverlay {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Doomsday.MODID, "textures/gui/time_bg.png");
    private static final int BASE_BG_WIDTH = 100;  // 基础背景宽度
    private static final int BASE_BG_HEIGHT = 20;  // 基础背景高度
    private static final int TEXT_COLOR = 0xFFFFFF;
    
    public static final IGuiOverlay HUD_TIME = (
            (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        
        boolean hasSoulClock = CuriosApi.getCuriosInventory(mc.player)
            .map(handler -> handler.findFirstCurio(item -> 
                item.getItem() == ModItem.SOUL_CLOCK.get()).isPresent())
            .orElse(false);
            
        if (!hasSoulClock) return;
        
        render(guiGraphics, screenWidth, screenHeight);
    });
    
    private static void render(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        float guiScale = (float)minecraft.getWindow().getGuiScale();
        
        // 根据GUI比例动态调整缩放系数
        float scaleRatio;
        if (guiScale >= 4) {
            scaleRatio = 1.0F; // GUI比例4或更高时使用1:1缩放
        } else if (guiScale == 3) {
            scaleRatio = 1.5F;
        } else {
            scaleRatio = guiScale / 2; // GUI比例1或2时使用原来的缩放
        }
        
        // 根据实际缩放计算尺寸
        int bgWidth = (int)(BASE_BG_WIDTH * scaleRatio);
        
        String timeText = ClientTimeManager.getTimeString();
        
        // 计算位置（居中显示）
        int x = (screenWidth - bgWidth) / 2;
        int y = (int)(5 * scaleRatio);  // 距离顶部的距离也跟随缩放
        
        // 保存当前矩阵状态
        guiGraphics.pose().pushPose();
        
        // 设置缩放
        guiGraphics.pose().scale(scaleRatio, scaleRatio, 1.0F);
        
        // 调整绘制坐标以适应缩放
        float adjustedX = x / scaleRatio;
        float adjustedY = y / scaleRatio;
        
        // 绘制背景
        RenderSystem.enableBlend();
        guiGraphics.blit(TEXTURE, (int)adjustedX, (int)adjustedY, 0, 0, 
                        BASE_BG_WIDTH, BASE_BG_HEIGHT, BASE_BG_WIDTH, BASE_BG_HEIGHT);
        
        // 计算文本位置（在背景内居中）
        int textWidth = minecraft.font.width(timeText);
        float textX = adjustedX + (BASE_BG_WIDTH - textWidth) / 2;
        float textY = adjustedY + (BASE_BG_HEIGHT - 8) / 2;
        
        // 绘制文本
        guiGraphics.drawString(minecraft.font, timeText, (int)textX, (int)textY, TEXT_COLOR, true);
        
        // 恢复矩阵状态
        guiGraphics.pose().popPose();
    }
} 