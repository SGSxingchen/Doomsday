package org.lanstard.doomsday.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.lanstard.doomsday.client.model.PuppetModel;
import org.lanstard.doomsday.common.entities.PuppetEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PuppetRenderer extends GeoEntityRenderer<PuppetEntity> {
    public PuppetRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PuppetModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(PuppetEntity entity, float entityYaw, float partialTicks, PoseStack stack,
                      MultiBufferSource bufferIn, int packedLightIn) {
        stack.scale(1.0F, 1.0F, 1.0F);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
} 