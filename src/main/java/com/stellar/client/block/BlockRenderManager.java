package com.stellar.client.block;

import com.stellar.register.BlockEntityRegistry;
import com.stellar.register.BlockRegistry;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;

public class BlockRenderManager {

    public BlockRenderManager(){
        setupBlockRenderer();
    }

    public static void setupBlockRenderer(){
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.IRON_CATWALK, RenderLayer.getTranslucent());
        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityRegistry.CREATIVE_CRATE_BLOCK_ENTITY, CreativeCrateBlockEntityRenderer::new);
    }
}
