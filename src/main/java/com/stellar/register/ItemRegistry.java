package com.stellar.register;

import com.stellar.item.DebugToolItem;
import com.stellar.item.WrenchItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Rarity;

public class ItemRegistry extends RegistryUtil {

    public static final Item DIRT = register("dirt", BlockRegistry.DIRT, ItemGroupRegistry.BLOCKS);
    public static final Item FERTILIZER = register("fertilizer", ItemGroupRegistry.ITEMS);
    public static final Item BIOMASS_BUCKET = register(new BucketItem(FluidRegistry.STILL_BIOMASS, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ItemGroupRegistry.ITEMS)),"biomass_bucket");
    public static final Item STAINLESS_STEEL_PIPE = register("stainless_steel_pipe", BlockRegistry.STAINLESS_STEEL_PIPE, ItemGroupRegistry.BLOCKS);
    public static final Item STAINLESS_STEEL_SHEET_BLOCK = register("stainless_steel_sheet_block", BlockRegistry.STAINLESS_STEEL_SHEET_BLOCK, ItemGroupRegistry.BLOCKS);
    public static final Item IRON_SHEET_BLOCK = register("iron_sheet_block", BlockRegistry.IRON_SHEET_BLOCK, ItemGroupRegistry.BLOCKS);
    public static final Item IRON_SHEET_PORT_BLOCK = register("iron_sheet_port_block", BlockRegistry.IRON_SHEET_PORT_BLOCK, ItemGroupRegistry.BLOCKS);
    public static final Item IRON_CATWALK = register("iron_catwalk", BlockRegistry.IRON_CATWALK, ItemGroupRegistry.BLOCKS);
    public static final Item CREATIVE_CRATE = register("creative_crate", BlockRegistry.CREATIVE_CRATE, ItemGroupRegistry.BLOCKS, new Item.Settings().rarity(Rarity.EPIC));

    public static final Item WRENCH = register(new WrenchItem(new Item.Settings().maxCount(1).group(ItemGroupRegistry.ITEMS)),"wrench");
    public static final Item DEBUG_TOOL = register(new DebugToolItem(new Item.Settings().maxCount(1).group(ItemGroupRegistry.ITEMS).rarity(Rarity.EPIC)),"debug_tool");
}
