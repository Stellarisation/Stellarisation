package com.stellar.client.block;

import com.stellar.blockentity.CreativeCrateBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

public class CreativeCrateBlockEntityRenderer<T extends CreativeCrateBlockEntity> implements BlockEntityRenderer<T> {

    public CreativeCrateBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(CreativeCrateBlockEntity blockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
        int k = (int)blockEntity.getPos().asLong();
        ItemStack itemStack = blockEntity.creative_item;
        if (itemStack != ItemStack.EMPTY) {
            matrixStack.push();
            matrixStack.translate(0.5, (1.0 / 16.0) * 14.0, 0.5);
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
            matrixStack.scale(0.375F, 0.375F, 0.375F);
            MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformation.Mode.FIXED, light, overlay, matrixStack, vertexConsumerProvider, k);
            matrixStack.pop();
        }
    }
}
