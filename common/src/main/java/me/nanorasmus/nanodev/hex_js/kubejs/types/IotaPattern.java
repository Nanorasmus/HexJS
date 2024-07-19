package me.nanorasmus.nanodev.hex_js.kubejs.types;

import at.petrak.hexcasting.api.spell.iota.BooleanIota;
import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import dev.latvian.mods.rhino.util.HideFromJS;
import me.nanorasmus.nanodev.hex_js.helpers.PatternHelper;

public class IotaPattern implements IotaJS {
    private HexPattern pattern;

    // -- Basic stuff --

    public IotaPattern(PatternIota pattern) {
        this.pattern = pattern.getPattern();
    }
    @HideFromJS
    public IotaPattern(HexPattern pattern) {
        this.pattern = pattern;
    }
    public IotaPattern(String angles, PatternStartDir startDir) {
        this(new HexPattern(PatternStartDir.toHexDir(startDir), PatternHelper.anglesFromString(angles)));
    }
    public IotaPattern(String angles) {
        this(angles, PatternStartDir.EAST);
    }


    @Override
    @HideFromJS
    public PatternIota toIota() {
        return new PatternIota(pattern);
    }

    @Override
    public IotaPattern copy() {
        return new IotaPattern(pattern);
    }

    public PatternStartDir getStartDir() {
        return PatternStartDir.fromHexDir(pattern.getStartDir());
    }

    public String getAngles() {
        return pattern.anglesSignature();
    }
}
