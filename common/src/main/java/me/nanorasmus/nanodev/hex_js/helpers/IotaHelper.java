package me.nanorasmus.nanodev.hex_js.helpers;

import at.petrak.hexcasting.api.spell.SpellList;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.ListIota;
import at.petrak.hexcasting.api.spell.math.HexAngle;

import java.util.ArrayList;

public class IotaHelper {
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
    /**
     * Sets the element at the given index of the given list iota to the given iota and returns a modified copy of the list iota, or null if the index was out of bounds.
     * @param list the list to insert the iota into
     * @param idx The index to override
     * @param iota the iota to override with
     * @return itself or null if the index was out of bounds
     */
    public static ListIota setElementAtIndexOfListIota(ListIota list, int idx, Iota iota) {
        if (idx >= list.getList().size()) {
            return null;
        }

        return new ListIota(list.getList().modifyAt(idx, it -> new SpellList.LPair(iota, it.getCdr())));
    }


    /**
     * @param list the list to retrieve the iota from
     * @param idx The index of the desired iota
     * @return The iota at the given index, or null if there is none
     */
    public Iota getIotaAtIndexofListIota (ListIota list, int idx) {
        if (idx >= list.getList().size()) {
            return null;
        }
        return list.getList().getAt(idx);
    }
}
