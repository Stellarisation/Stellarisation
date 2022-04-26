package com.stellar.fluid;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.List;

public class FluidInventories {
    public FluidInventories() {
    }

    public static NbtCompound writeNbt(NbtCompound nbt, List<FluidStack> stacks) {
        NbtList nbtList = new NbtList();
        for(int i = 0; i < stacks.size(); ++i) {
            FluidStack fluidStack = (FluidStack)stacks.get(i);
            if (!fluidStack.isEmpty()) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putByte("Slot", (byte)i);
                fluidStack.writeNbt(nbtCompound);
                nbtList.add(nbtCompound);
            }
        }
        if (!nbtList.isEmpty()) {
            nbt.put("Fluids", nbtList);
        }
        return nbt;
    }

    public static void readNbt(NbtCompound nbt, List<FluidStack> stacks) {
        NbtList nbtList = nbt.getList("Fluids", 10);
        for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            if (j >= 0 && j < stacks.size()) {
                stacks.set(j, FluidStack.readNbt(nbtCompound));
            }
        }
    }
}
