package me.nanorasmus.nanodev.hex_js.kubejs.types;

import at.petrak.hexcasting.api.spell.iota.Iota;

public interface IotaJS {
    public <T extends Iota> T toIota();
    public <T extends IotaJS> T copy();
}
