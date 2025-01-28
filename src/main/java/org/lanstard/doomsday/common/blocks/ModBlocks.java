package org.lanstard.doomsday.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.lanstard.doomsday.Doomsday;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Doomsday.MODID);

    public static final RegistryObject<Block> PRESERVATION_TABLE = BLOCKS.register("preservation_table",
        () -> new PreservationTableBlock(Block.Properties.copy(net.minecraft.world.level.block.Blocks.STONE)
            .strength(3.5F)
            .requiresCorrectToolForDrops()
            .noOcclusion()));

    public static final RegistryObject<Block> AFTERGLOW_LAMP = BLOCKS.register("afterglow_lamp",
        () -> new AfterglowLampBlock(Block.Properties.copy(net.minecraft.world.level.block.Blocks.GLASS)
            .strength(0.3F)
            .lightLevel((state) -> 15)
            .sound(SoundType.GLASS)
            .noOcclusion()));

    public static final RegistryObject<Block> FIELD_BLOCK = BLOCKS.register("field_block",
        () -> new FieldBlock(Block.Properties.copy(net.minecraft.world.level.block.Blocks.BARRIER)
            .strength(-1.0F, 3600000.0F)
            .noLootTable()
            .noOcclusion()));

    public static final RegistryObject<Block> ECHO_CLOCK = BLOCKS.register("echo_clock",
        () -> new EchoClockBlock(Block.Properties.copy(net.minecraft.world.level.block.Blocks.BELL)
            .strength(5.0F)
            .requiresCorrectToolForDrops()
            .sound(SoundType.METAL)
            .noOcclusion()));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
} 