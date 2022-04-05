package com.stellar;

import com.stellar.client.block.BlockRenderManager;
import com.stellar.client.fluid.FluidRenderManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class StellarClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        new FluidRenderManager();
        new BlockRenderManager();
    }
}
