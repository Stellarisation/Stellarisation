package com.stellar.client.block;

import com.stellar.blockentity.CreativeTankBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class CreativeTankBlockEntityRenderer<T extends CreativeTankBlockEntity> implements BlockEntityRenderer<T> {

    public CreativeTankBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(CreativeTankBlockEntity blockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
        //TODO: Render Fluid in Tank
    }
}
