package com.stellar;

import com.stellar.register.BlockRegistry;
import com.stellar.register.ItemGroupRegistry;
import com.stellar.register.ItemRegistry;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;

public class Stellar implements ModInitializer {

	public static final String ID = "stellar";
	public static final String NAME = "Stellar";
	public static final String VERSION = "0.1";

	public static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize() {

		new BlockRegistry();
		new ItemRegistry();
		new ItemGroupRegistry();
	}
}
