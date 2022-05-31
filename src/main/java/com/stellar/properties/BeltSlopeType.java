package com.stellar.properties;

import net.minecraft.util.StringIdentifiable;

public enum BeltSlopeType implements StringIdentifiable {
  HORIZONTAL("horizontal"), UPWARD("upward"), DOWNWARD("downward");

  private final String name;

  private BeltSlopeType(String name) {
    this.name = name;
  }

  public String toString() {
    return this.name;
  }

  public String asString() {
    return this.name;
  }
}
