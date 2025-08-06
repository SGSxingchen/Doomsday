package org.lanstard.doomsday.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.client.manage.ClientEchoManager;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.sanity.ClientSanityManager;

import java.util.List;

public class EchoStatusScreen extends Screen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Doomsday.MODID, "textures/gui/echo_status.png");
    private static final int BACKGROUND_WIDTH = 256;
    private static final int BACKGROUND_HEIGHT = 166;
    
    private int leftPos;
    private int topPos;
    
    public EchoStatusScreen() {
        super(Component.translatable("screen.doomsday.echo_status"));
    }
    
    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - BACKGROUND_WIDTH) / 2;
        this.topPos = (this.height - BACKGROUND_HEIGHT) / 2;
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        
        // 渲染背景
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        
        // 渲染理智值
        int sanity = ClientSanityManager.getSanity();
        Component sanityText = Component.translatable("gui.doomsday.sanity", sanity);
        guiGraphics.drawString(font, sanityText, leftPos + 10, topPos + 10, 0xFFFFFF);
        
        // 渲染回响列表
        List<Echo> echoes = ClientEchoManager.getEchoes();
        int yOffset = 30;
        for (Echo echo : echoes) {
            Component echoText = Component.literal(String.format(
                "%s (%s)",
                echo.getName(),
                echo.getType()
            ));
            guiGraphics.drawString(font, echoText, leftPos + 10, topPos + yOffset, 0xFFFFFF);
            yOffset += 12;
        }
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
} 