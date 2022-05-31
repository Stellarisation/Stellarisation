package com.stellar.fluid;

public interface IFluidHandler {
    int getSize();
    FluidStack getFluidInTank(int tank);
    long getCapacity();
    long fill(FluidStack stack); // returns amount filled
    FluidStack drain(FluidStack stack, long amount); // returns amount drained
    FluidStack drain(long amount); // returns amount drained
    default boolean isFluidValid(int tank, FluidStack stack) { return true; }
}
