package com.stellar.properties;

import net.minecraft.util.StringIdentifiable;

public enum BeltPartType implements StringIdentifiable {
  START("start"), MIDDLE("middle"), END("end"), SINGLE("single");

  private final String name;

  private BeltPartType(String name) {
    this.name = name;
  }

  public String toString() {
    return this.name;
  }

  public String asString() {
    return this.name;
  }
}
