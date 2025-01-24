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

    public static final RegistryObject<EntityType<ShenJunEntity>> SHENJUN = 
        ENTITY_TYPES.register("shenjun",
            () -> EntityType.Builder.of(ShenJunEntity::new, MobCategory.MONSTER)
                .sized(1.2f, 2.4f) // 更新碰撞箱大小以匹配实体大小
                .build("shenjun"));

    public static final RegistryObject<EntityType<PuppetEntity>> PUPPET = 
        ENTITY_TYPES.register("puppet",
            () -> EntityType.Builder.of(PuppetEntity::new, MobCategory.MONSTER)
                .sized(0.6f, 1.8f)
                .build("puppet"));

    public static final RegistryObject<EntityType<MuaEntity>> MUA = 
        ENTITY_TYPES.register("mua",
            () -> EntityType.Builder.of(MuaEntity::new, MobCategory.MONSTER)
                .sized(0.6f, 1.8f)
                .build("mua"));

    public static final RegistryObject<EntityType<LouyiEntity>> LOUYI = 
        ENTITY_TYPES.register("louyi",
            () -> EntityType.Builder.of(LouyiEntity::new, MobCategory.MONSTER)
                .sized(0.6f, 1.2f)  // 设置合适的碰撞箱大小
                .build("louyi"));

    public static final RegistryObject<EntityType<FireBombEntity>> FIRE_BOMB = 
        ENTITY_TYPES.register("fire_bomb",
            () -> EntityType.Builder.<FireBombEntity>of(FireBombEntity::new, MobCategory.MISC)
                .sized(0.25F, 0.25F)
                .clientTrackingRange(64)
                .build("fire_bomb"));

    public static final RegistryObject<EntityType<IceBlockEntity>> ICE_BLOCK = 
        ENTITY_TYPES.register("ice_block",
            () -> EntityType.Builder.<IceBlockEntity>of(IceBlockEntity::new, MobCategory.MISC)
                .sized(0.5F, 0.5F)
                .clientTrackingRange(64)
                .build("ice_block"));
} 