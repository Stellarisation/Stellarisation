package com.stellar.register;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

public class BlockRegistry extends RegistryUtil{

    public static final Block DIRT = registerBlock("dirt", 50.0F, 50.0F, Material.SOIL, BlockSoundGroup.GRAVEL);
}
