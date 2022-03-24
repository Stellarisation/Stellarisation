package com.stellar.register;

import com.stellar.fluid.BiomassFluid;
import net.minecraft.fluid.FlowableFluid;

public class FluidRegistry extends RegistryUtil {

    public static final FlowableFluid STILL_BIOMASS = (FlowableFluid)register(new BiomassFluid.Still(),"biomass");
    public static final FlowableFluid FLOWING_BIOMASS = (FlowableFluid)register(new BiomassFluid.Flowing(),"flowing_biomass");
}
