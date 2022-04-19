package com.stellar.blockentity;

import com.stellar.register.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CreativeCrateBlockEntity extends BlockEntity implements SidedInventory, Inventory, BlockEntityTicker {

    public DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    public ItemStack creative_item = items.get(0);


    public CreativeCrateBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.CREATIVE_CRATE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        int[] result = new int[items.size()];
        return result;
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        Inventories.writeNbt(tag, items);
        super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        Inventories.readNbt(tag, items);
        creative_item = items.get(0);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        if(!items.get(0).isEmpty()){
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot)  {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if(amount == 0){
            return ItemStack.EMPTY;
        }
        if(amount == items.get(slot).getCount()){
            return removeStack(slot);
        }

        ItemStack toRet = items.get(slot).copy();
        items.get(slot).decrement(amount);
        toRet.setCount(amount);

        return toRet;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack toRet = items.get(slot).copy();
        items.set(slot, ItemStack.EMPTY);

        return toRet;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.hasWorld() && !this.getWorld().isClient()) {
            ((ServerWorld) world).getChunkManager().markForUpdate(getPos());
        }
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        items.set(0, creative_item);
    }
}
