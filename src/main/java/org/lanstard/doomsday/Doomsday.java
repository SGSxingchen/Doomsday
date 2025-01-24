package org.lanstard.doomsday;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.IEventBus;
import com.mojang.logging.LogUtils;
import org.lanstard.doomsday.common.items.ModItem;
import org.slf4j.Logger;
import org.lanstard.doomsday.network.NetworkManager;
import net.minecraftforge.common.MinecraftForge;
import org.lanstard.doomsday.common.items.CreativeTabRegister;
import org.lanstard.doomsday.common.recipe.ModRecipes;
import org.lanstard.doomsday.common.blocks.ModBlocks;
import org.lanstard.doomsday.common.blocks.entity.ModBlockEntities;
import org.lanstard.doomsday.common.effects.ModEffects;
import org.lanstard.doomsday.common.entities.ModEntities;
import org.lanstard.doomsday.common.items.ModSpawnEggs;
import org.lanstard.doomsday.config.DoomsdayConfig;

@Mod(Doomsday.MODID)
public class Doomsday {
    public static final String MODID = "doomsday";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Doomsday() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册配置
        DoomsdayConfig.register();
        
        // 注册网络消息
        NetworkManager.register();

        // 注册方块
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        
        // 注册实体
        ModEntities.ENTITY_TYPES.register(modEventBus);
        
        // 注册物品和创造模式物品栏
        ModItem.register(modEventBus);

        // 注册刷怪蛋（必须在实体之后）
        ModSpawnEggs.register(modEventBus);

        CreativeTabRegister.register(modEventBus);


        // 注册事件监听器
        MinecraftForge.EVENT_BUS.register(this);
        
        // 注册配方
        ModRecipes.RECIPE_TYPES.register(modEventBus);
        ModRecipes.SERIALIZERS.register(modEventBus);
        
        // 注册效果
        ModEffects.register(modEventBus);
    }
}