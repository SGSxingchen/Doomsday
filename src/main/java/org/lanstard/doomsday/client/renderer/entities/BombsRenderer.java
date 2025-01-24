package org.lanstard.doomsday.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.entities.BombsEntity;
import org.lanstard.doomsday.common.items.ModItem;

public class BombsRenderer extends EntityRenderer<BombsEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Doomsday.MODID, "textures/item/bombs.png");
    private final ItemRenderer itemRenderer;

    public BombsRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(BombsEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        
        // 移动到实体位置
        poseStack.translate(0.0D, 0.15D, 0.0D);
        
        // 使物品朝向相机
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        
        // 调整渲染位置和大小
        poseStack.scale(0.5f, 0.5f, 0.5f);
        
        // 渲染物品
        ItemStack itemStack = new ItemStack(ModItem.BOMBS.get());
        itemRenderer.renderStatic(itemStack, ItemDisplayContext.GROUND, 
                packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
                
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BombsEntity entity) {
        return TEXTURE;
    }
} 