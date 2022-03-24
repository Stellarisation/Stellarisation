package com.stellar.register;

import com.stellar.Stellar;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegistryUtil {

    public static <B extends Block> B register(B block, String id) {
        net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.BLOCK, new Identifier(Stellar.ID, id), block);
        return block;
    }

    public static <I extends Item> I register(I item, String id) {
        net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.ITEM, new Identifier(Stellar.ID, id), item);
        return item;
    }

    public static <F extends Fluid> F register(F fluid, String id) {
        return net.minecraft.util.registry.Registry.register(Registry.FLUID, new Identifier(Stellar.ID, id), fluid);
    }

    public static Item register(String id, Block block, ItemGroup group) {
        return net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.ITEM, new Identifier(Stellar.ID, id), new BlockItem(block, new Item.Settings().maxCount(64).group(group)));
    }

    public static Item register(String id, ItemGroup group) {
        return net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.ITEM, new Identifier(Stellar.ID, id), new Item(new Item.Settings().maxCount(64).group(group)));
    }

    public static Block registerBlock(String id, float hardness, float resistance, Material material, BlockSoundGroup sound) {
        return register(new Block(FabricBlockSettings.of(material).strength(hardness, resistance).sounds(sound).requiresTool()), id);
    }
}
