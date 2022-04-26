package com.stellar.register;

import com.stellar.blockentity.CreativeCrateBlockEntity;
import com.stellar.blockentity.CreativeTankBlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public class BlockEntityRegistry extends RegistryUtil{

    public static final BlockEntityType<CreativeCrateBlockEntity> CREATIVE_CRATE_BLOCK_ENTITY = register(BlockRegistry.CREATIVE_CRATE, "creative_crate_block_entity", CreativeCrateBlockEntity::new);
    public static final BlockEntityType<CreativeTankBlockEntity> CREATIVE_TANK_BLOCK_ENTITY = register(BlockRegistry.CREATIVE_TANK, "creative_tank_block_entity", CreativeTankBlockEntity::new);
}
