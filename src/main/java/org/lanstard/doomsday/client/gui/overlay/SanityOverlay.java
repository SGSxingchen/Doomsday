package org.lanstard.doomsday.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.sanity.ClientSanityManager;

public class SanityOverlay {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Doomsday.MODID, "textures/gui/sanity_bg.png");
    private static final int BASE_BG_WIDTH = 100;
    private static final int BASE_BG_HEIGHT = 20;
    private static final int TEXT_COLOR = 0xFFFFFF;
    
    public static final IGuiOverlay HUD_SANITY = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        
        render(guiGraphics, screenWidth, screenHeight);
    });
    
    private static void render(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        float scale = (float)minecraft.getWindow().getGuiScale();
        
        int bgWidth = (int)(BASE_BG_WIDTH * (scale / 2));
        int bgHeight = (int)(BASE_BG_HEIGHT * (scale / 2));
        
        String sanityText = "理智值: " + ClientSanityManager.getSanity();
        
        // 放在右上角
        int x = screenWidth - bgWidth - 5;
        int y = (int)(5 * (scale / 2));
        
        guiGraphics.pose().pushPose();
        
        float scaleRatio = scale / 2;
        guiGraphics.pose().scale(scaleRatio, scaleRatio, 1.0F);
        
        float adjustedX = x / scaleRatio;
        float adjustedY = y / scaleRatio;
        
        RenderSystem.enableBlend();
        // guiGraphics.blit(TEXTURE, (int)adjustedX, (int)adjustedY, 0, 0, 
        //                 BASE_BG_WIDTH, BASE_BG_HEIGHT, BASE_BG_WIDTH, BASE_BG_HEIGHT);
        
        int textWidth = minecraft.font.width(sanityText);
        float textX = adjustedX + (BASE_BG_WIDTH - textWidth) / 2;
        float textY = adjustedY + (BASE_BG_HEIGHT - 8) / 2;
        
        guiGraphics.drawString(minecraft.font, sanityText, (int)textX, (int)textY, TEXT_COLOR, true);
        
        guiGraphics.pose().popPose();
    }
} 