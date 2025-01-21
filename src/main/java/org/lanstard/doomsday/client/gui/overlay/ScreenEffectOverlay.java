package org.lanstard.doomsday.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.lanstard.doomsday.Doomsday;

public class ScreenEffectOverlay {
    private static final ResourceLocation LEFT_BLIND_TEXTURE = new ResourceLocation(Doomsday.MODID, "textures/overlay/left_half_blind.png");
    private static final ResourceLocation RIGHT_BLIND_TEXTURE = new ResourceLocation(Doomsday.MODID, "textures/overlay/right_half_blind.png");
    private static float flashAlpha = 0.0f;
    private static boolean isFlashing = false;
    private static boolean isLeftEyeBlind = false;
    private static boolean isRightEyeBlind = false;
    private static boolean isFullBlind = false;
    private static float flashTime = 0.0f;
    
    public static final IGuiOverlay HUD_SCREEN_EFFECTS = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        
        setupRenderState();
        render(guiGraphics, screenWidth, screenHeight, partialTick);
        restoreRenderState();
    });
    
    private static void setupRenderState() {
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }
    
    private static void restoreRenderState() {
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }
    
    private static void render(GuiGraphics guiGraphics, int screenWidth, int screenHeight, float partialTick) {
        // 渲染半盲效果
        if (isLeftEyeBlind || isRightEyeBlind) {
            renderHalfBlind(guiGraphics, screenWidth, screenHeight, partialTick);
        }
        
        // 渲染全盲效果
        if (isFullBlind) {
            renderFullBlind(guiGraphics, screenWidth, screenHeight);
        }
        
        // 渲染爆闪效果
        if (isFlashing) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            renderFlash(guiGraphics, screenWidth, screenHeight);
            updateFlash();
        }
    }
    
    private static void renderHalfBlind(GuiGraphics guiGraphics, int screenWidth, int screenHeight, float partialTick) {
        if (isLeftEyeBlind) {
            // 渲染左眼失明效果
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, LEFT_BLIND_TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.95F);
            guiGraphics.blit(LEFT_BLIND_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, 1000, 600);
        }
        
        if (isRightEyeBlind) {
            // 渲染右眼失明效果
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, RIGHT_BLIND_TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.95F);
            guiGraphics.blit(RIGHT_BLIND_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, 1000, 600);
        }
        
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    private static void renderFullBlind(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        guiGraphics.fill(0, 0, screenWidth, screenHeight, 0xF2000000);
    }
    
    private static void renderFlash(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        // 波浪式闪烁，强度在0.6-0.9之间波动
        float baseAlpha = 0.6f + (float)(Math.sin(flashTime * 0.1f) * 0.15f);
        int alpha = (int)(baseAlpha * 255);
        int color = (alpha << 24) | 0xFFFFFF;
        guiGraphics.fill(0, 0, screenWidth, screenHeight, color);
    }
    
    private static void updateFlash() {
        if (isFlashing) {
            flashTime += 0.1f;
        } else {
            flashTime = 0.0f;
        }
    }
    
    public static void setFlashing(boolean flashing) {
        isFlashing = flashing;
        if (!flashing) {
            flashTime = 0.0f;
        }
    }
    
    public static void setLeftEyeBlind(boolean blind) {
        isLeftEyeBlind = blind;
    }
    
    public static void setRightEyeBlind(boolean blind) {
        isRightEyeBlind = blind;
    }
    
    public static void setFullBlind(boolean blind) {
        isFullBlind = blind;
    }
} 