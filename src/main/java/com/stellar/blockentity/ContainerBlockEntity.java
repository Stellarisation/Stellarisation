package com.stellar.blockentity;

import com.stellar.register.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ContainerBlockEntity extends BlockEntity implements BlockEntityTicker, IMultiEntityContainer {

    private static final int MAX_RADIUS = 3;
    protected BlockPos controller;
    protected boolean updateConnectivity;
    public int radius;
    public int length;
    public Direction.Axis axis;

    public ContainerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.CONTAINER_BLOCK_ENTITY, pos, state);
        radius = 1;
        length = 1;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        if (updateConnectivity)
            nbt.putBoolean("Uninitialized", true);
        if (!isController())
            nbt.put("Controller", NbtHelper.fromBlockPos(controller));
        if (isController()) {
            nbt.putInt("Radius", radius);
            nbt.putInt("Length", length);
        }
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        BlockPos controllerBefore = controller;
        int prevRadius = radius;
        int prevLength = length;

        updateConnectivity = nbt.contains("Uninitialized");
        controller = null;

        if (nbt.contains("Controller"))
            controller = NbtHelper.toBlockPos(nbt.getCompound("Controller"));

        if (isController()) {
            radius = nbt.getInt("Radius");
            length = nbt.getInt("Length");
        }

        boolean changeOfController = controllerBefore == null ? controller != null : !controllerBefore.equals(controller);
        if (changeOfController || prevRadius != radius || prevLength != length) {
            if (hasWorld())
                world.updateListeners(getPos(), getCachedState(), getCachedState(), 16);
        }
    }

    @Override
    public BlockPos getController() {
        return isController() ? pos : controller;
    }

    public ContainerBlockEntity getControllerEntity() {
        if (isController())
            return this;
        BlockEntity blockEntity = world.getBlockEntity(controller);
        if (blockEntity instanceof ContainerBlockEntity)
            return (ContainerBlockEntity) blockEntity;
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

    public void removeController() {
        if (world.isClient) return;

        updateConnectivity = true;
        controller = null;
        radius = 1;
        length = 1;

        markDirty();
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (updateConnectivity) updateConnectivity();
    }

    public int getTotalContainerSize() {
        return length * length * radius;
    }

    public static int getMaxLength(int radius) {
        return radius * 3;
    }

    public static int getMaxRadius() {
        return MAX_RADIUS;
    }

    public void updateConnectivity() {
        updateConnectivity = false;
        if (world.isClient())
            return;
        if (!isController())
            return;
        ContainerConnectivityHandler.formContainers(this);
    }
}
