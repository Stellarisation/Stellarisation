package com.stellar.blockentity;

import com.stellar.fluid.FluidInventories;
import com.stellar.fluid.FluidStack;
import com.stellar.register.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CreativeTankBlockEntity extends BlockEntity implements BlockEntityTicker {

    public DefaultedList<FluidStack> fluids = DefaultedList.ofSize(2, FluidStack.empty());
    private long capacity = 1000;

    public CreativeTankBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.CREATIVE_TANK_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        FluidInventories.writeNbt(nbt,fluids);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        FluidInventories.readNbt(nbt,fluids);
    }

    public void setTank(FluidStack fluid){
        if(fluids.get(0).isEmpty()){
            fluids.set(0,fluid);
        } else {
            fluids.get(0).grow(fluid.getAmount());
        }
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
    }

    public Long getCapacity() {
        return capacity;
    }

    public float getFillState() {
        return (float) fluids.get(0).getAmount() / capacity;
    }

}
