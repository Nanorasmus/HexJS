package me.nanorasmus.nanodev.hex_js.storage;

import at.petrak.hexcasting.api.spell.math.HexAngle;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.api.utils.HexUtils;
import me.nanorasmus.nanodev.hex_js.helpers.PatternHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class PatternList {
    boolean isWhitelist = false;
    public ArrayList<String> angleSignatureList = new ArrayList<>();
    public HashMap<String, ArrayList<HexAngle>> redirectList = new HashMap<>();
    public HashMap<String, String> redirectListRaw = new HashMap<>();

    public PatternList() {
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
        ArrayList<HexAngle> angles = PatternHelper.anglesFromString(output);
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
