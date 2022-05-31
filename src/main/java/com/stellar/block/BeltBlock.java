package com.stellar.block;

import com.stellar.blockentity.BeltBlockEntity;
import com.stellar.properties.BeltPartType;
import com.stellar.properties.BeltSlopeType;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class BeltBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final DirectionProperty FACING;
    public static final EnumProperty<BeltPartType> PART;
    public static final EnumProperty<BeltSlopeType> SLOPE;

    public BeltBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(PART, BeltPartType.SINGLE).with(SLOPE, BeltSlopeType.HORIZONTAL));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING, PART, SLOPE);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BeltBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    static {
        PART = EnumProperty.of("part", BeltPartType.class);
        SLOPE = EnumProperty.of("slope", BeltSlopeType.class);
        FACING = HorizontalFacingBlock.FACING;
    }
}
