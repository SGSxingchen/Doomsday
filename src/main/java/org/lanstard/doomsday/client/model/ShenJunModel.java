package org.lanstard.doomsday.client.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.entities.ShenJunEntity;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class ShenJunModel extends GeoModel<ShenJunEntity> {
    @Override
    public ResourceLocation getModelResource(ShenJunEntity object) {
        return new ResourceLocation(Doomsday.MODID, "geo/entity/shenjun.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ShenJunEntity object) {
        return new ResourceLocation(Doomsday.MODID, "textures/entity/shenjun.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ShenJunEntity animatable) {
        return new ResourceLocation(Doomsday.MODID, "animations/entity/shenjun.animation.json");
    }

    @Override
    public void setCustomAnimations(ShenJunEntity entity, long instanceId, AnimationState<ShenJunEntity> animationState) {
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        
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