package com.stellar.register;

import com.stellar.Stellar;
import com.stellar.block.*;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockRegistry extends RegistryUtil {

    public static final Block DIRT = registerBlock("dirt", 50.0F, 50.0F, Material.SOIL, BlockSoundGroup.GRAVEL);
    public static final Block BIOMASS = Registry.register(Registry.BLOCK, new Identifier(Stellar.ID, "biomass"), new FluidBlock(FluidRegistry.STILL_BIOMASS, AbstractBlock.Settings.of(Material.WATER).noCollision().strength(100.0F).dropsNothing()){});
    public static final Block STAINLESS_STEEL_PIPE = register(new PipeBlock(0.25f, FabricBlockSettings.of(Material.METAL).strength(50.0F, 50.0F).sounds(BlockSoundGroup.METAL).requiresTool()), "stainless_steel_pipe");
    public static final Block STAINLESS_STEEL_SHEET_BLOCK = registerBlock("stainless_steel_sheet_block", 50.0F, 50.0F, Material.METAL, BlockSoundGroup.METAL);
    public static final Block IRON_SHEET_BLOCK = registerBlock("iron_sheet_block", 50.0F, 50.0F, Material.METAL, BlockSoundGroup.METAL);
    public static final Block IRON_SHEET_PORT_BLOCK = register(new PipePortBlock(FabricBlockSettings.of(Material.METAL).strength(50.0F, 50.0F).sounds(BlockSoundGroup.METAL).requiresTool()), "iron_sheet_port_block");
    public static final Block IRON_CATWALK = register(new CatwalkBlock(FabricBlockSettings.of(Material.METAL).strength(50.0F, 50.0F).sounds(BlockSoundGroup.METAL)), "iron_catwalk");
    public static final Block CREATIVE_CRATE = register(new CreativeCrateBlock(FabricBlockSettings.of(Material.METAL).strength(50.0F, 50.0F)), "creative_crate");
    public static final Block CREATIVE_TANK = register(new CreativeTankBlock(FabricBlockSettings.of(Material.METAL).strength(50.0F, 50.0F)), "creative_tank");
}
