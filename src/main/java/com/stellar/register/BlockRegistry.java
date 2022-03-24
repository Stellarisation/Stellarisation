package com.stellar.register;

import com.stellar.Stellar;
import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockRegistry extends RegistryUtil{

    public static final Block DIRT = registerBlock("dirt", 50.0F, 50.0F, Material.SOIL, BlockSoundGroup.GRAVEL);
    public static final Block BIOMASS = Registry.register(Registry.BLOCK, new Identifier(Stellar.ID, "biomass"), new FluidBlock(FluidRegistry.STILL_BIOMASS, AbstractBlock.Settings.of(Material.WATER).noCollision().strength(100.0F).dropsNothing()){});
}
