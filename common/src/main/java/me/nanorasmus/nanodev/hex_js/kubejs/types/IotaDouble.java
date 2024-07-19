package me.nanorasmus.nanodev.hex_js.kubejs.types;

import at.petrak.hexcasting.api.spell.iota.BooleanIota;
import at.petrak.hexcasting.api.spell.iota.DoubleIota;
import dev.latvian.mods.rhino.util.HideFromJS;

public class IotaDouble implements IotaJS {
    private double value;

    public IotaDouble(double value) {
        this.value = value;
    }
    @HideFromJS
    public IotaDouble(DoubleIota iota) {
        value = iota.getDouble();
    }

    @HideFromJS
    public DoubleIota toIota() {
        return new DoubleIota(value);
    }


    /**
     * Gets the internal value
     */
    public double getValue() {
        return value;
    }

    @Override
    public IotaDouble copy() {
        return new IotaDouble(value);
    }
}
