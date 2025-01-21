package org.lanstard.doomsday.common.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.lanstard.doomsday.Doomsday;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = 
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Doomsday.MODID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = 
        DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Doomsday.MODID);

    public static final RegistryObject<RecipeType<PreservationRecipe>> PRESERVATION = 
        RECIPE_TYPES.register("preservation", () -> new RecipeType<PreservationRecipe>() {
            @Override
            public String toString() { return "preservation"; }
        });

    public static final RegistryObject<RecipeSerializer<PreservationRecipe>> PRESERVATION_SERIALIZER = 
        SERIALIZERS.register("preservation", () -> PreservationRecipe.SERIALIZER);
} 