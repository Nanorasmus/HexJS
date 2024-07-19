package me.nanorasmus.nanodev.hex_js.kubejs.types;

import at.petrak.hexcasting.api.spell.iota.BooleanIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import dev.latvian.mods.rhino.util.HideFromJS;

public interface IotaJS {
    @HideFromJS
    public <T extends Iota> T toIota();
    public <T extends IotaJS> T copy();
}
