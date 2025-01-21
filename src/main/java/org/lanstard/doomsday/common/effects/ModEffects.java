package org.lanstard.doomsday.common.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.lanstard.doomsday.Doomsday;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = 
        DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Doomsday.MODID);
        
    public static final RegistryObject<MobEffect> LEFT_EYE_BLIND = MOB_EFFECTS.register(
        "left_eye_blind", LeftEyeBlindEffect::new);
        
    public static final RegistryObject<MobEffect> RIGHT_EYE_BLIND = MOB_EFFECTS.register(
        "right_eye_blind", RightEyeBlindEffect::new);
        
    public static final RegistryObject<MobEffect> FULL_BLIND = MOB_EFFECTS.register(
        "full_blind", FullBlindEffect::new);
        
    public static final RegistryObject<MobEffect> FLASH = MOB_EFFECTS.register(
        "flash", FlashEffect::new);
        
    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
} 