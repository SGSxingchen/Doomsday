package org.lanstard.doomsday.client.renderer.entities;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.client.model.MuaModel;
import org.lanstard.doomsday.common.entities.MuaEntity;
import org.lanstard.doomsday.common.entities.PuppetEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MuaRenderer extends GeoEntityRenderer<MuaEntity> {
    public MuaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MuaModel());
        this.shadowRadius = 0.3f;
    }
    
    @Override
    public void render(MuaEntity entity, float entityYaw, float partialTicks, PoseStack stack,
                      MultiBufferSource bufferIn, int packedLightIn) {
        stack.scale(1.0F, 1.0F, 1.0F);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
} 