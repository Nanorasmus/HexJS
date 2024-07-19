package me.nanorasmus.nanodev.hex_js.kubejs.types;

import at.petrak.hexcasting.api.spell.math.HexDir;
import dev.latvian.mods.rhino.util.HideFromJS;

public enum PatternStartDir {
    NORTH_EAST,

    EAST,

    SOUTH_EAST,

    SOUTH_WEST,

    WEST,

    NORTH_WEST;

    @HideFromJS
    public static HexDir toHexDir(PatternStartDir self) {
        switch (self) {
            case NORTH_EAST -> {
                return HexDir.NORTH_EAST;
            }
            case EAST -> {
                return HexDir.EAST;
            }
            case SOUTH_EAST -> {
                return HexDir.SOUTH_EAST;
            }
            case SOUTH_WEST -> {
                return HexDir.SOUTH_WEST;
            }
            case WEST -> {
                return HexDir.WEST;
            }
            case NORTH_WEST -> {
                return HexDir.NORTH_WEST;
            }
        }
        return HexDir.EAST;
    }

    @HideFromJS
    public static PatternStartDir fromHexDir(HexDir dir) {
        switch (dir) {
            case NORTH_EAST -> {
                return PatternStartDir.NORTH_EAST;
            }
            case EAST -> {
                return PatternStartDir.EAST;
            }
            case SOUTH_EAST -> {
                return PatternStartDir.SOUTH_EAST;
            }
            case SOUTH_WEST -> {
                return PatternStartDir.SOUTH_WEST;
            }
            case WEST -> {
                return PatternStartDir.WEST;
            }
            case NORTH_WEST -> {
                return PatternStartDir.NORTH_WEST;
            }
        }
        return PatternStartDir.EAST;
    }
}
