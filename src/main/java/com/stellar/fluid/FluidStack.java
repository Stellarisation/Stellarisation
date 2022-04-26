package com.stellar.fluid;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

public final class FluidStack {
    private static final FluidStack EMPTY = create(Fluids.EMPTY, 0);
    private long amount;
    @Nullable private NbtCompound tag;
    private Supplier<Fluid> fluid;

    private FluidStack(Supplier<Fluid> fluid, long amount, NbtCompound tag) {
        this.fluid = Objects.requireNonNull(fluid);
        this.amount = amount;
        this.tag = tag == null ? null : tag.copy();
    }

    public static FluidStack empty() {
        return EMPTY;
    }

    public static FluidStack create(Fluid fluid, long amount, @Nullable NbtCompound tag) {
        return create(() -> fluid, amount, tag);
    }

    public static FluidStack create(Fluid fluid, long amount) {
        return create(fluid, amount, null);
    }

    public static FluidStack create(Supplier<Fluid> fluid, long amount, @Nullable NbtCompound tag) {
        return new FluidStack(fluid, amount, tag);
    }

    public static FluidStack create(Supplier<Fluid> fluid, long amount) {
        return create(fluid, amount, null);
    }

    public static FluidStack create(FluidStack stack, long amount) {
        return create(stack.getRawFluidSupplier(), amount, stack.getTag());
    }

    public final Fluid getFluid() {
        return isEmpty() ? Fluids.EMPTY : getRawFluid();
    }

    @Nullable
    public final Fluid getRawFluid() {
        return fluid.get();
    }

    public final Supplier<Fluid> getRawFluidSupplier() {
        return fluid;
    }

    public boolean isEmpty() {
        return getRawFluid() == Fluids.EMPTY || amount <= 0;
    }

    public long getAmount() {
        return isEmpty() ? 0 : amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void grow(long amount) {
        setAmount(this.amount + amount);
    }

    public void shrink(long amount) {
        setAmount(this.amount - amount);
    }

    public boolean hasTag() {
        return tag != null;
    }

    @Nullable
    public NbtCompound getTag() {
        return tag;
    }

    public void setTag(@Nullable NbtCompound tag) {
        this.tag = tag;
    }

    public FluidStack copy() {
        return new FluidStack(fluid, amount, tag);
    }

    @Override
    public final int hashCode() {
        var code = 1;
        code = 31 * code + getFluid().hashCode();
        code = 31 * code + Long.hashCode(amount);
        if (tag != null)
            code = 31 * code + tag.hashCode();
        return code;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof FluidStack)) {
            return false;
        }
        return isFluidStackEqual((FluidStack) o);
    }

    public boolean isFluidStackEqual(FluidStack other) {
        return getFluid() == other.getFluid() && getAmount() == other.getAmount() && isTagEqual(other);
    }

    private boolean isTagEqual(FluidStack other) {
        return tag == null ? other.tag == null : other.tag != null && tag.equals(other.tag);
    }

    public static FluidStack readNbt(NbtCompound tag) {
        if (tag == null || !tag.contains("id")) {
            return FluidStack.empty();
        }
        Fluid fluid = Registry.FLUID.get(new Identifier(tag.getString("id")));
        if (fluid == null || fluid == Fluids.EMPTY) {
            return FluidStack.empty();
        }
        long amount = tag.getLong("Amount");
        FluidStack stack = FluidStack.create(fluid, amount);
        if (tag.contains("tag")) {
            stack.setTag(tag.getCompound("tag"));
        }
        return stack;
    }

    public NbtCompound writeNbt(NbtCompound tag) {
        return writeNbt(this, tag);
    }

    public static NbtCompound writeNbt(FluidStack stack, NbtCompound tag) {
        tag.putString("id", Registry.FLUID.getId(stack.getFluid()).toString());
        tag.putLong("Amount", stack.getAmount());
        if (stack.hasTag()) {
            tag.put("tag", stack.getTag());
        }
        return tag;
    }

    @Environment(EnvType.CLIENT)
    public static int getColor(FluidStack stack) {
        if (stack.getFluid() == Fluids.EMPTY) return -1;
        var handler = FluidRenderHandlerRegistry.INSTANCE.get(stack.getFluid());
        if (handler == null) return -1;
        return handler.getFluidColor(null, null, stack.getFluid().getDefaultState());
    }

    @Environment(EnvType.CLIENT)
    public static int getColor(Fluid fluid) {
        if (fluid == Fluids.EMPTY) return -1;
        var handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
        if (handler == null) return -1;
        return handler.getFluidColor(null, null, fluid.getDefaultState());
    }
}
