package me.nanorasmus.nanodev.hex_js.kubejs.types;

import at.petrak.hexcasting.api.spell.iota.BooleanIota;
import dev.latvian.mods.rhino.util.HideFromJS;

public class IotaBoolean implements IotaJS {
    private boolean value;

    // -- Basic stuff --

    public IotaBoolean (boolean value) {
        this.value = value;
    }
    @HideFromJS
    public IotaBoolean (BooleanIota value) {
        this.value = value.getBool();
    }


    @Override
    @HideFromJS
    public BooleanIota toIota() {
        return new BooleanIota(value);
    }

    @Override
    public IotaBoolean copy() {
        return new IotaBoolean(value);
    }
}
