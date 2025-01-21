package org.lanstard.doomsday.common.items;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.blocks.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ArmorItem;

public class ItemRegister {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Doomsday.MODID);
    
    public static final RegistryObject<Item> SOUL_COMPASS = ITEMS.register("soul_compass",
        () -> new SoulCompassItem());

    public static final RegistryObject<Item> SOUL_CLOCK = ITEMS.register("soul_clock",
        () -> new SoulClockItem());

    public static final RegistryObject<Item> CREATIVE_TAB_ICON = ITEMS.register("creative_tab_icon",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ECHO_BALL = ITEMS.register("echo_ball",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> DAO = ITEMS.register("dao",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> EYE = ITEMS.register("eye",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MOLDY_EYE = ITEMS.register("moldy_eye",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> CHISEL = ITEMS.register("chisel",
        () -> new ChiselItem(new Item.Properties().durability(1)));

    public static final RegistryObject<Item> BELIEF_POINTS = ITEMS.register("belief_points",
        () -> new BeliefPointsItem(new Item.Properties()));

    public static final RegistryObject<Item> BOMBS = ITEMS.register("bombs",
        () -> new BombsItem(new Item.Properties()));

    public static final RegistryObject<Item> AFTERGLOW_LAMP = ITEMS.register("afterglow_lamp",
        () -> new BlockItem(ModBlocks.AFTERGLOW_LAMP.get(), new Item.Properties()));

    public static final RegistryObject<Item> FIELD_BLOCK = ITEMS.register("field_block",
        () -> new BlockItem(ModBlocks.FIELD_BLOCK.get(), new Item.Properties()));


    public static final RegistryObject<Item> SHENJUN_HELMET = ITEMS.register("shenjun_helmet",
            () -> new ShenJunArmorItem(ArmorItem.Type.HELMET, new Item.Properties()));
    
    public static final RegistryObject<Item> SHENJUN_CHESTPLATE = ITEMS.register("shenjun_chestplate",
            () -> new ShenJunArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    
    public static final RegistryObject<Item> SHENJUN_LEGGINGS = ITEMS.register("shenjun_leggings",
            () -> new ShenJunArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties()));
    
    public static final RegistryObject<Item> SHENJUN_BOOTS = ITEMS.register("shenjun_boots",
            () -> new ShenJunArmorItem(ArmorItem.Type.BOOTS, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
} 