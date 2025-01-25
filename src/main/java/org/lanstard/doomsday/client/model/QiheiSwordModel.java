package org.lanstard.doomsday.client.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.entities.QiheiSwordEntity;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.model.GeoModel;

public class QiheiSwordModel extends GeoModel<QiheiSwordEntity> {
    // 动画状态枚举
    public enum AnimationState {
        IDLE_FLOAT,      // 静态漂浮
        CHASE_FLOAT      // 追击漂浮
    }

    @Override
    public ResourceLocation getModelResource(QiheiSwordEntity object) {
        return new ResourceLocation(Doomsday.MODID, "geo/entity/qihei_sword.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(QiheiSwordEntity object) {
        return new ResourceLocation(Doomsday.MODID, "textures/entity/qihei_sword.png");
    }

    @Override
    public ResourceLocation getAnimationResource(QiheiSwordEntity animatable) {
        return new ResourceLocation(Doomsday.MODID, "animations/entity/qihei_sword.animation.json");
    }

    @Override
    public void setCustomAnimations(QiheiSwordEntity entity, long instanceId, software.bernie.geckolib.core.animation.AnimationState<QiheiSwordEntity> animationState) {
        CoreGeoBone root = this.getAnimationProcessor().getBone("root");
        
        if (root != null) {
            // 获取当前游戏时间
            float time = (entity.level().getGameTime() + animationState.getPartialTick()) / 20.0F;
            
            // 根据实体的动画状态执行不同的动画
            if (entity.getAnimationState() == AnimationState.CHASE_FLOAT) {
                applyChaseFloatAnimation(root, time, entity);
            } else {
                applyIdleFloatAnimation(root, time);
            }

            
            float headPitch = -entity.getXRot();  // 反转俯仰角
            float netHeadYaw = -entity.getYRot();  // 反转偏航角
            
            // 将角度转换为弧度并应用到头部骨骼
            root.setRotX(headPitch * Mth.DEG_TO_RAD);
            root.setRotY(netHeadYaw * Mth.DEG_TO_RAD);
        }
    }

    private void applyIdleFloatAnimation(CoreGeoBone root, float time) {
        // 静态漂浮动画 - 轻微上下浮动
        float t = (Mth.sin(time * Mth.PI * 0.5f) + 1) / 2;
        float y = bezierY(t) * 0.5f;
        root.updatePosition(0, y, 0);
        

    }

    private void applyChaseFloatAnimation(CoreGeoBone root, float time, QiheiSwordEntity entity) {
        // 追击时的漂浮动画
        float t = (Mth.sin(time * Mth.PI) + 1) / 2;
        float y = bezierY(t) * 0.15f;
        root.updatePosition(0, y, 0);
    }

    /**
     * 使用三次贝塞尔曲线计算Y轴位移
     * 控制点设置为：
     * P0(0, -0.5)
     * P1(0.25, -0.5)
     * P2(0.75, 0.5)
     * P3(1, 0.5)
     */
    private float bezierY(float t) {
        float oneMinusT = 1 - t;
        float oneMinusT2 = oneMinusT * oneMinusT;
        float oneMinusT3 = oneMinusT2 * oneMinusT;
        float t2 = t * t;
        float t3 = t2 * t;

        return oneMinusT3 * (-0.5f) +
               3 * oneMinusT2 * t * (-0.5f) +
               3 * oneMinusT * t2 * 0.5f +
               t3 * 0.5f;
    }
}