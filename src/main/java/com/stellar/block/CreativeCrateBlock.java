package com.stellar.block;

import com.stellar.blockentity.CreativeCrateBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CreativeCrateBlock extends BlockWithEntity {

    static final VoxelShape SHAPE;

    public CreativeCrateBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeCrateBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
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

    @SuppressWarnings( "deprecation" )
    @Override
    public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockHitResult hitResult) {
        final CreativeCrateBlockEntity CreativeCrate = (CreativeCrateBlockEntity) worldIn.getBlockEntity(pos);
        ItemStack stackInHand = playerIn.getStackInHand(Hand.MAIN_HAND);
        Item itemInHand = stackInHand.getItem();
        if (playerIn.isSneaking()){
            return ActionResult.PASS;
        }
        if (CreativeCrate != null && itemInHand != Items.AIR ) {
            CreativeCrate.creative_item = new ItemStack(itemInHand, itemInHand.getMaxCount());
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, worldIn, pos, playerIn, hand, hitResult);
    }

    static {
        SHAPE = Block.createCuboidShape(1D, 0D, 1D, 15D, 14D, 15D);
    }
}
