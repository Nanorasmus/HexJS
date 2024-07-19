package me.nanorasmus.nanodev.hex_js.kubejs;

import at.petrak.hexcasting.api.PatternRegistry;
import me.nanorasmus.nanodev.hex_js.kubejs.customPatterns.CustomPatternHolder;
import me.nanorasmus.nanodev.hex_js.kubejs.customPatterns.CustomPatternRegistry;
import me.nanorasmus.nanodev.hex_js.kubejs.types.IotaPattern;
import me.nanorasmus.nanodev.hex_js.storage.StorageManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class HexJSBindings {

    public void registerCustomPattern(String name, String angles, boolean isGreatSpell, boolean causesBlindDiversion) {
        CustomPatternRegistry.registry.put(angles, new CustomPatternHolder(
                name,
                new IotaPattern(angles),
                isGreatSpell,
                causesBlindDiversion
        ));
    }

    // -- Default --

    /**
     * Sets whether the pattern list of future players are blacklists or whitelists
     * Takes in a boolean
     * False = Blacklist, True = Whitelist
     */
    public void setDefaultPatternListType(boolean isWhitelist) {
        StorageManager.setDefaultIsWhitelist(isWhitelist);
    }

    /**
     * Adds a pattern to the blacklist/whitelist of future players
     * Takes in a string consisting of angles of the pattern to add, as an example "qaq" is mind's reflection
     */
    public void addDefaultPattern(String patternAngles) {
        StorageManager.addToDefaultPatternList(new ArrayList<>(Collections.singletonList(patternAngles)));
    }

    /**
     * Adds a redirect to future players
     * Takes in a string consisting of angles of the pattern to add, as an example "qaq" is mind's reflection
     */
    public void addRedirect(String fromPatternAngles, String toPatternAngles) {
        StorageManager.addDefaultRedirect(fromPatternAngles, toPatternAngles);
    }


    // -- Per-player --

    /**
     * Sets whether the pattern list of a player is a blacklist or a whitelist
     * Takes in a UUID and a boolean
     * False = Blacklist, True = Whitelist
     * IS PERSISTENT ACROSS RESTARTS
     */
    public void setPlayerPatternListType(UUID playerUUID, boolean isWhitelist) {
        StorageManager.setIsPlayerPatternsWhitelist(playerUUID, isWhitelist);
    }

    /**
     * Adds a pattern to the blacklist/whitelist of the specified player
     * Takes in a UUID and a string consisting of angles of the pattern to add, as an example "qaq" is mind's reflection
     * IS PERSISTENT ACROSS RESTARTS
     */
    public void addPatternToPlayer(UUID playerUUID, String patternAngles) {
        StorageManager.addToPlayerPatternList(playerUUID, new ArrayList<>(Collections.singletonList(patternAngles)));
    }
}
