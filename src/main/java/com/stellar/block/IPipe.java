package com.stellar.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

interface IPipe {

    default boolean canConnectFrom(BlockState state, Direction dir) {
        return true;
    }

    static boolean canConnect(BlockState block, Direction dir) {
        return block.getBlock() instanceof IPipe && ((IPipe)block.getBlock()).canConnectFrom(block, dir.getOpposite());
    }
}
