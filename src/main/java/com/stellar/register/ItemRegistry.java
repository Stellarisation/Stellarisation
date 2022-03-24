package com.stellar.register;

import net.minecraft.item.Item;

public class ItemRegistry extends RegistryUtil{

    public static final Item DIRT = register("dirt", BlockRegistry.DIRT, ItemGroupRegistry.BLOCKS);
    public static final Item FERTILIZER = register("fertilizer", ItemGroupRegistry.ITEMS);
}
