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
                    .icon(() -> new ItemStack(ItemRegister.CREATIVE_TAB_ICON.get()))
                    .title(Component.translatable("itemGroup.doomsday"))
                    .displayItems((parameters, output) -> {
                        // 工具
                        output.accept(ItemRegister.SOUL_COMPASS.get());
                        output.accept(ItemRegister.SOUL_CLOCK.get());
                        output.accept(ItemRegister.CHISEL.get());
                        
                        // 方块
                        output.accept(ItemRegister.AFTERGLOW_LAMP.get());
                        output.accept(ItemRegister.FIELD_BLOCK.get());
                        
                        // 回响相关物品
                        output.accept(ItemRegister.ECHO_BALL.get());
                        
                        // 其他物品
                        output.accept(ItemRegister.DAO.get());
                        output.accept(ItemRegister.EYE.get());
                        output.accept(ItemRegister.MOLDY_EYE.get());
                        output.accept(ItemRegister.BELIEF_POINTS.get());
                        output.accept(ItemRegister.BOMBS.get());
                        
                        // 添加神君盔甲
                        output.accept(ItemRegister.SHENJUN_HELMET.get());
                        output.accept(ItemRegister.SHENJUN_CHESTPLATE.get());
                        output.accept(ItemRegister.SHENJUN_LEGGINGS.get());
                        output.accept(ItemRegister.SHENJUN_BOOTS.get());
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
} 