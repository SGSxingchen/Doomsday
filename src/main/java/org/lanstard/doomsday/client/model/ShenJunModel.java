package org.lanstard.doomsday.client.model;

import net.minecraft.resources.ResourceLocation;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.entities.ShenJunEntity;
import software.bernie.geckolib.model.GeoModel;

public class ShenJunModel extends GeoModel<ShenJunEntity> {
    @Override
    public ResourceLocation getModelResource(ShenJunEntity object) {
        return new ResourceLocation(Doomsday.MODID, "geo/entity/shenjun.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ShenJunEntity object) {
        return new ResourceLocation(Doomsday.MODID, "geo/entity/shenjun.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ShenJunEntity animatable) {
        return new ResourceLocation(Doomsday.MODID, "geo/entity/shenjun.animation.json");
    }
} 