package org.lanstard.doomsday.common.entities;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;

@Mod.EventBusSubscriber(modid = Doomsday.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntitySetup {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SHENJUN.get(), ShenJunEntity.createAttributes().build());
        event.put(ModEntities.LOUYI.get(), LouyiEntity.createAttributes().build());
        event.put(ModEntities.PUPPET.get(), PuppetEntity.createAttributes().build());
        event.put(ModEntities.MUA.get(), MuaEntity.createAttributes().build());
        event.put(ModEntities.QIHEI_SWORD.get(), QiheiSwordEntity.createAttributes().build());
    }
}