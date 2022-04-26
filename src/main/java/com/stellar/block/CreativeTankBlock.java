package com.stellar.block;

import com.stellar.blockentity.CreativeTankBlockEntity;
import com.stellar.fluid.FluidStack;
import com.stellar.properties.StellarProperties;
import com.stellar.register.FluidRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CreativeTankBlock extends BlockWithEntity implements BlockEntityProvider, IWrenchable {

    public static final BooleanProperty WINDOW;

    public CreativeTankBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.getDefaultState().with(WINDOW, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(WINDOW);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeTankBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockHitResult hitResult) {
        final CreativeTankBlockEntity CreativeTank = (CreativeTankBlockEntity) worldIn.getBlockEntity(pos);
        ItemStack stackInHand = playerIn.getStackInHand(Hand.MAIN_HAND);
        Item itemInHand = stackInHand.getItem();
        if (playerIn.isSneaking()){
            return ActionResult.PASS;
        }
        if (CreativeTank != null && itemInHand != Items.AIR ) {
            CreativeTank.setTank(FluidStack.create(FluidRegistry.STILL_BIOMASS,100));
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, worldIn, pos, playerIn, hand, hitResult);
    }

    public ActionResult onSneakedWrenched(BlockState state, ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);

        world.setBlockState(blockPos, (BlockState) blockState.with(WINDOW, !blockState.get(WINDOW)));
        return ActionResult.success(world.isClient());
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
        WINDOW = StellarProperties.WINDOW;
    }
}
