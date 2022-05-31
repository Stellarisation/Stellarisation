package com.stellar.blockentity;

import net.minecraft.util.math.BlockPos;

public interface IMultiEntityContainer {

    BlockPos getController();
    boolean isController();
    void setController(BlockPos pos);

}
