package org.lanstard.doomsday.client.model;

import net.minecraft.resources.ResourceLocation;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.entities.PuppetEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class PuppetModel extends GeoModel<PuppetEntity> {
    @Override
    public ResourceLocation getModelResource(PuppetEntity object) {
        return new ResourceLocation("minecraft", "geo/entity/zombie/zombie.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PuppetEntity object) {
        return new ResourceLocation(Doomsday.MODID, "textures/entity/puppet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PuppetEntity animatable) {
        return new ResourceLocation("minecraft", "animations/zombie.animation.json");
    }
} 