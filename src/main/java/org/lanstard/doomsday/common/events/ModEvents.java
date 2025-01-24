package org.lanstard.doomsday.common.events;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.common.entities.PuppetEntity;
import org.lanstard.doomsday.common.entities.MuaEntity;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide()) {
            if (event.getEntity() instanceof PuppetEntity puppet) {
                // 如果是傀儡实体，且在服务器端
                if (puppet.getOwner() == null) {
                    Player nearestPlayer = event.getLevel().getNearestPlayer(puppet, 5.0);
                    if (nearestPlayer != null) {
                        puppet.setOwner(nearestPlayer);
                    }
                }
            } else if (event.getEntity() instanceof MuaEntity mua) {
                // 如果是茂木造物，且在服务器端
                if (mua.getOwner() == null) {
                    Player nearestPlayer = event.getLevel().getNearestPlayer(mua, 5.0);
                    if (nearestPlayer != null) {
                        mua.setOwner(nearestPlayer);
                    }
                }
            }
        }
    }
} 