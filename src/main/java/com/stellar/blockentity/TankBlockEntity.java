package com.stellar.blockentity;

import com.stellar.block.TankBlock;
import com.stellar.register.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static java.lang.Math.abs;

public class TankBlockEntity extends BlockEntity implements BlockEntityTicker, IMultiEntityContainer {

    private static final int MAX_HEIGHT = 5;
    private static final int MAX_WIDTH = 3;
    public static int capacityMultiplier = 1000;
    protected BlockPos controller;
    protected boolean window;
    public boolean updateConnectivity;
    public int width;
    public int height;

    public TankBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.TANK_BLOCK_ENTITY, pos, state);
        updateConnectivity = false;
        window = true;
        height = 1;
        width = 1;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        if (updateConnectivity)
            nbt.putBoolean("Uninitialized", true);
        if (!isController())
            nbt.put("Controller", NbtHelper.fromBlockPos(controller));
        if (isController()) {
            nbt.putBoolean("Window", window);
            nbt.putInt("Width", width);
            nbt.putInt("Height", height);
        }
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        BlockPos controllerBefore = controller;
        int prevWidth = width;
        int prevHeight = height;

        updateConnectivity = nbt.contains("Uninitialized");
        controller = null;

        if (nbt.contains("Controller"))
            controller = NbtHelper.toBlockPos(nbt.getCompound("Controller"));

        if (isController()) {
            window = nbt.getBoolean("Window");
            width = nbt.getInt("Width");
            height = nbt.getInt("Height");
        }

        boolean changeOfController = controllerBefore == null ? controller != null : !controllerBefore.equals(controller);
        if (changeOfController || prevWidth != width || prevHeight != height) {
            if (hasWorld())
                world.updateListeners(getPos(), getCachedState(), getCachedState(), 16);
        }
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (updateConnectivity) updateConnectivity();
    }

    public int getTotalTankSize() {
        return width * width * height;
    }

    public void updateConnectivity() {
        updateConnectivity = false;
        if (world.isClient())
            return;
        if (!isController())
            return;
        TankConnectivityHandler.formTanks(this);
    }

    @Override
    public BlockPos getController() {
        return isController() ? pos : controller;
    }

    public TankBlockEntity getControllerEntity() {
        if (isController())
            return this;
        BlockEntity blockEntity = world.getBlockEntity(controller);
        if (blockEntity instanceof TankBlockEntity)
            return (TankBlockEntity) blockEntity;
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

    public static int getMaxWidth() {
        return MAX_WIDTH;
    }

    public static int getMaxHeight() {
        return MAX_HEIGHT;
    }

    public void removeController() {
        if (world.isClient) return;

        updateConnectivity = true;
        controller = null;
        width = 1;
        height = 1;

        BlockState state = getCachedState();
        if (TankBlock.isTank(state)) {
            state = state.with(TankBlock.BOTTOM, true);
            state = state.with(TankBlock.TOP, true);
            state = state.with(TankBlock.WINDOW, true);
            getWorld().setBlockState(pos, state, 22);
        }
        markDirty();
    }

    public void setWindows(boolean window) {
        this.window = window;
        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {

                    BlockPos pos = this.pos.add(xOffset, yOffset, zOffset);
                    BlockState blockState = world.getBlockState(pos);
                    if (!TankBlock.isTank(blockState))
                        continue;

                    boolean shape = false;
                    if (window) {
                        // SIZE 1: Every tank has a window
                        if (width == 1)
                            shape = true;
                        // SIZE 2: Every tank has a corner window
                        if (width == 2)
                            shape = xOffset == 0 ? zOffset == 0 ? true : true
                                    : zOffset == 0 ? true : true;
                        // SIZE 3: Tanks in the center have a window
                        if (width == 3 && abs(abs(xOffset) - abs(zOffset)) == 1)
                            shape = true;
                    }

                    world.setBlockState(pos, blockState.with(TankBlock.WINDOW, shape), 22);
                }
            }
        }
    }

    public void toggleWindows() {
        TankBlockEntity tankBlockEntity = getControllerEntity();
        if (tankBlockEntity == null)
            return;
        tankBlockEntity.setWindows(!tankBlockEntity.window);
    }
}
