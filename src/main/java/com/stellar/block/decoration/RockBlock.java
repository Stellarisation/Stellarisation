package com.stellar.block.decoration;

import com.stellar.properties.StellarProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class RockBlock extends Block {

    public static final IntProperty ROCKS = StellarProperties.ROCKS;
    protected static final VoxelShape SHAPE_1 = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
    protected static final VoxelShape SHAPE_2 = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
    protected static final VoxelShape SHAPE_3 = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 10.0, 13.0);

    public RockBlock(Settings settings) {
        super(settings.dynamicBounds());
        this.setDefaultState((BlockState)this.getDefaultState().with(ROCKS, 1));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(ROCKS);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Vec3d vec3d = state.getModelOffset(world, pos);
        switch ((Integer)state.get(ROCKS)) {
            case 1:
                return SHAPE_1.offset(vec3d.x, vec3d.y, vec3d.z);
            case 2:
                return SHAPE_2.offset(vec3d.x, vec3d.y, vec3d.z);
            case 3:
                return SHAPE_3.offset(vec3d.x, vec3d.y, vec3d.z);
            default:
                return VoxelShapes.empty();
        }
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return !context.shouldCancelInteraction() && context.getStack().getItem() == this.asItem() && (Integer)state.get(ROCKS) < 3 ? true : super.canReplace(state, context);
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        return blockState.isOf(this) ? (BlockState)blockState.with(ROCKS, Math.min(3, (Integer)blockState.get(ROCKS) + 1)) : super.getPlacementState(ctx);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack);
        world.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
        int i = (Integer)state.get(ROCKS);
        if (i <= 1) {
            world.breakBlock(pos, false);
        } else {
            world.setBlockState(pos, (BlockState)state.with(ROCKS, i - 1), 2);
            world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state));
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.down());
        return Block.isFaceFullSquare(blockState.getCollisionShape(world, pos.down()), Direction.UP);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }
}
