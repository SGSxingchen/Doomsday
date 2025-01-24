package org.lanstard.doomsday.common.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.common.ForgeSpawnEggItem;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.entities.ModEntities;

public class ModSpawnEggs {
    public static final DeferredRegister<Item> SPAWN_EGGS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, Doomsday.MODID);

    public static final RegistryObject<Item> SHENJUN_SPAWN_EGG = SPAWN_EGGS.register("shenjun_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.SHENJUN,
                    0x4B0082, // 主要颜色（深紫色）
                    0x800080, // 副颜色（紫色）
                    new Item.Properties()));

    public static void register(IEventBus eventBus) {
        SPAWN_EGGS.register(eventBus);
    }
} 