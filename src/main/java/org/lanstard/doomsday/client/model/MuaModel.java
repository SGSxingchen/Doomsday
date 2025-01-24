package org.lanstard.doomsday.client.model;

import net.minecraft.resources.ResourceLocation;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.entities.MuaEntity;
import software.bernie.geckolib.model.GeoModel;

public class MuaModel extends GeoModel<MuaEntity> {
    @Override
    public ResourceLocation getModelResource(MuaEntity object) {
        return new ResourceLocation(Doomsday.MODID, "geo/entity/mua.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MuaEntity object) {
        return new ResourceLocation(Doomsday.MODID, "textures/entity/mua.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MuaEntity animatable) {
        return new ResourceLocation(Doomsday.MODID, "animations/entity/mua.animation.json");
    }
} 