package com.stellar.item.blockitem;

import com.stellar.block.TankBlock;
import com.stellar.blockentity.TankBlockEntity;
import com.stellar.blockentity.TankConnectivityHandler;
import com.stellar.fluid.FluidStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TankBlockItem extends BlockItem {

    public TankBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult place(ItemPlacementContext context) {
        ActionResult initialResult = super.place(context);
        if (!initialResult.isAccepted())
            return initialResult;
        tryMultiPlace(context);
        return initialResult;
    }

    @Override
    protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        MinecraftServer minecraftserver = world.getServer();
        if (minecraftserver == null)
            return false;
        NbtCompound nbt = stack.getSubNbt("BlockEntityTag");
        if (nbt != null) {
            nbt.remove("Width");
            nbt.remove("Height");
            nbt.remove("Controller");
            if (nbt.contains("Fluids")) {
                NbtList nbtList = nbt.getList("Fluids", 10);
                FluidStack fluidStack = FluidStack.readNbt(nbtList.getCompound(0));
                if (!fluidStack.isEmpty()) {
                    fluidStack.setAmount(Math.min(TankBlockEntity.capacityMultiplier, fluidStack.getAmount()));
                    NbtList nbtListWrite = new NbtList();
                    NbtCompound nbtCompound = new NbtCompound();
                    nbtCompound.putByte("Slot", (byte)1);
                    fluidStack.writeNbt(nbtCompound);
                    nbtListWrite.add(nbtCompound);
                    nbt.put("Fluids", nbtListWrite);
                }
            }
        }
        return super.postPlacement(pos, world, player, stack, state);
    }

    private void tryMultiPlace(ItemPlacementContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null)
            return;

        if (player.isSneaking())
            return;

        Direction face = context.getSide();
        if (!face.getAxis().isVertical())
            return;

        ItemStack stack = context.getStack();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockPos placedOnPos = pos.offset(face.getOpposite());
        BlockState placedOnState = world.getBlockState(placedOnPos);

        if (!TankBlock.isTank(placedOnState))
            return;

        TankBlockEntity tankBlockEntity = TankConnectivityHandler.anyTankAt(world, placedOnPos);
        if (tankBlockEntity == null)
            return;

        TankBlockEntity controllerTankBlockEntity = tankBlockEntity.getControllerEntity();
        if (controllerTankBlockEntity == null)
            return;

        int width = controllerTankBlockEntity.width;
        if (width == 1)
            return;

        int tanksToPlace = 0;
        BlockPos startPos = face == Direction.DOWN ? controllerTankBlockEntity.getPos().down() : controllerTankBlockEntity.getPos().up(controllerTankBlockEntity.height);

        if (startPos.getY() != pos.getY())
            return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = startPos.add(xOffset, 0, zOffset);
                BlockState blockState = world.getBlockState(offsetPos);
                if (TankBlock.isTank(blockState))
                    continue;

                if (!blockState.getMaterial().isReplaceable())
                    return;

                tanksToPlace++;
            }
        }
        if (!player.isCreative() && stack.getCount() < tanksToPlace)
            return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = startPos.add(xOffset, 0, zOffset);
                BlockState blockState = world.getBlockState(offsetPos);
                if (TankBlock.isTank(blockState))
                    continue;
                ItemPlacementContext newContext = ItemPlacementContext.offset(context, offsetPos, face);
                super.place(newContext);
            }
        }
    }

}
