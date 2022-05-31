package com.stellar.register;

import com.stellar.blockentity.BeltBlockEntity;
import com.stellar.blockentity.ContainerBlockEntity;
import com.stellar.blockentity.CreativeCrateBlockEntity;
import com.stellar.blockentity.TankBlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public class BlockEntityRegistry extends RegistryUtil{

    public static final BlockEntityType<CreativeCrateBlockEntity> CREATIVE_CRATE_BLOCK_ENTITY = register(BlockRegistry.CREATIVE_CRATE, "creative_crate_block_entity", CreativeCrateBlockEntity::new);
    public static final BlockEntityType<TankBlockEntity> TANK_BLOCK_ENTITY = register(BlockRegistry.TANK, "tank_block_entity", TankBlockEntity::new);
    public static final BlockEntityType<ContainerBlockEntity> CONTAINER_BLOCK_ENTITY = register(BlockRegistry.CONTAINER, "container_block_entity", ContainerBlockEntity::new);
    public static final BlockEntityType<ContainerBlockEntity> BELT_BLOCK_ENTITY = register(BlockRegistry.BELT, "belt_block_entity", BeltBlockEntity::new);
}
