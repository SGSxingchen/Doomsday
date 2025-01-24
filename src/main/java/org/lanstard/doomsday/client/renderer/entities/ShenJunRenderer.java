package org.lanstard.doomsday.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.lanstard.doomsday.client.model.ShenJunModel;
import org.lanstard.doomsday.common.entities.ShenJunEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShenJunRenderer extends GeoEntityRenderer<ShenJunEntity> {
    public ShenJunRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ShenJunModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(ShenJunEntity entity, float entityYaw, float partialTicks, PoseStack stack,
                      MultiBufferSource bufferIn, int packedLightIn) {
        stack.scale(1.0F, 1.0F, 1.0F); // 可以调整实体大小
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
} 