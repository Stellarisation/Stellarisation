package com.stellar.client.fluid;

import com.stellar.register.FluidRegistry;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class FluidRenderManager {

    public FluidRenderManager(){
        setupFluidRenderer();
    }

    public static void setupFluidRenderer(){
        /*ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            registry.register(new Identifier("stellar:fluid/fluid_still"));
            registry.register(new Identifier("stellar:fluid/fluid_flow"));
        });

        FluidRenderHandlerRegistry.INSTANCE.register(FluidRegistry.STILL_FLUID, FluidRegistry.FLOWING_FLUID, new SimpleFluidRenderHandler(
                new Identifier("stellar:fluid/fluid_still"),
                new Identifier("stellar:fluid/fluid_flow"),
                0x4CC248
        ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), FluidRegistry.STILL_FLUID, FluidRegistry.FLOWING_FLUID);*/

        FluidRenderHandlerRegistry.INSTANCE.register(FluidRegistry.STILL_BIOMASS, FluidRegistry.FLOWING_BIOMASS, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0x4CC248
        ));

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), FluidRegistry.STILL_BIOMASS, FluidRegistry.FLOWING_BIOMASS);

    }
}
