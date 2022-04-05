package com.stellar.properties;

import net.minecraft.util.StringIdentifiable;

public enum TopBottomType implements StringIdentifiable {
  TOP("top"), BOTTOM("bottom");

  private final String name;

  private TopBottomType(String name) {
    this.name = name;
  }

  public String toString() {
    return this.name;
  }

  public String asString() {
    return this.name;
  }
}
