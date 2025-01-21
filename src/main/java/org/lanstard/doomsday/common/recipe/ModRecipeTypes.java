package org.lanstard.doomsday.common.recipe;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.lanstard.doomsday.Doomsday;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = 
        DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Doomsday.MODID);

    public static final RegistryObject<RecipeType<PreservationRecipe>> PRESERVATION = 
        RECIPE_TYPES.register("preservation", () -> new RecipeType<PreservationRecipe>() {
            @Override
            public String toString() { return "preservation"; }
        });
} 