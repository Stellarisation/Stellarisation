package com.stellar.block;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public interface IWrenchable {

	default ActionResult onWrenched(BlockState state, ItemUsageContext context) {
		//Add Default onWrenched Action, maybe rotate?
		return ActionResult.FAIL;
	}

	default ActionResult onSneakedWrenched(BlockState state, ItemUsageContext context) {
		//Add Default onWrenched Action, maybe break Block?
		return ActionResult.FAIL;
	}
}
