package org.lanstard.doomsday.common.fluids;

import net.minecraftforge.fluids.FluidType;

public class PreservativeFluidType extends FluidType {
    public PreservativeFluidType() {
        super(FluidType.Properties.create()
            .density(1000)
            .viscosity(1000)
            .temperature(300)
            .lightLevel(2));
    }
} 