package com.stellar.item.blockitem;

import com.stellar.block.ContainerBlock;
import com.stellar.blockentity.ContainerBlockEntity;
import com.stellar.blockentity.ContainerConnectivityHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ContainerBlockItem extends BlockItem {
    public ContainerBlockItem(Block block, Settings settings) {super(block, settings); }

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
            nbt.remove("Radius");
            nbt.remove("Length");
            nbt.remove("Controller");
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
        ItemStack stack = context.getStack();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockPos placedOnPos = pos.offset(face.getOpposite());
        BlockState placedOnState = world.getBlockState(placedOnPos);

        if (!face.getAxis().isHorizontal())
            return;

        if (!ContainerBlock.isContainer(placedOnState))
            return;

        ContainerBlockEntity containerBlockEntity = ContainerConnectivityHandler.anyContainerAt(world, placedOnPos);
        if (containerBlockEntity == null)
            return;

        ContainerBlockEntity controllerContainerBlockEntity = containerBlockEntity.getControllerEntity();
        if (controllerContainerBlockEntity == null)
            return;

        int width = controllerContainerBlockEntity.radius;
        if (width == 1)
            return;

        int tanksToPlace = 0;
        Direction.Axis containerBlockAxis = ContainerBlock.getContainerBlockAxis(placedOnState);
        if (containerBlockAxis == null)
            return;
        if (face.getAxis() != containerBlockAxis)
            return;

        Direction containerFacing = Direction.from(containerBlockAxis, Direction.AxisDirection.POSITIVE);
        BlockPos startPos = face == containerFacing.getOpposite() ? controllerContainerBlockEntity.getPos()
                .offset(containerFacing.getOpposite())
                : controllerContainerBlockEntity.getPos()
                .offset(containerFacing, controllerContainerBlockEntity.length);

        if(containerBlockAxis.choose(startPos.getX(),startPos.getY(),startPos.getZ()) != containerBlockAxis.choose(pos.getX(),pos.getY(),pos.getZ()))
            return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = containerBlockAxis == Direction.Axis.X ? startPos.add(0, xOffset, zOffset)
                        : startPos.add(xOffset, zOffset, 0);
                BlockState blockState = world.getBlockState(offsetPos);
                if (ContainerBlock.isContainer(blockState))
                    continue;
                if (!blockState.getMaterial()
                        .isReplaceable())
                    return;
                tanksToPlace++;
            }
        }

        if (!player.isCreative() && stack.getCount() < tanksToPlace)
            return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = containerBlockAxis == Direction.Axis.X ? startPos.add(0, xOffset, zOffset)
                        : startPos.add(xOffset, zOffset, 0);
                BlockState blockState = world.getBlockState(offsetPos);
                if (ContainerBlock.isContainer(blockState))
                    continue;
                ItemPlacementContext newContext = ItemPlacementContext.offset(context, offsetPos, face);
                super.place(newContext);
            }
        }
    }
}
