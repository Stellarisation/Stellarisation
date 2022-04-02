package com.stellar.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.Direction;

public class PipePortBlock extends Block implements IPipe{
    public static final DirectionProperty FACING;

    public PipePortBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    public static Direction getFacing(BlockState state) {
        return state.get(FACING);
    }

    @Override
    public boolean canConnectFrom(BlockState state, Direction dir) {
        return (dir == getFacing(state));
    }

    static {
        FACING = FacingBlock.FACING;
    }
}