package me.nanorasmus.nanodev.hex_js.kubejs.types;

import at.petrak.hexcasting.api.spell.iota.BooleanIota;
import at.petrak.hexcasting.api.spell.iota.GarbageIota;
import dev.latvian.mods.rhino.util.HideFromJS;

public class IotaGarbage implements IotaJS {
    private boolean value;

    // -- Basic stuff --

    public IotaGarbage() {}
    @HideFromJS
    public IotaGarbage(GarbageIota value) {}


    @Override
    @HideFromJS
    public GarbageIota toIota() {
        return new GarbageIota();
    }

    @Override
    public IotaGarbage copy() {
        return new IotaGarbage();
    }
}
