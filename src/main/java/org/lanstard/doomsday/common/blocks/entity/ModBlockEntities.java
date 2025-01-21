package org.lanstard.doomsday.common.blocks.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.blocks.ModBlocks;
import org.lanstard.doomsday.common.blocks.PreservationTableBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Doomsday.MODID);

    public static final RegistryObject<BlockEntityType<PreservationTableBlockEntity>> PRESERVATION_TABLE =
        BLOCK_ENTITIES.register("preservation_table",
            () -> BlockEntityType.Builder.of(PreservationTableBlockEntity::new,
                ModBlocks.PRESERVATION_TABLE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
} 