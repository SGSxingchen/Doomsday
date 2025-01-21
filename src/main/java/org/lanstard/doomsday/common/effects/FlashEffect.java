package org.lanstard.doomsday.common.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lanstard.doomsday.client.gui.overlay.ScreenEffectOverlay;

public class FlashEffect extends MobEffect {
    private static final int FLASH_INTERVAL = 20; // 每秒闪一次
    
    public FlashEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFFFFF); // 白色
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

} 