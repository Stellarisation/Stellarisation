package com.stellar.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class PipeBlock extends ConnectingBlock implements Waterloggable, IPipe {
    public static final BooleanProperty UP = Properties.UP;
    public static final BooleanProperty DOWN = Properties.DOWN;
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty WEST = Properties.WEST;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty[] DIR_TO_PROPERTY = new BooleanProperty[] {DOWN, UP, NORTH, SOUTH, WEST, EAST};

    /*
    Add Wrench Support to remove facings.
     */

    public PipeBlock(Float radius, Settings settings) {
        //super(0.125f, Block.Settings.of(Material.METAL).strength(2));
        super(radius, settings);
        setDefaultState(getDefaultState()
                .with(DOWN, false)
                .with(UP, false)
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST, WATERLOGGED);
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos) {
        return stateIn.with(ConnectingBlock.FACING_PROPERTIES.get(facing), IPipe.canConnect(facingState, facing));
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {

        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        FluidState ifluidstate = context.getWorld().getFluidState(context.getBlockPos());
        return withConnectionProperties(context.getWorld(), context.getBlockPos()).
                with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
    }

    public BlockState withConnectionProperties(WorldAccess blockView, BlockPos blockPos) {
        return getDefaultState()
                .with(DOWN, IPipe.canConnect(blockView.getBlockState(blockPos.down()), Direction.DOWN))
                .with(UP, IPipe.canConnect(blockView.getBlockState(blockPos.up()), Direction.UP))
                .with(NORTH, IPipe.canConnect(blockView.getBlockState(blockPos.north()), Direction.NORTH))
                .with(EAST, IPipe.canConnect(blockView.getBlockState(blockPos.east()), Direction.EAST))
                .with(SOUTH, IPipe.canConnect(blockView.getBlockState(blockPos.south()), Direction.SOUTH))
                .with(WEST, IPipe.canConnect(blockView.getBlockState(blockPos.west()), Direction.WEST));
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
        switch (blockRotation) {
            case CLOCKWISE_180:
                return blockState.with(NORTH, blockState.get(SOUTH)).with(EAST, blockState.get(WEST)).with(SOUTH, blockState.get(NORTH)).with(WEST, blockState.get(EAST));
            case CLOCKWISE_90:
                return blockState.with(NORTH, blockState.get(EAST)).with(EAST, blockState.get(SOUTH)).with(SOUTH, blockState.get(WEST)).with(WEST, blockState.get(NORTH));
            case COUNTERCLOCKWISE_90:
                return blockState.with(NORTH, blockState.get(WEST)).with(EAST, blockState.get(NORTH)).with(SOUTH, blockState.get(EAST)).with(WEST, blockState.get(SOUTH));
            default:
                break;
        }
        return blockState;
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
        switch (blockMirror) {
            case FRONT_BACK:
                return blockState.with(NORTH, blockState.get(SOUTH)).with(SOUTH, blockState.get(NORTH));
            case LEFT_RIGHT:
                return blockState.with(EAST, blockState.get(WEST)).with(WEST, blockState.get(EAST));
            default:
                break;
        }

        return super.mirror(blockState, blockMirror);
    }
}