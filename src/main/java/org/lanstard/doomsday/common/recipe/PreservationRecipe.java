package org.lanstard.doomsday.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.core.RegistryAccess;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.lanstard.doomsday.common.blocks.PreservationTableBlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.Container;

public class PreservationRecipe implements Recipe<Container> {
    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation id;
    private final ItemStack input;
    private final ItemStack output;
    private final Fluid fluid;
    private final int fluidAmount;
    private final int processTime;

    public PreservationRecipe(ResourceLocation id, ItemStack input, ItemStack output, 
                            Fluid fluid, int fluidAmount, int processTime) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.fluid = fluid;
        this.fluidAmount = fluidAmount;
        this.processTime = processTime;
    }

    @Override
    public boolean matches(Container container, Level level) {
        if (!(container instanceof PreservationTableBlockEntity table)) return false;
        
        ItemStack inputStack = table.getItem(0);
        FluidStack fluidStack = table.getFluidStack();
        
        return inputStack.getItem() == input.getItem() && 
               fluidStack.getFluid() == fluid &&
               fluidStack.getAmount() >= fluidAmount;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        if (container instanceof PreservationTableBlockEntity table) {
            return output.copy();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.PRESERVATION.get();
    }

    public void craft(PreservationTableBlockEntity table) {
        table.getItem(0).shrink(1);
        table.removeFluid(fluidAmount);
        table.addItem(output.copy(), null);
    }

    public int getProcessTime() {
        return processTime;
    }

    public int getFluidAmount() {
        return fluidAmount;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public static class Serializer implements RecipeSerializer<PreservationRecipe> {
        @Override
        public PreservationRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            ItemStack input = ShapedRecipe.itemStackFromJson(json.getAsJsonObject("input"));
            ItemStack output = ShapedRecipe.itemStackFromJson(json.getAsJsonObject("output"));
            ResourceLocation fluidId = new ResourceLocation(json.get("fluid").getAsString());
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidId);
            int fluidAmount = json.get("fluid_amount").getAsInt();
            int processTime = json.get("process_time").getAsInt();

            return new PreservationRecipe(recipeId, input, output, fluid, fluidAmount, processTime);
        }

        @Override
        public PreservationRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ItemStack input = buffer.readItem();
            ItemStack output = buffer.readItem();
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(buffer.readResourceLocation());
            int fluidAmount = buffer.readInt();
            int processTime = buffer.readInt();

            return new PreservationRecipe(recipeId, input, output, fluid, fluidAmount, processTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, PreservationRecipe recipe) {
            buffer.writeItem(recipe.input);
            buffer.writeItem(recipe.output);
            buffer.writeResourceLocation(ForgeRegistries.FLUIDS.getKey(recipe.fluid));
            buffer.writeInt(recipe.fluidAmount);
            buffer.writeInt(recipe.processTime);
        }
    }
} 