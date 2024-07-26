package me.nanorasmus.nanodev.hex_js.kubejs;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.addldata.ADHexHolder;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.ControllerInfo;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.items.ItemScroll;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.network.MsgNewSpellPatternAck;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.datafixers.util.Pair;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;
import kotlin.jvm.functions.Function2;
import me.nanorasmus.nanodev.hex_js.HexJS;
import me.nanorasmus.nanodev.hex_js.helpers.IotaHelper;
import me.nanorasmus.nanodev.hex_js.kubejs.customIotas.CustomIota;
import me.nanorasmus.nanodev.hex_js.kubejs.customIotas.CustomIotaEntry;
import me.nanorasmus.nanodev.hex_js.kubejs.customIotas.CustomIotaRegistry;
import me.nanorasmus.nanodev.hex_js.kubejs.customPatterns.CustomPatternHolder;
import me.nanorasmus.nanodev.hex_js.kubejs.customPatterns.CustomPatternRegistry;
import me.nanorasmus.nanodev.hex_js.storage.StorageManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class HexJSBindings {

    // Get info from Hex

    @Info(
            value = "Gets the angle signature of a great spell, or an empty string if not found",
            params = {
                    @Param(name = "greatSpellID", value = "The resource ID of the great spell")
            }
    )
    public String getGreatSpellAngles(String greatSpellID) {
        Map<String, Pair<Identifier, HexDir>> perWorldPatterns = PatternRegistry.getPerWorldPatterns(HexJS.server.getOverworld());

        for (String angles : perWorldPatterns.keySet()) {
            if (greatSpellID.equals(perWorldPatterns.get(angles).getFirst().toString())) return angles;
        }

        return "";
    }

    // Custom Iotas
    @Info(
            value = "Registers a custom iota type",
            params = {
                    @Param(name = "identifier", value = "The ID of the iota type"),
                    @Param(name = "init", value = ""),
                    @Param(name = "isTruthy", value = ""),
                    @Param(name = "toleratesOther", value = ""),
                    @Param(name = "serialize", value = ""),
                    @Param(name = "deserialize", value = "")
            }
    )
    public void registerCustomIota(
            @NotNull String identifier,
            @NotNull Consumer<CustomIota> init,
            @Nullable Function<CustomIota, Boolean> isTruthy,
            @Nullable Function2<CustomIota, Iota, Boolean> toleratesOther,
            @Nullable Function<CustomIota, NbtCompound> serialize,
            @Nullable Function2<CustomIota, World, CustomIota> deserialize
    ) {
        CustomIotaRegistry.customIotas.put(identifier, new CustomIotaEntry(
                identifier,
                init,
                isTruthy,
                toleratesOther,
                serialize,
                deserialize
        ));
    }

    // Custom Patterns
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
        // Give the player an override to the staff requirement
        StorageManager.currentlyForcedPlayers.add(player.getUuid());

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

        // Remove the override
        StorageManager.currentlyForcedPlayers.remove(player.getUuid());
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

    // -- Scrolls --

    @HideFromJS
    public NbtCompound createScrollTags(String scrollID, HexDir startDir, String patternAngles) {
        NbtCompound tag = new NbtCompound();

        tag.putString(ItemScroll.TAG_OP_ID, scrollID);
        tag.put(ItemScroll.TAG_PATTERN, new HexPattern(startDir, IotaHelper.anglesFromString(patternAngles)).serializeToNBT());

        return tag;
    }

    @Info(
            value = "Creates a small scroll pre-imbued with the specified pattern",
            params = {
                    @Param(name = "scrollName", value = "The desired name of the final scroll"),
                    @Param(name = "startDir", value = "The direction of the starting stroke of the pattern"),
                    @Param(name = "patternAngles", value = "The angle signature of the pattern to imbue")
            }
    )
    public ItemStack createSmallScroll(String scrollName, HexDir startDir, String patternAngles) {
        ItemStack scroll = new ItemStack(HexItems.SCROLL_SMOL);
        
        scroll.setNbt(createScrollTags(scrollName, startDir, patternAngles));
        scroll.setCustomName(Text.of(scrollName));

        return scroll;
    }

    @Info(
            value = "Creates a medium scroll pre-imbued with the specified pattern",
            params = {
                    @Param(name = "scrollName", value = "The desired name of the final scroll"),
                    @Param(name = "startDir", value = "The direction of the starting stroke of the pattern"),
                    @Param(name = "patternAngles", value = "The angle signature of the pattern to imbue")
            }
    )
    public ItemStack createMediumScroll(String scrollName, HexDir startDir, String patternAngles) {
        ItemStack scroll = new ItemStack(HexItems.SCROLL_MEDIUM);
        
        scroll.setNbt(createScrollTags(scrollName, startDir, patternAngles));
        scroll.setCustomName(Text.of(scrollName));

        return scroll;
    }

    @Info(
            value = "Creates a large scroll pre-imbued with the specified pattern",
            params = {
                    @Param(name = "scrollName", value = "The desired name of the final scroll"),
                    @Param(name = "startDir", value = "The direction of the starting stroke of the pattern"),
                    @Param(name = "patternAngles", value = "The angle signature of the pattern to imbue")
            }
    )
    public ItemStack createLargeScroll(String scrollName, HexDir startDir, String patternAngles) {
        ItemStack scroll = new ItemStack(HexItems.SCROLL_LARGE);

        scroll.setNbt(createScrollTags(scrollName, startDir, patternAngles));
        scroll.setCustomName(Text.of(scrollName));

        return scroll;
    }

    @Info(
            value = "Creates an ancient scroll pre-imbued with the specified pattern",
            params = {
                    @Param(name = "scrollID", value = "The registered language entry ID of the pattern name under the 'hexcasting.spell.minecraft:' category"),
                    @Param(name = "startDir", value = "The direction of the starting stroke of the pattern"),
                    @Param(name = "patternAngles", value = "The angle signature of the pattern to imbue")
            }
    )
    public ItemStack createAncientScroll(String scrollID, HexDir startDir, String patternAngles) {
        ItemStack scroll = new ItemStack(HexItems.SCROLL_LARGE)
                ;
        scroll.setNbt(createScrollTags(scrollID, startDir, patternAngles));

        return scroll;
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
            value = "Adds a list of patterns to the global blacklist/whitelist.",
            params = {
                    @Param(name = "patterns", value = "The list of the angle signatures of the patterns to add (List<String>)")
            }
    )
    public void addGlobalPatterns(List<String> patterns) {
        StorageManager.addToGlobalPatternList(new ArrayList<>(patterns));
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

    @Info(
            value = "Sets the global maximum iota length of a Bookkeeper's Gambit",
            params = {
                    @Param(name = "newLength", value = "The desired maximum length of Bookkeeper's Gambit")
            }
    )
    public void setGlobalMaxBookkeeperLength(int newLength) {
        StorageManager.defaultPatternList.maxBookKeepersLength = newLength;
    }

    @Info("Gets the global maximum iota length of a Bookkeeper's Gambit")
    public int getGlobalMaxBookkeeperLength() {
        return StorageManager.defaultPatternList.maxBookKeepersLength;
    }

    @Info("Clears the global maximum iota length of a Bookkeeper's Gambit")
    public void clearGlobalMaxBookkeeperLength() {
        StorageManager.defaultPatternList.maxBookKeepersLength = -1;
    }


    // -- Per-player --

    @Info(
            value = "Sets whether the pattern list of a player is a blacklist or a whitelist. IS PERSISTENT ACROSS RESTARTS",
            params = {
                    @Param(name = "playerUUID", value = "The UUID of the player in question"),
                    @Param(name = "isWhitelist", value = "The desired list type. False = Blacklist, True = Whitelist.")
            }
    )
    public void setPlayerPatternListType(UUID playerUUID, boolean isWhitelist) {
        StorageManager.setIsPlayerPatternsWhitelist(playerUUID, isWhitelist);
    }

    @Info(
            value = "Gets whether the pattern list of a player is a blacklist or a whitelist. False = Blacklist, True = Whitelist",
            params = {
                    @Param(name = "playerUUID", value = "The UUID of the player in question")
            }
    )
    public boolean getPlayerPatternListType(UUID playerUUID) {
        return StorageManager.getIsPlayerPatternsWhitelist(playerUUID);
    }

    @Info(
            value = "Adds a pattern to the blacklist/whitelist of the specified player. IS PERSISTENT ACROSS RESTARTS",
            params = {
                    @Param(name = "playerUUID", value = "The UUID of the player in question"),
                    @Param(name = "patternAngles", value = "A string consisting of angles of the pattern to add, as an example \"qaq\" is mind's reflection")
            }
    )
    public void addPatternToPlayer(UUID playerUUID, String patternAngles) {
        StorageManager.addToPlayerPatternList(playerUUID, new ArrayList<>(Collections.singletonList(patternAngles)));
    }

    @Info(
            value = "Removes a pattern to the blacklist/whitelist of the specified player. IS PERSISTENT ACROSS RESTARTS",
            params = {
                    @Param(name = "playerUUID", value = "The UUID of the player in question"),
                    @Param(name = "patternAngles", value = "A string consisting of angles of the pattern to remove, as an example \"qaq\" is mind's reflection")
            }
    )
    public void removePatternFromPlayer(UUID playerUUID, String patternAngles) {
        StorageManager.removeFromPlayerPatternList(playerUUID, patternAngles);
    }

    @Info(
            value = "Clears the blacklist/whitelist of a player. IS PERSISTENT ACROSS RESTARTS",
            params = {
                    @Param(name = "playerUUID", value = "The UUID of the player in question")
            }
    )
    public void clearPlayerList(UUID playerUUID) {
        StorageManager.clearPlayerPatternList(playerUUID);
    }

    @Info(
            value = "Adds a redirect to specified player. IS PERSISTENT ACROSS RESTARTS",
            params = {
                    @Param(name = "playerUUID", value = "The UUID of the player in question"),
                    @Param(name = "fromPatternAngles", value = "A string consisting of angles of the pattern to redirect from, as an example \"qaq\" is mind's reflection"),
                    @Param(name = "toPatternAngles", value = "A string consisting of angles of the pattern to redirect to, as an example \"qaq\" is mind's reflection")
            }
    )
    public void addRedirectToPlayer(UUID playerUUID, String fromPatternAngles, String toPatternAngles) {
        StorageManager.addPlayerRedirect(playerUUID, fromPatternAngles, toPatternAngles);
    }

    @Info(
            value = "Gets all redirects of a player in the form of a HashMap(fromPatternAngles, toPatternAngles)",
            params = {
                    @Param(name = "playerUUID", value = "The UUID of the player in question")
            }
    )
    public HashMap<String, String> getPlayerRedirects(UUID playerUUID) {
        return StorageManager.getPlayerRedirects(playerUUID);
    }

    @Info(
            value = "Sets all redirects of a player. IS PERSISTENT ACROSS RESTARTS",
            params = {
                    @Param(name = "playerUUID", value = "The UUID of the player in question"),
                    @Param(name = "redirects", value = "The new redirects in the form of a Hashmap(fromPatternAngles, toPatternAngles)")
            }
    )
    public void setPlayerRedirects(UUID playerUUID, HashMap<String, String> redirects) {
        StorageManager.setPlayerRedirects(playerUUID, redirects);
    }

    @Info(
            value = "Clears all current redirects of a player. IS PERSISTENT ACROSS RESTARTS",
            params = {
                    @Param(name = "playerUUID", value = "The UUID of the player in question")
            }
    )
    public void clearPlayerRedirects(UUID playerUUID) {
        StorageManager.clearPlayerRedirects(playerUUID);
    }

    @Info(
            value = "Sets a player's maximum iota length of a Bookkeeper's Gambit",
            params = {
                    @Param(name = "newLength", value = "The desired maximum length of Bookkeeper's Gambit")
            }
    )
    public void setPlayerMaxBookkeeperLength(UUID playerUUID, int newLength) {
        StorageManager.setPlayerMaxBookkeeperLength(playerUUID, newLength);
    }

    @Info("Gets a player's maximum iota length of a Bookkeeper's Gambit")
    public int getPlayerMaxBookkeeperLength(UUID playerUUID) {
        return StorageManager.getPlayerMaxBookkeeperLength(playerUUID);
    }

    @Info("Clears a player's maximum iota length of a Bookkeeper's Gambit")
    public void clearPlayerMaxBookkeeperLength(UUID playerUUID) {
        StorageManager.clearPlayerMaxBookkeeperLength(playerUUID);
    }

}
