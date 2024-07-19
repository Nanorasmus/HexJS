package me.nanorasmus.nanodev.hex_js.helpers;

import at.petrak.hexcasting.api.spell.math.HexAngle;

import java.util.ArrayList;

public class PatternHelper {
    public static ArrayList<HexAngle> anglesFromString(String input) {
        ArrayList<HexAngle> angles = new ArrayList<>();
        for (String angle : input.toLowerCase().split("")) {
            switch (angle) {
                case "a":
                    angles.add(HexAngle.LEFT_BACK);
                    break;
                case "q":
                    angles.add(HexAngle.LEFT);
                    break;
                case "w":
                    angles.add(HexAngle.FORWARD);
                    break;
                case "e":
                    angles.add(HexAngle.RIGHT);
                    break;
                case "d":
                    angles.add(HexAngle.RIGHT_BACK);
                    break;
                case "s":
                    angles.add(HexAngle.BACK);
                    break;
            }
        }
        return angles;
    }
}
