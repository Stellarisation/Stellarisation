package com.stellar.register;

import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class ItemRegistry extends RegistryUtil{

    public static final Item DIRT = register("dirt", BlockRegistry.DIRT, ItemGroupRegistry.BLOCKS);
    public static final Item FERTILIZER = register("fertilizer", ItemGroupRegistry.ITEMS);
    public static final Item BIOMASS_BUCKET = register(new BucketItem(FluidRegistry.STILL_BIOMASS, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ItemGroupRegistry.ITEMS)),"biomass_bucket");
}
