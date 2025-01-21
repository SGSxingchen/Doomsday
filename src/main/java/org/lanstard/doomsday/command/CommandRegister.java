package org.lanstard.doomsday.command;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class CommandRegister {
    
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        // 注册回响状态命令
        EchoScreenCommand.register(event.getDispatcher());
        
        // 在这里注册其他命令...
    }
} 