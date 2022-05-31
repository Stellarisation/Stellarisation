package com.stellar.blockentity;

import com.stellar.register.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BeltBlockEntity extends BlockEntity implements BlockEntityTicker, IMultiEntityContainer {

    protected BlockPos controller;
    protected boolean updateConnectivity;

    public BeltBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.BELT_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        if (updateConnectivity)
            nbt.putBoolean("Uninitialized", true);
        if (!isController())
            nbt.put("Controller", NbtHelper.fromBlockPos(controller));
        if (isController()) {
        }
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        BlockPos controllerBefore = controller;

        updateConnectivity = nbt.contains("Uninitialized");
        controller = null;

        if (nbt.contains("Controller"))
            controller = NbtHelper.toBlockPos(nbt.getCompound("Controller"));

        if (isController()) {
        }

        boolean changeOfController = controllerBefore == null ? controller != null : !controllerBefore.equals(controller);
        if (changeOfController) {
            if (hasWorld())
                world.updateListeners(getPos(), getCachedState(), getCachedState(), 16);
        }
    }

    @Override
    public BlockPos getController() {
        return isController() ? pos : controller;
    }

    public BeltBlockEntity getControllerEntity() {
        if (isController())
            return this;
        BlockEntity blockEntity = world.getBlockEntity(controller);
        if (blockEntity instanceof BeltBlockEntity)
            return (BeltBlockEntity) blockEntity;
        return null;
    }

    @Override
    public boolean isController() {
        return controller == null || pos.getX() == controller.getX() && pos.getY() == controller.getY() && pos.getZ() == controller.getZ();
    }

    @Override
    public void setController(BlockPos controller) {
        if (world.isClient)
            return;
        if (controller.equals(this.controller))
            return;
        this.controller = controller;
        markDirty();
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {

    }
}
