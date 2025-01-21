package org.lanstard.doomsday.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.lanstard.doomsday.client.data.ClientDisplayData;

@Mixin(Gui.class)
public class DisplayHealthRenderMixin {
    @Inject(method = "renderHearts", at = @At("HEAD"), cancellable = true)
    private void onRenderHearts(GuiGraphics graphics, Player player, int x, int y, int lines,
                               int regeneratingHeartIndex, float maxHealth, int lastHealth,
                               int health, int absorption, boolean blinking, CallbackInfo ci) {
        // 根据配置决定是否渲染生命值
        if (!ClientDisplayData.canSeeHealth()) {
            ci.cancel();
        }
    }
}