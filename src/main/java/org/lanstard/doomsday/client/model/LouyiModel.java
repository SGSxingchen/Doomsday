package org.lanstard.doomsday.client.model;

import net.minecraft.resources.ResourceLocation;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.entities.LouyiEntity;
import software.bernie.geckolib.model.GeoModel;

public class LouyiModel extends GeoModel<LouyiEntity> {
    @Override
    public ResourceLocation getModelResource(LouyiEntity object) {
        return new ResourceLocation(Doomsday.MODID, "geo/entity/louyi.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LouyiEntity object) {
        return new ResourceLocation(Doomsday.MODID, "textures/entity/louyi.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LouyiEntity animatable) {
        return new ResourceLocation(Doomsday.MODID, "animations/entity/louyi.animation.json");
    }
} 