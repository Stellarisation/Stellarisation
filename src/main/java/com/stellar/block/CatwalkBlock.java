package com.stellar.block;

import com.stellar.properties.TopBottomType;
import net.minecraft.block.*;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CatwalkBlock extends Block implements Waterloggable, IWrenchable {

    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final EnumProperty<TopBottomType> TYPE;
    public static final BooleanProperty WATERLOGGED;

    static final VoxelShape VOXEL_TOP;
    static final VoxelShape VOXEL_BOTTOM;

    static final VoxelShape VOXEL_SHAPE_SOUTH_BOTTOM;
    static final VoxelShape VOXEL_SHAPE_EAST_BOTTOM;
    static final VoxelShape VOXEL_SHAPE_WEST_BOTTOM;
    static final VoxelShape VOXEL_SHAPE_NORTH_BOTTOM;

    static final VoxelShape VOXEL_SHAPE_SOUTH_TOP;
    static final VoxelShape VOXEL_SHAPE_EAST_TOP;
    static final VoxelShape VOXEL_SHAPE_WEST_TOP;
    static final VoxelShape VOXEL_SHAPE_NORTH_TOP;

    public CatwalkBlock(Settings blockSettings) {
        super(blockSettings.nonOpaque());
        this.setDefaultState(this.getDefaultState().with(TYPE, TopBottomType.BOTTOM).with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(TYPE, NORTH, EAST, SOUTH, WEST, WATERLOGGED);
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return false;
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        //return stateFrom.isOf(this) ? true : super.isSideInvisible(state, stateFrom, direction.getOpposite());
        return false;
    }


    public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);

        double xPos = context.getHitPos().getX() - blockPos.getX();
        double zPos = context.getHitPos().getZ() - blockPos.getZ();
        if (zPos > 0.66) {
            world.setBlockState(blockPos, (BlockState) blockState.with(SOUTH, !blockState.get(SOUTH)));
        } else if (zPos < 0.33) {
            world.setBlockState(blockPos, (BlockState) blockState.with(NORTH, !blockState.get(NORTH)));
        }
        if (xPos > 0.66) {
            world.setBlockState(blockPos, (BlockState) blockState.with(EAST, !blockState.get(EAST)));
        } else if (xPos < 0.33) {
            world.setBlockState(blockPos, (BlockState) blockState.with(WEST, !blockState.get(WEST)));
        }
        return ActionResult.success(world.isClient());

    }

    @SuppressWarnings( "deprecation" )
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        TopBottomType type = state.get(TYPE);
        VoxelShape shape = VoxelShapes.empty();

        if(state.get(TYPE) == TopBottomType.TOP){
            shape = VoxelShapes.union(shape, VOXEL_TOP);
            if(state.get(NORTH)) shape = VoxelShapes.union(shape, VOXEL_SHAPE_NORTH_BOTTOM);
            if(state.get(EAST)) shape = VoxelShapes.union(shape, VOXEL_SHAPE_EAST_BOTTOM);
            if(state.get(SOUTH)) shape = VoxelShapes.union(shape, VOXEL_SHAPE_SOUTH_BOTTOM);
            if(state.get(WEST)) shape = VoxelShapes.union(shape, VOXEL_SHAPE_WEST_BOTTOM);
        } else {
            shape = VoxelShapes.union(shape, VOXEL_BOTTOM);
            if(state.get(NORTH)) shape = VoxelShapes.union(shape, VOXEL_SHAPE_NORTH_TOP);
            if(state.get(EAST)) shape = VoxelShapes.union(shape, VOXEL_SHAPE_EAST_TOP);
            if(state.get(SOUTH)) shape = VoxelShapes.union(shape, VOXEL_SHAPE_SOUTH_TOP);
            if(state.get(WEST)) shape = VoxelShapes.union(shape, VOXEL_SHAPE_WEST_TOP);
        }
        return shape;
    }

    static {
        NORTH = ConnectingBlock.NORTH;
        EAST = ConnectingBlock.EAST;
        SOUTH = ConnectingBlock.SOUTH;
        WEST = ConnectingBlock.WEST;
        TYPE = EnumProperty.of("type", TopBottomType.class);
        WATERLOGGED = Properties.WATERLOGGED;

        VOXEL_TOP = Block.createCuboidShape(0.0D, -2.0D, 0.0D, 16.0D, 0.0D, 16.0D);
        VOXEL_BOTTOM = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

        VOXEL_SHAPE_SOUTH_TOP = Block.createCuboidShape(0.0D, 2.0D, 14.0D, 16.0D, 16.0D, 16.0D);
        VOXEL_SHAPE_EAST_TOP = Block.createCuboidShape(14.0D, 2.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        VOXEL_SHAPE_WEST_TOP = Block.createCuboidShape(0.0D, 2.0D, 0.0D, 2.0D, 16.0D, 16.0D);
        VOXEL_SHAPE_NORTH_TOP = Block.createCuboidShape(0.0D, 2.0D, 0.0D, 16.0D, 16.0D, 2.0D);

        VOXEL_SHAPE_SOUTH_BOTTOM = Block.createCuboidShape(0.0D, 0.0D, 14.0D, 16.0D, 14.0D, 16.0D);
        VOXEL_SHAPE_EAST_BOTTOM = Block.createCuboidShape(14.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);
        VOXEL_SHAPE_WEST_BOTTOM = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 2.0D, 14.0D, 16.0D);
        VOXEL_SHAPE_NORTH_BOTTOM = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 2.0D);
    }
}
