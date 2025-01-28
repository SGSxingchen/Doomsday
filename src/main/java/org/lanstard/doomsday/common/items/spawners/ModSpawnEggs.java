package org.lanstard.doomsday.common.items.spawners;

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
                    0x2F4F4F, // 主要颜色（深灰色）
                    0xDCDCDC, // 副颜色（浅灰色）
                    new Item.Properties()));

    public static final RegistryObject<Item> PUPPET_SPAWN_EGG = SPAWN_EGGS.register("puppet_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.PUPPET,
                    0x696969, // 主要颜色（暗灰色）
                    0x808080, // 副颜色（灰色）
                    new Item.Properties()));

    public static final RegistryObject<Item> MUA_SPAWN_EGG = SPAWN_EGGS.register("mua_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MUA,
                    0x8B4513, // 主要颜色（棕色）
                    0xD2691E, // 副颜色（巧克力色）
                    new Item.Properties()));

    public static final RegistryObject<Item> LOUYI_SPAWN_EGG = SPAWN_EGGS.register("louyi_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.LOUYI, 0x4A4A4A, 0x2A2A2A,  // 深灰色和暗灰色
                    new Item.Properties()));

    public static final RegistryObject<Item> QIHEI_SWORD_SPAWN_EGG = SPAWN_EGGS.register("qihei_sword_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.QIHEI_SWORD, 0x2F2F2F, 0x000000,  // 深灰色和黑色
                    new Item.Properties()));

    public static void register(IEventBus eventBus) {
        SPAWN_EGGS.register(eventBus);
    }
} 