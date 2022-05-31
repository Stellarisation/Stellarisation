package com.stellar.item;

import net.minecraft.item.Item;

public class RadioActiveItem extends Item {

    public double sievert;

    public RadioActiveItem(Settings settings, double sievert) {
        super(settings);
        this.sievert = sievert;
    }

    public double getSievert(){
        return sievert;
    }
}
