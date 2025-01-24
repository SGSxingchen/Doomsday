package org.lanstard.doomsday.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.data.ModelData;
import org.lanstard.doomsday.common.entities.IceBlockEntity;

public class IceBlockRenderer extends EntityRenderer<IceBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public IceBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(IceBlockEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, 
                      MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        
        poseStack.pushPose();
        // 缩放为0.3倍大小
        poseStack.scale(0.3f, 0.3f, 0.3f);
        
        // 使用新的非过时方法渲染蓝冰方块
        blockRenderer.renderSingleBlock(
            Blocks.BLUE_ICE.defaultBlockState(),
            poseStack,
            buffer,
            packedLight,
            0,
            ModelData.EMPTY,
            null
        );
            
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(IceBlockEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
} 