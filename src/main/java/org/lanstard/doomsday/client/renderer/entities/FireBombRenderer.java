package org.lanstard.doomsday.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.lanstard.doomsday.common.entities.FireBombEntity;

public class FireBombRenderer extends EntityRenderer<FireBombEntity> {
    private final ItemRenderer itemRenderer;
    private static final float SCALE = 0.5f;

    public FireBombRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(FireBombEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, 
                      MultiBufferSource buffer, int packedLight) {
        if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25D)) {
            poseStack.pushPose();
            
            // 缩放
            poseStack.scale(SCALE, SCALE, SCALE);
            
            // 旋转
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

            // 渲染物品模型
            this.itemRenderer.renderStatic(entity.getItem(), ItemDisplayContext.GROUND,
                    packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
                    
            poseStack.popPose();
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(FireBombEntity entity) {
        return new ResourceLocation("doomsday:textures/item/fire_bomb.png");
    }
} 