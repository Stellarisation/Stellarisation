package com.stellar.block;

import com.stellar.blockentity.TankBlockEntity;
import com.stellar.blockentity.TankConnectivityHandler;
import com.stellar.properties.StellarProperties;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TankBlock extends BlockWithEntity implements BlockEntityProvider, IWrenchable, IPipe {

    public static final BooleanProperty WINDOW;
    public static final BooleanProperty TOP;
    public static final BooleanProperty BOTTOM;

    public TankBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.getDefaultState().with(WINDOW, true).with(TOP, true).with(BOTTOM, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(WINDOW,TOP,BOTTOM);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TankBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public ActionResult onSneakedWrenched(BlockState state, ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        TankBlockEntity tankBlockEntity = (TankBlockEntity) blockEntity;
        tankBlockEntity.toggleWindows();
        return ActionResult.success(world.isClient());
    }

    public static boolean isTank(BlockState state) {
        return state.getBlock() instanceof TankBlock;
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

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(state.hasBlockEntity()){
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!(blockEntity instanceof TankBlockEntity)){
                return;
            }
            TankBlockEntity tankBlockEntity = (TankBlockEntity) blockEntity;
            TankConnectivityHandler.formTanks(tankBlockEntity);
        }
        super.onPlaced(world,pos,state,placer,itemStack);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (state.hasBlockEntity()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!(blockEntity instanceof TankBlockEntity))
                return;
            TankBlockEntity tankBlockEntity = (TankBlockEntity) blockEntity;
            world.removeBlockEntity(pos);
            TankConnectivityHandler.splitTank(tankBlockEntity);
        }
        super.onBreak(world,pos,state,player);
    }

    static {
        WINDOW = StellarProperties.WINDOW;
        TOP = StellarProperties.TOP;
        BOTTOM = StellarProperties.BOTTOM;
    }
}
