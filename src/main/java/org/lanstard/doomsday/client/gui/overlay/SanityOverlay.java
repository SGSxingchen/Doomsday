package org.lanstard.doomsday.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.sanity.ClientSanityManager;

public class SanityOverlay {

    private static final int ICON_SIZE = 32; // 图标大小
    private static final int TEXT_COLOR = 0xFFFFFF;
    
    public static final IGuiOverlay HUD_SANITY = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        
        render(guiGraphics, screenWidth, screenHeight);
    });

    private static ResourceLocation getSanityIcon(int sanity) {
        int percentage = (sanity * 100) / 1000;
        int level = (percentage / 10) * 100;
        if (level > 1000) level = 1000;
        if (level < 0) level = 0;
        return new ResourceLocation(Doomsday.MODID, "textures/gui/san/" + level + ".png");
    }
    
    private static void render(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        int sanity = ClientSanityManager.getSanity();
        
        // 获取物品栏的Y位置（在屏幕底部）
        int hotbarY = screenHeight - 23;
        
        // 计算图标位置（物品栏右侧，但稍微靠内一些）
        // 物品栏最右边的槽位宽度是182，我们在它右边留一点间距
        int x = (screenWidth / 2) + 91 + 8; // 91是物品栏一半的宽度，8是间距
        int y = hotbarY - ICON_SIZE - 2;
        
        // 渲染图标
        RenderSystem.enableBlend();
        ResourceLocation iconTexture = getSanityIcon(sanity);
        guiGraphics.blit(iconTexture, x, y, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        
        // 渲染文字
        String sanityText = String.valueOf(sanity);
        int textWidth = minecraft.font.width(sanityText);
        guiGraphics.drawString(minecraft.font, sanityText, 
                             x + (ICON_SIZE - textWidth) / 2,
                             y + ICON_SIZE + 2, 
                             TEXT_COLOR, true);
    }
} 