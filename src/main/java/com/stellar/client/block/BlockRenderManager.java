package com.stellar.client.block;

import com.stellar.register.BlockRegistry;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class BlockRenderManager {

    public BlockRenderManager(){
        setupBlockRenderer();
    }

    public static void setupBlockRenderer(){
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.IRON_CATWALK, RenderLayer.getTranslucent());
    }
}
