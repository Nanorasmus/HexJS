package me.nanorasmus.nanodev.hex_js.kubejs.types;

import at.petrak.hexcasting.api.spell.iota.BooleanIota;
import at.petrak.hexcasting.api.spell.iota.NullIota;
import dev.latvian.mods.rhino.util.HideFromJS;

public class IotaNull implements IotaJS {
    private boolean value;

    // -- Basic stuff --

    public IotaNull() { }
    @HideFromJS
    public IotaNull(NullIota value) { }


    @Override
    @HideFromJS
    public NullIota toIota() {
        return new NullIota();
    }

    @Override
    public IotaNull copy() {
        return new IotaNull();
    }
}
