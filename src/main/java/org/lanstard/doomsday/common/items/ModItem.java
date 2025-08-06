package org.lanstard.doomsday.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.blocks.ModBlocks;
import org.lanstard.doomsday.common.items.combat.explosives.BombsItem;
import org.lanstard.doomsday.common.items.combat.explosives.FireBombItem;
import org.lanstard.doomsday.common.items.echo.EchoBallItem;
import org.lanstard.doomsday.common.items.echo.EyeItem;
import org.lanstard.doomsday.common.items.echo.MoldyEyeItem;
import org.lanstard.doomsday.common.items.echo.HeartLockItem;
import org.lanstard.doomsday.common.items.equipment.curios.SoulClockItem;
import org.lanstard.doomsday.common.items.equipment.curios.SoulCompassItem;
import org.lanstard.doomsday.common.items.equipment.armor.ShenJunArmorItem;
import org.lanstard.doomsday.common.items.medical.BeliefPointsItem;
import org.lanstard.doomsday.common.items.medical.MedkitItem;
import org.lanstard.doomsday.common.items.tools.ChiselItem;
import org.lanstard.doomsday.common.items.tools.DaoItem;
import org.lanstard.doomsday.common.items.tools.WrenchItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import java.util.List;
import javax.annotation.Nullable;

public class ModItem {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Doomsday.MODID);
    
    public static final RegistryObject<Item> SOUL_COMPASS = ITEMS.register("soul_compass",
        () -> new SoulCompassItem());

    public static final RegistryObject<Item> SOUL_CLOCK = ITEMS.register("soul_clock",
        () -> new SoulClockItem());

    public static final RegistryObject<Item> CREATIVE_TAB_ICON = ITEMS.register("creative_tab_icon",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ECHO_BALL = ITEMS.register("echo_ball",
        () -> new EchoBallItem(new Item.Properties()));

    public static final RegistryObject<Item> DAO = ITEMS.register("dao",
        () -> new DaoItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> EYE = ITEMS.register("eye",
        () -> new EyeItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> MOLDY_EYE = ITEMS.register("moldy_eye",
        () -> new MoldyEyeItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> CHISEL = ITEMS.register("chisel",
        () -> new ChiselItem(new Item.Properties().durability(1)));

    public static final RegistryObject<Item> WRENCH = ITEMS.register("wrench",
        () -> new WrenchItem(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> BELIEF_POINTS = ITEMS.register("belief_points",
        () -> new BeliefPointsItem(new Item.Properties()));

    public static final RegistryObject<Item> BOMBS = ITEMS.register("bombs",
        () -> new BombsItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> FIRE_BOMB = ITEMS.register("fire_bomb",
        () -> new FireBombItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> AFTERGLOW_LAMP = ITEMS.register("afterglow_lamp",
        () -> new BlockItem(ModBlocks.AFTERGLOW_LAMP.get(), new Item.Properties()));

    public static final RegistryObject<Item> FIELD_BLOCK = ITEMS.register("field_block",
        () -> new BlockItem(ModBlocks.FIELD_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> ECHO_CLOCK = ITEMS.register("echo_clock",
        () -> new BlockItem(ModBlocks.ECHO_CLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> STAR_TICKET = ITEMS.register("star_ticket", () ->
        new Item(new Item.Properties().rarity(Rarity.UNCOMMON)) {
            @Override
            public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                tooltip.add(Component.translatable("item.doomsday.star_ticket.tooltip").withStyle(ChatFormatting.GRAY));
            }
        });

    public static final RegistryObject<Item> SHENJUN_HELMET = ITEMS.register("shenjun_helmet",
            () -> new ShenJunArmorItem(ArmorItem.Type.HELMET, new Item.Properties()));
    
    public static final RegistryObject<Item> SHENJUN_CHESTPLATE = ITEMS.register("shenjun_chestplate",
            () -> new ShenJunArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    
    public static final RegistryObject<Item> SHENJUN_LEGGINGS = ITEMS.register("shenjun_leggings",
            () -> new ShenJunArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties()));
    
    public static final RegistryObject<Item> SHENJUN_BOOTS = ITEMS.register("shenjun_boots",
            () -> new ShenJunArmorItem(ArmorItem.Type.BOOTS, new Item.Properties()));

    public static final RegistryObject<Item> MEDKIT = ITEMS.register("medkit",
            () -> new MedkitItem());

    public static final RegistryObject<Item> WHITE_COAT = ITEMS.register("white_coat",
        () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)) {
            @Override
            public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                tooltip.add(Component.translatable("item.doomsday.white_coat.tooltip").withStyle(ChatFormatting.GRAY));
            }
        });

    public static final RegistryObject<Item> GROWTH_SEED = ITEMS.register("growth_seed",
        () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)) {
            @Override
            public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                tooltip.add(Component.translatable("item.doomsday.growth_seed.tooltip").withStyle(ChatFormatting.GRAY));
            }
        });

    public static final RegistryObject<Item> HEART_LOCK = ITEMS.register("heart_lock",
        () -> new HeartLockItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
} 