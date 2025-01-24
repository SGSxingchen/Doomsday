package org.lanstard.doomsday.client.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.entities.PuppetEntity;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class PuppetModel extends GeoModel<PuppetEntity> {
    @Override
    public ResourceLocation getModelResource(PuppetEntity object) {
        return new ResourceLocation(Doomsday.MODID, "geo/entity/puppet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PuppetEntity object) {
        return new ResourceLocation(Doomsday.MODID, "textures/entity/puppet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PuppetEntity animatable) {
        return new ResourceLocation(Doomsday.MODID, "animations/entity/puppet.animation.json");
    }

    @Override
    public void setCustomAnimations(PuppetEntity entity, long instanceId, AnimationState<PuppetEntity> animationState) {
        CoreGeoBone head = this.getAnimationProcessor().getBone("Head");
        
        if (head != null) {
            // 获取实体的头部旋转角度并反转
            float headPitch = -entity.getXRot();  // 反转俯仰角
            float netHeadYaw = -(entity.yHeadRot - entity.yBodyRot);  // 反转偏航角
            
            // 将角度转换为弧度并应用到头部骨骼
            head.setRotX(headPitch * Mth.DEG_TO_RAD);
            head.setRotY(netHeadYaw * Mth.DEG_TO_RAD);
        }
    }
} 