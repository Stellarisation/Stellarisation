package com.stellar.register;

import com.stellar.blockentity.CreativeCrateBlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public class BlockEntityRegistry extends RegistryUtil{

    public static final BlockEntityType<CreativeCrateBlockEntity> CREATIVE_CRATE_BLOCK_ENTITY = register(BlockRegistry.CREATIVE_CRATE, "creative_crate_block_entity", CreativeCrateBlockEntity::new);
}
