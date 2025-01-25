package org.lanstard.doomsday.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.lanstard.doomsday.client.model.QiheiSwordModel;
import org.lanstard.doomsday.common.entities.QiheiSwordEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class QiheiSwordRenderer extends GeoEntityRenderer<QiheiSwordEntity> {
    public QiheiSwordRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new QiheiSwordModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(QiheiSwordEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                      MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
} 