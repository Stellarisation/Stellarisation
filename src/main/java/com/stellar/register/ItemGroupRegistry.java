package com.stellar.register;

import com.stellar.Stellar;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ItemGroupRegistry {

    public static final ItemGroup BLOCKS = FabricItemGroupBuilder.create(new Identifier(Stellar.ID, "blocks")).icon(() -> new ItemStack(ItemRegistry.DIRT)).build();
    public static final ItemGroup ITEMS = FabricItemGroupBuilder.create(new Identifier(Stellar.ID, "items")).icon(() -> new ItemStack(ItemRegistry.FERTILIZER)).build();
}
