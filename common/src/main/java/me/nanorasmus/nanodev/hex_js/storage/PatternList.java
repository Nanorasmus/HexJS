package me.nanorasmus.nanodev.hex_js.storage;

import at.petrak.hexcasting.api.spell.math.HexAngle;
import at.petrak.hexcasting.api.spell.math.HexPattern;

import java.util.ArrayList;
import java.util.HashMap;

public class PatternList {
    boolean isWhitelist = false;
    public ArrayList<String> angleSignatureList = new ArrayList<>();
    public HashMap<String, ArrayList<HexAngle>> redirectList = new HashMap<>();
    public HashMap<String, String> redirectListRaw = new HashMap<>();

    public PatternList() {
        angleSignatureList.add("qaq");
        addRedirect("wwqe", "qaq");
    }

    public PatternList(boolean isWhitelist) {
        this();
        this.isWhitelist = isWhitelist;
    }

    public PatternList(boolean isWhitelist, ArrayList<String> angleSignatureList) {
        this(isWhitelist);
        this.angleSignatureList = angleSignatureList;
    }

    public PatternList(boolean isWhitelist, ArrayList<String> angleSignatureList, HashMap<String, String> redirects) {
        this(isWhitelist, angleSignatureList);

        for (String redirectInput : redirects.keySet()) {
            addRedirect(redirectInput, redirects.get(redirectInput));
        }
    }

    public void addRedirect(String input, String output) {
        ArrayList<HexAngle> angles = new ArrayList<>();
        for (String angle : output.toLowerCase().split("")) {
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
        redirectList.put(input, angles);
        redirectListRaw.put(input, output);
    }

    public PatternList copy() {
        return new PatternList(
                isWhitelist,
                new ArrayList<>(angleSignatureList),
                new HashMap<>(redirectListRaw)
        );
    }

    public boolean blocks(HexPattern pattern) {
        boolean isBlocked = isWhitelist;
        for (String angleSignature : angleSignatureList) {
            if (angleSignature.equals(pattern.anglesSignature())) {
                isBlocked = !isWhitelist;
                break;
            }
        }
        return isBlocked;
    }
    public HexPattern handleRedirects(HexPattern pattern) {
        if (redirectList.containsKey(pattern.anglesSignature())) {
            return new HexPattern(pattern.getStartDir(), redirectList.get(pattern.anglesSignature()));
        }
        return pattern;
    }
}
