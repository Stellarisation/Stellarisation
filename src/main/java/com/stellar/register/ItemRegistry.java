package com.stellar.register;

import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class ItemRegistry extends RegistryUtil {

    public static final Item DIRT = register("dirt", BlockRegistry.DIRT, ItemGroupRegistry.BLOCKS);
    public static final Item FERTILIZER = register("fertilizer", ItemGroupRegistry.ITEMS);
    public static final Item BIOMASS_BUCKET = register(new BucketItem(FluidRegistry.STILL_BIOMASS, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ItemGroupRegistry.ITEMS)),"biomass_bucket");
    public static final Item STAINLESS_STEEL_PIPE = register("stainless_steel_pipe", BlockRegistry.STAINLESS_STEEL_PIPE, ItemGroupRegistry.BLOCKS);
    public static final Item STAINLESS_STEEL_SHEET_BLOCK = register("stainless_steel_sheet_block", BlockRegistry.STAINLESS_STEEL_SHEET_BLOCK, ItemGroupRegistry.BLOCKS);
    public static final Item IRON_SHEET_BLOCK = register("iron_sheet_block", BlockRegistry.IRON_SHEET_BLOCK, ItemGroupRegistry.BLOCKS);
    public static final Item IRON_SHEET_PORT_BLOCK = register("iron_sheet_port_block", BlockRegistry.IRON_SHEET_PORT_BLOCK, ItemGroupRegistry.BLOCKS);
}
