package com.stellar.block;

import com.stellar.blockentity.ContainerBlockEntity;
import com.stellar.blockentity.ContainerConnectivityHandler;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ContainerBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final EnumProperty<Direction.Axis> HORIZONTAL_AXIS;

    public ContainerBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.getDefaultState().with(HORIZONTAL_AXIS, Direction.Axis.X));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(HORIZONTAL_AXIS);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        if (context.getPlayer() == null || !context.getPlayer().isSneaking()) {
            BlockState placedOn = context.getWorld().getBlockState(context.getBlockPos().offset(context.getSide().getOpposite()));
            Direction.Axis preferredAxis = getContainerBlockAxis(placedOn);
            if (preferredAxis != null)
                return this.getDefaultState().with(HORIZONTAL_AXIS, preferredAxis);
        }
        //return this.getDefaultState().with(HORIZONTAL_AXIS, context.getVerticalPlayerLookDirection().getAxis());
        return this.getDefaultState().with(HORIZONTAL_AXIS, context.getPlayerFacing().getAxis());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(state.hasBlockEntity()){
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!(blockEntity instanceof ContainerBlockEntity)){
                return;
            }
            ContainerBlockEntity contaierBlockEntity = (ContainerBlockEntity) blockEntity;
            ContainerConnectivityHandler.formContainers(contaierBlockEntity);
        }
        super.onPlaced(world,pos,state,placer,itemStack);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ContainerBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public static boolean isContainer(BlockState state) {
        return state.getBlock() instanceof ContainerBlock;
    }

    @Nullable
    public static Direction.Axis getContainerBlockAxis(BlockState state) {
        if (!isContainer(state))
            return null;
        return state.get(HORIZONTAL_AXIS);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return (world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof BlockEntityTicker) {
                ((BlockEntityTicker) blockEntity).tick(world1, pos, state1, blockEntity);
            }
        };
    }

    static {
        HORIZONTAL_AXIS = Properties.HORIZONTAL_AXIS;
    }
}
