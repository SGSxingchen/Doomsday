package org.lanstard.doomsday.common.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.lanstard.doomsday.Doomsday;

public class CreativeTabRegister {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Doomsday.MODID);

    public static final RegistryObject<CreativeModeTab> DOOMSDAY_TAB = CREATIVE_MODE_TABS.register("doomsday_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItem.CREATIVE_TAB_ICON.get()))
                    .title(Component.translatable("itemGroup.doomsday"))
                    .displayItems((parameters, output) -> {
                        // 工具
                        output.accept(ModItem.SOUL_COMPASS.get());
                        output.accept(ModItem.SOUL_CLOCK.get());
                        output.accept(ModItem.CHISEL.get());
                        output.accept(ModItem.WRENCH.get());
                        
                        // 方块
                        output.accept(ModItem.AFTERGLOW_LAMP.get());
                        output.accept(ModItem.FIELD_BLOCK.get());
                        
                        // 回响相关物品
                        output.accept(ModItem.ECHO_BALL.get());
                        
                        // 其他物品
                        output.accept(ModItem.DAO.get());
                        output.accept(ModItem.EYE.get());
                        output.accept(ModItem.MOLDY_EYE.get());
                        output.accept(ModItem.BELIEF_POINTS.get());
                        output.accept(ModItem.BOMBS.get());
                        output.accept(ModItem.FIRE_BOMB.get());
                        output.accept(ModItem.MEDKIT.get());
                        output.accept(ModItem.WHITE_COAT.get());
                        output.accept(ModItem.GROWTH_SEED.get());
                        
                        // 添加神君盔甲
                        output.accept(ModItem.SHENJUN_HELMET.get());
                        output.accept(ModItem.SHENJUN_CHESTPLATE.get());
                        output.accept(ModItem.SHENJUN_LEGGINGS.get());
                        output.accept(ModItem.SHENJUN_BOOTS.get());
                        
                        // 添加生物刷怪蛋
                        output.accept(ModSpawnEggs.SHENJUN_SPAWN_EGG.get());
                        output.accept(ModSpawnEggs.PUPPET_SPAWN_EGG.get());
                        output.accept(ModSpawnEggs.MUA_SPAWN_EGG.get());
                        output.accept(ModSpawnEggs.LOUYI_SPAWN_EGG.get());
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
} 