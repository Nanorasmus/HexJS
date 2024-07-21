package me.nanorasmus.nanodev.hex_js.kubejs;

import at.petrak.hexcasting.api.addldata.ADHexHolder;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import dev.latvian.mods.rhino.util.HideFromJS;
import me.nanorasmus.nanodev.hex_js.helpers.IotaHelper;
import me.nanorasmus.nanodev.hex_js.kubejs.customPatterns.CustomPatternHolder;
import me.nanorasmus.nanodev.hex_js.kubejs.customPatterns.CustomPatternRegistry;
import me.nanorasmus.nanodev.hex_js.storage.StorageManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.*;

public class HexJSBindings {

    public void registerCustomPattern(String name, String angles, boolean isGreatSpell, boolean causesBlindDiversion) {
        CustomPatternRegistry.registry.put(angles, new CustomPatternHolder(
                name,
                new HexPattern(HexDir.EAST, IotaHelper.anglesFromString(angles)),
                isGreatSpell,
                causesBlindDiversion
        ));
    }

    public void unregisterCustomPattern(String angles) {
        CustomPatternRegistry.registry.remove(angles);
    }

    // -- Casting Items --
    @HideFromJS
    private ItemStack imbuePatternsFromAngles(Item base, List<String> patternAngles, int media) {
        ItemStack baseStack = base.getDefaultStack();
        ADHexHolder hexHolder = IXplatAbstractions.INSTANCE.findHexHolder(baseStack);
        ArrayList<Iota> patterns = new ArrayList<>();

        for (String angles : patternAngles) {
            patterns.add(new PatternIota(new HexPattern(
                    HexDir.EAST,
                    IotaHelper.anglesFromString(angles)
            )));
        }

        hexHolder.writeHex(patterns, media);

        return baseStack;
    }

    /**
     * Creates a new cypher
     * @param patternAngles A list of strings representing the patterns of the spell
     * @param media the amount of media to put in the spell (in units of 1/10,000 of a default dust)
     * @return A new cypher in the form of an ItemStack
     */
    public ItemStack createCypher(List<String> patternAngles, int media) {
        return imbuePatternsFromAngles(HexItems.CYPHER, patternAngles, media);
    }

    /**
     * Creates a new trinket
     * @param patternAngles A list of strings representing the patterns of the spell
     * @param media the amount of media to put in the spell (in units of 1/10,000 of a default dust)
     * @return A new trinket in the form of an ItemStack
     */
    public ItemStack createTrinket(List<String> patternAngles, int media) {
        return imbuePatternsFromAngles(HexItems.TRINKET, patternAngles, media);
    }

    /**
     * Creates a new artifact
     * @param patternAngles A list of strings representing the patterns of the spell
     * @param media the amount of media to put in the spell (in units of 1/10,000 of a default dust)
     * @return A new artifact in the form of an ItemStack
     */
    public ItemStack createArtifact(List<String> patternAngles, int media) {
        return imbuePatternsFromAngles(HexItems.ARTIFACT, patternAngles, media);
    }

    // -- Global --

    /**
     * Gets whether the global pattern list is a blacklist or whitelist.
     * False = Blacklist, True = Whitelist
     */
    public boolean getGlobalPatternListType() {
        return StorageManager.getGlobalIsWhitelist();
    }

    /**
     * Sets whether the global pattern list is a blacklist or whitelist.
     * Takes in a boolean.
     * False = Blacklist, True = Whitelist
     */
    public void setGlobalPatternListType(boolean isWhitelist) {
        StorageManager.setGlobalIsWhitelist(isWhitelist);
    }

    /**
     * Adds a pattern to the global blacklist/whitelist.
     * Takes in a string consisting of angles of the pattern to add, as an example "qaq" is mind's reflection.
     */
    public void addGlobalPattern(String patternAngles) {
        StorageManager.addToGlobalPatternList(new ArrayList<>(Collections.singletonList(patternAngles)));
    }

    /**
     * Remove a pattern to the global blacklist/whitelist.
     * Takes in a string consisting of angles of the pattern to remove, as an example "qaq" is mind's reflection.
     */
    public void removeGlobalPattern(String patternAngles) {
        StorageManager.removeFromGlobalPatternList(patternAngles);
    }

    /**
     * Clears the global blacklist/whitelist.
     */
    public void clearGlobalList() {
        StorageManager.clearGlobalList();
    }

    /**
     * Adds a global redirect
     * Takes in 2 strings consisting of angles of the pattern to redirect from and to, as an example "qaq" is mind's reflection.
     */
    public void addGlobalRedirect(String fromPatternAngles, String toPatternAngles) {
        StorageManager.addGlobalRedirect(fromPatternAngles, toPatternAngles);
    }


    /**
     * Gets all global redirects in the form of a HashMap(fromPatternAngles, toPatternAngles).
     */
    public HashMap<String, String> getGlobalRedirects() {
        return StorageManager.getGlobalRedirects();
    }

    /**
     * Overrides all global redirects with a new set of redirects.
     * @param redirects The new set of redirects to apply to the player
     */
    public void setGlobalRedirects(HashMap<String, String> redirects) {
        StorageManager.setGlobalRedirects(redirects);
    }

    /**
     * Clears all global redirects
     */
    public void clearGlobalRedirects() {
        StorageManager.clearGlobalRedirects();
    }


    // -- Per-player --

    /**
     * Sets whether the pattern list of a player is a blacklist or a whitelist.
     * Takes in a UUID and a boolean.
     * False = Blacklist, True = Whitelist
     * IS PERSISTENT ACROSS RESTARTS
     */
    public void setPlayerPatternListType(UUID playerUUID, boolean isWhitelist) {
        StorageManager.setIsPlayerPatternsWhitelist(playerUUID, isWhitelist);
    }

    /**
     * Gets whether the pattern list of a player is a blacklist or a whitelist.
     * Takes in a UUID and a boolean.
     * False = Blacklist, True = Whitelist
     */
    public boolean getPlayerPatternListType(UUID playerUUID) {
        return StorageManager.getIsPlayerPatternsWhitelist(playerUUID);
    }

    /**
     * Adds a pattern to the blacklist/whitelist of the specified player.
     * Takes in a UUID and a string consisting of angles of the pattern to add, as an example "qaq" is mind's reflection.
     * IS PERSISTENT ACROSS RESTARTS
     */
    public void addPatternToPlayer(UUID playerUUID, String patternAngles) {
        StorageManager.addToPlayerPatternList(playerUUID, new ArrayList<>(Collections.singletonList(patternAngles)));
    }

    /**
     * Removes pattern to the blacklist/whitelist of the specified player.
     * Takes in a UUID and a string consisting of angles of the pattern to remove, as an example "qaq" is mind's reflection.
     * IS PERSISTENT ACROSS RESTARTS
     */
    public void removePatternFromPlayer(UUID playerUUID, String patternAngles) {
        StorageManager.removeFromPlayerPatternList(playerUUID, patternAngles);
    }

    /**
     * Clears the blacklist/whitelist of a player.
     * IS PERSISTENT ACROSS RESTARTS
     * @param playerUUID The UUID of the player in question
     */
    public void clearPlayerList(UUID playerUUID) {
        StorageManager.clearPlayerPatternList(playerUUID);
    }

    /**
     * Adds a redirect to specified player.
     * Takes in a UUID and 2 strings consisting of angles of the pattern to redirect from and to, as an example "qaq" is mind's reflection.
     * IS PERSISTENT ACROSS RESTARTS
     */
    public void addRedirectToPlayer(UUID playerUUID, String fromPatternAngles, String toPatternAngles) {
        StorageManager.addPlayerRedirect(playerUUID, fromPatternAngles, toPatternAngles);
    }


    /**
     * Gets all redirects of a player in the form of a HashMap(fromPatternAngles, toPatternAngles).
     * @param playerUUID The UUID of the player in question
     */
    public HashMap<String, String> getPlayerRedirects(UUID playerUUID) {
        return StorageManager.getPlayerRedirects(playerUUID);
    }

    /**
     * Overrides all current redirects of a player with a new set of redirects.
     * IS PERSISTENT ACROSS RESTARTS
     * @param playerUUID The UUID of the player in question
     * @param redirects The new set of redirects to apply to the player
     */
    public void setPlayerRedirects(UUID playerUUID, HashMap<String, String> redirects) {
        StorageManager.setPlayerRedirects(playerUUID, redirects);
    }

    /**
     * Clears all current redirects of a player.
     * IS PERSISTENT ACROSS RESTARTS
     * @param playerUUID The UUID of the player in question
     */
    public void clearPlayerRedirects(UUID playerUUID) {
        StorageManager.clearPlayerRedirects(playerUUID);
    }

}
