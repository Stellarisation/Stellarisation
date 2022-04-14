package com.stellar.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.BlockDataObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.property.Property;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

import java.util.Map.Entry;

public class DebugToolItem extends Item {

	public DebugToolItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
		Block block = blockState.getBlock();
		if (block == null) {
			return ActionResult.FAIL;
		}
		if (context.getWorld().isClient) {
			return ActionResult.SUCCESS;
		}
		sendMessage(context, new LiteralText(getRegistryName(block)));

		for (Entry<Property<?>, Comparable<?>> entry : blockState.getEntries().entrySet()) {
			sendMessage(context, new LiteralText(getPropertyString(entry)));
		}

		BlockEntity blockEntity = context.getWorld().getBlockEntity(context.getBlockPos());
		if (blockEntity == null) {
			return ActionResult.CONSUME;
		}

		sendMessage(context, new LiteralText(getBlockEntityType(blockEntity)));

		sendMessage(context, getBlockEntityTags(blockEntity));

		return ActionResult.CONSUME;
	}

	private void sendMessage(ItemUsageContext context, Text message) {
		if (context.getWorld().isClient || context.getPlayer() == null) {
			return;
		}
		context.getPlayer().sendSystemMessage(message, Util.NIL_UUID);
	}

	private String getPropertyString(Entry<Property<?>, Comparable<?>> entryIn) {
		Property<?> property = entryIn.getKey();
		Comparable<?> comparable = entryIn.getValue();
		String s = Util.getValueAsString(property, comparable);
		if (Boolean.TRUE.equals(comparable)) {
			s = Formatting.GREEN + s;
		} else if (Boolean.FALSE.equals(comparable)) {
			s = Formatting.RED + s;
		}

		return property.getName() + ": " + s;
	}

	private String getRegistryName(Block block) {
		String s = "" + Formatting.GREEN;
		s += "Block Registry Name: ";
		s += Formatting.BLUE;
		s += Registry.BLOCK.getId(block);

		return s;
	}

	private String getBlockEntityType(BlockEntity blockEntity) {
		String s = "" + Formatting.GREEN;
		s += "Block Entity: ";
		s += Formatting.BLUE;
		s += blockEntity.getType().toString();

		return s;
	}

	private Text getBlockEntityTags(BlockEntity blockEntity){
		MutableText s = new LiteralText("BlockEntity Tags:").formatted(Formatting.GREEN);

		BlockDataObject bdo = new BlockDataObject(blockEntity, blockEntity.getPos());
		s.append(bdo.getNbt().toString());

		return s;
	}
}
