package org.lanstard.doomsday.client.renderer.entities;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.lanstard.doomsday.client.model.LouyiModel;
import org.lanstard.doomsday.common.entities.LouyiEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LouyiRenderer extends GeoEntityRenderer<LouyiEntity> {
    public LouyiRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LouyiModel());
        this.shadowRadius = 0.5f;
    }
        @Override
    public void render(LouyiEntity entity, float entityYaw, float partialTicks, PoseStack stack,
                      MultiBufferSource bufferIn, int packedLightIn) {
        stack.scale(1.0F, 1.0F, 1.0F);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
} 