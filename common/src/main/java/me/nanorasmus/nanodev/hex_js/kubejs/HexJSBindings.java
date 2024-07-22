package me.nanorasmus.nanodev.hex_js.kubejs;

import at.petrak.hexcasting.api.addldata.ADHexHolder;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.ControllerInfo;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.network.MsgNewSpellPatternAck;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;
import me.nanorasmus.nanodev.hex_js.HexJS;
import me.nanorasmus.nanodev.hex_js.helpers.IotaHelper;
import me.nanorasmus.nanodev.hex_js.kubejs.customPatterns.CustomPatternHolder;
import me.nanorasmus.nanodev.hex_js.kubejs.customPatterns.CustomPatternRegistry;
import me.nanorasmus.nanodev.hex_js.storage.StorageManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

import java.util.*;

public class HexJSBindings {

    @Info(
            value = "Registers a custom pattern for handling in HexcastingEvents.registeredPatternCastedEvent()",
            params = {
                    @Param(name = "name", value = "The name of the pattern, can include any characters (String)"),
                    @Param(name = "angles", value = "The angle signature of the pattern to unregister (String)"),
                    @Param(name = "isGreatSpell", value = "Whether the spell is blocked for un-enlightened casters (Boolean)"),
                    @Param(name = "causesBlindDiversion", value = "Whether attempting to cast the spell triggers blind diversion (Boolean)")
            }
    )
    public void registerCustomPattern(String name, String angles, boolean isGreatSpell, boolean causesBlindDiversion) {
        CustomPatternRegistry.registry.put(angles, new CustomPatternHolder(
                name,
                new HexPattern(HexDir.EAST, IotaHelper.anglesFromString(angles)),
                isGreatSpell,
                causesBlindDiversion
        ));
    }

    @Info(
            value = "Unregisters a custom pattern",
            params = {
                    @Param(name = "angles", value = "The angle signature of the pattern to unregister (String)")
            }
    )
    public void unregisterCustomPattern(String angles) {
        CustomPatternRegistry.registry.remove(angles);
    }

    @Info("Unregisters all custom patterns")
    public void clearCustomPatterns() {
        CustomPatternRegistry.registry.clear();
    }

    // -- Player Force Casting --

    @Info(
            value = "Forces a player to cast a series of patterns, this will act as if they cast the patterns themselves",
            params = {
                    @Param(name = "player", value = "The player in question (Entity)"),
                    @Param(name = "patterns", value = "The patterns to cast (List of Strings)")
            }
    )
    public void forceCastPlayerEntity(ServerPlayerEntity player, List<String> patterns) {
        CastingHarness harness = IXplatAbstractions.INSTANCE.getHarness(player, Hand.MAIN_HAND);

        ArrayList<Iota> spell = IotaHelper.patternIotasFromStrings(new ArrayList<>(patterns));

        ControllerInfo clientInfo = harness.executeIotas(spell, player.getWorld());

        if (clientInfo.isStackClear()) {
            IXplatAbstractions.INSTANCE.setHarness(player, null);
            IXplatAbstractions.INSTANCE.setPatterns(player, List.of());
        } else {
            IXplatAbstractions.INSTANCE.setHarness(player, harness);
        }
        IXplatAbstractions.INSTANCE.sendPacketToPlayer(player, new MsgNewSpellPatternAck(clientInfo, 0));
    }

    @Info(
            value = "Forces a player to cast a series of patterns, this will act as if they cast the patterns themselves",
            params = {
                    @Param(name = "uuid", value = "the uuid of the player in question (UUID)"),
                    @Param(name = "patterns", value = "The patterns to cast (List of Strings)")
            }
    )
    public void forceCastPlayerUUID(UUID uuid, List<String> patterns) {
        forceCastPlayerEntity(HexJS.server.getPlayerManager().getPlayer(uuid), patterns);
    }

    @Info(
            value = "Forces a player to cast a series of patterns, this will act as if they cast the patterns themselves",
            params = {
                    @Param(name = "name", value = "the name of the player in question, this is not the DisplayName (String)"),
                    @Param(name = "patterns", value = "The patterns to cast (List of Strings)")
            }
    )
    public void forceCastPlayerName(String name, List<String> patterns) {
        forceCastPlayerEntity(HexJS.server.getPlayerManager().getPlayer(name), patterns);
    }

    // -- Casting Items --
    @HideFromJS
    private ItemStack imbuePatternsFromAngles(Item base, ArrayList<String> patternAngles, int media) {
        ItemStack baseStack = base.getDefaultStack();
        ADHexHolder hexHolder = IXplatAbstractions.INSTANCE.findHexHolder(baseStack);

        List<Iota> patterns = IotaHelper.patternIotasFromStrings(patternAngles);

        hexHolder.writeHex(patterns, media);

        return baseStack;
    }

    @Info(
            value = "Creates a cypher pre-imbued with the specified spell and media",
            params = {
                    @Param(name = "spell", value = "A list of strings representing the patterns of the spell (List of Strings)"),
                    @Param(name = "media", value = "the amount of media to put in the cypher, in units of 1/10,000 of a default dust (int)")
            }
    )
    public ItemStack createCypher(List<String> spell, int media) {
        return imbuePatternsFromAngles(HexItems.CYPHER, new ArrayList<>(spell), media);
    }

    @Info(
            value = "Creates a trinket pre-imbued with the specified spell and media",
            params = {
                    @Param(name = "spell", value = "A list of strings representing the patterns of the spell (List of Strings)"),
                    @Param(name = "media", value = "the amount of media to put in the trinket, in units of 1/10,000 of a default dust (int)")
            }
    )
    public ItemStack createTrinket(List<String> spell, int media) {
        return imbuePatternsFromAngles(HexItems.TRINKET, new ArrayList<>(spell), media);
    }

    @Info(
            value = "Creates an artifact pre-imbued with the specified spell and media",
            params = {
                    @Param(name = "spell", value = "A list of strings representing the patterns of the spell (List of Strings)"),
                    @Param(name = "media", value = "the amount of media to put in the artifact, in units of 1/10,000 of a default dust (int)")
            }
    )
    public ItemStack createArtifact(List<String> spell, int media) {
        return imbuePatternsFromAngles(HexItems.ARTIFACT, new ArrayList<>(spell), media);
    }

    // -- Global --

    @Info("Gets whether the global pattern list is a blacklist or whitelist. False = Blacklist, True = Whitelist")
    public boolean getGlobalPatternListType() {
        return StorageManager.getGlobalIsWhitelist();
    }
    @Info(
            value = "Sets the list type of the global list",
            params = {
                    @Param(name = "isWhitelist", value = "Should the global list be a whitelist? (Boolean)")
            }
    )
    public void setGlobalPatternListType(boolean isWhitelist) {
        StorageManager.setGlobalIsWhitelist(isWhitelist);
    }

    @Info(
            value = "Adds a pattern to the global blacklist/whitelist.",
            params = {
                    @Param(name = "patternAngles", value = "The angle signature of the pattern to add (String)")
            }
    )
    public void addGlobalPattern(String patternAngles) {
        StorageManager.addToGlobalPatternList(new ArrayList<>(Collections.singletonList(patternAngles)));
    }

    @Info(
            value = "Removes a pattern to the global blacklist/whitelist.",
            params = {
                    @Param(name = "patternAngles", value = "The angle signature of the pattern to remove (String)")
            }
    )
    public void removeGlobalPattern(String patternAngles) {
        StorageManager.removeFromGlobalPatternList(patternAngles);
    }

    @Info("Clears the global blacklist/whitelist.")
    public void clearGlobalList() {
        StorageManager.clearGlobalList();
    }


    @Info(
            value = "Adds a global redirect",
            params = {
                    @Param(name = "fromPatternAngles", value = "The angle signature of the pattern to redirect from (String)"),
                    @Param(name = "toPatternAngles", value = "The angle signature of the pattern to redirect to (String)")
            }
    )
    public void addGlobalRedirect(String fromPatternAngles, String toPatternAngles) {
        StorageManager.addGlobalRedirect(fromPatternAngles, toPatternAngles);
    }


    @Info("Gets all global redirects in the form of a Hashmap<fromPatternAngles: String, toPatternAngles: String>.")
    public HashMap<String, String> getGlobalRedirects() {
        return StorageManager.getGlobalRedirects();
    }


    @Info(
            value = "Overrides all global redirects with a new set of redirects.",
            params = {
                    @Param(name = "redirects", value = "The new set of redirects to apply (Hashmap<fromPatternAngles: String, toPatternAngles: String>)")
            }
    )
    public void setGlobalRedirects(HashMap<String, String> redirects) {
        StorageManager.setGlobalRedirects(redirects);
    }

    @Info("Clears all global redirects")
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
