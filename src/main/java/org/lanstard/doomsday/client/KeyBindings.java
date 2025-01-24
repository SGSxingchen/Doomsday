package org.lanstard.doomsday.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;

@Mod.EventBusSubscriber(modid = Doomsday.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyBindings {
    // public static final KeyMapping OPEN_ECHO_SCREEN = new KeyMapping(
    //     "key.doomsday.open_echo_screen",
    //     InputConstants.KEY_R, // 默认R键
    //     "key.categories.doomsday"
    // );
    
    public static final KeyMapping USE_ECHO = new KeyMapping(
        "key.doomsday.use_echo",
        InputConstants.KEY_V,  // 默认V键
        "key.categories.doomsday"
    );
    
    public static final KeyMapping TOGGLE_CONTINUOUS_ECHO = new KeyMapping(
        "key.doomsday.toggle_continuous_echo",
        InputConstants.KEY_G,  // 默认G键
        "key.categories.doomsday"
    );
    
    public static final KeyMapping NEXT_ECHO = new KeyMapping(
        "key.doomsday.next_echo",
        InputConstants.KEY_RBRACKET,  // 默认]键
        "key.categories.doomsday"
    );
    
    public static final KeyMapping PREVIOUS_ECHO = new KeyMapping(
        "key.doomsday.previous_echo",
        InputConstants.KEY_LBRACKET,  // 默认[键
        "key.categories.doomsday"
    );
    
    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        // event.register(OPEN_ECHO_SCREEN);
        event.register(USE_ECHO);
        event.register(TOGGLE_CONTINUOUS_ECHO);
        event.register(NEXT_ECHO);
        event.register(PREVIOUS_ECHO);
    }
} 