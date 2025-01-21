package org.lanstard.doomsday;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.IEventBus;
import com.mojang.logging.LogUtils;
import org.lanstard.doomsday.common.entity.EntityRegister;
import org.lanstard.doomsday.common.items.ItemRegister;
import org.slf4j.Logger;

import org.lanstard.doomsday.network.NetworkManager;

import net.minecraftforge.common.MinecraftForge;

import org.lanstard.doomsday.common.items.CreativeTabRegister;
import org.lanstard.doomsday.common.recipe.ModRecipes;
import org.lanstard.doomsday.common.blocks.ModBlocks;
import org.lanstard.doomsday.common.blocks.entity.ModBlockEntities;
import org.lanstard.doomsday.common.effects.ModEffects;
import org.lanstard.doomsday.common.entities.ModEntities;


@Mod(Doomsday.MODID)
public class Doomsday {
    public static final String MODID = "doomsday";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Doomsday() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册网络消息
        NetworkManager.register();
        
        // 注册实体和物品
        EntityRegister.register(modEventBus);
        ItemRegister.register(modEventBus);
        CreativeTabRegister.register(modEventBus);

        // 注册事件监听器
        MinecraftForge.EVENT_BUS.register(this);
        
        ModRecipes.RECIPE_TYPES.register(modEventBus);
        ModRecipes.SERIALIZERS.register(modEventBus);

        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        
        // 注册效果
        ModEffects.register(modEventBus);

        ModEntities.ENTITY_TYPES.register(modEventBus);
    }
}