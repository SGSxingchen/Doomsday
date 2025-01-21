package org.lanstard.doomsday.common.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.lanstard.doomsday.Doomsday;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Doomsday.MODID);
        
    public static final RegistryObject<EntityType<BombsEntity>> BOMBS = ENTITY_TYPES.register("bombs",
        () -> EntityType.Builder.<BombsEntity>of(BombsEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(64)
            .build("bombs"));
} 