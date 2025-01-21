package org.lanstard.doomsday.common.fluids;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.lanstard.doomsday.Doomsday;

public class PreservativeFluid {
    public static final DeferredRegister<Fluid> FLUIDS =
        DeferredRegister.create(ForgeRegistries.FLUIDS, Doomsday.MODID);

    public static final ResourceLocation STILL_RL = new ResourceLocation(Doomsday.MODID, "block/preservative_still");
    public static final ResourceLocation FLOWING_RL = new ResourceLocation(Doomsday.MODID, "block/preservative_flow");

    public static final FluidType PRESERVATIVE_FLUID_TYPE = new PreservativeFluidType();
    
    private static ForgeFlowingFluid.Properties properties;
    private static RegistryObject<FlowingFluid> fluid;
    private static RegistryObject<FlowingFluid> flowingFluid;

    static {
        properties = new ForgeFlowingFluid.Properties(
            () -> PRESERVATIVE_FLUID_TYPE,
            () -> fluid.get(),
            () -> flowingFluid.get()
        );

        fluid = FLUIDS.register("preservative_fluid",
            () -> new ForgeFlowingFluid.Source(properties));

        flowingFluid = FLUIDS.register("preservative_flowing",
            () -> new ForgeFlowingFluid.Flowing(properties));
    }

    public static ForgeFlowingFluid.Properties getProperties() {
        return properties;
    }

    public static RegistryObject<FlowingFluid> getFluid() {
        return fluid;
    }

    public static RegistryObject<FlowingFluid> getFlowingFluid() {
        return flowingFluid;
    }
} 