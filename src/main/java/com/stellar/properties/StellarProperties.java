package com.stellar.properties;


import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;

public class StellarProperties extends Properties {
    public static final BooleanProperty WINDOW = BooleanProperty.of("window");
    public static final BooleanProperty BOTTOM = BooleanProperty.of("bottom");
    public static final BooleanProperty TOP = BooleanProperty.of("top");
    public static final IntProperty ROCKS = IntProperty.of("rocks", 1, 3);

    public StellarProperties() {
    }
}
