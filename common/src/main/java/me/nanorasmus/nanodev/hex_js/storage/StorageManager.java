package me.nanorasmus.nanodev.hex_js.storage;


import at.petrak.hexcasting.api.spell.iota.Iota;
import me.nanorasmus.nanodev.hex_js.HexJS;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class StorageManager extends PersistentState {
    public static final String saveId = HexJS.MOD_ID + ".save";
    public static StorageManager INSTANCE;


    public static PatternList defaultPatternList = new PatternList();
    public static HashMap<UUID, PatternList> playerPatternList = new HashMap<>();


    public static void save(MinecraftServer server) {
        server.getOverworld().getPersistentStateManager().set(saveId, INSTANCE);
    }

    public static void load(MinecraftServer server) {
        INSTANCE = server.getOverworld().getPersistentStateManager().getOrCreate(StorageManager::createFromNbt, StorageManager::new, saveId);
    }

    public static PatternList getDefaultPatternList() { return defaultPatternList.copy(); }
    public static void setDefaultPatternList(PatternList override) { defaultPatternList = override; }
    public static void addToDefaultPatternList(ArrayList<String> additions) {
        defaultPatternList.angleSignatureList.addAll(additions);
    }

    public static boolean getDefaultIsWhitelist() { return defaultPatternList.isWhitelist; }
    public static void setDefaultIsWhitelist(boolean isWhitelist) { defaultPatternList.isWhitelist = isWhitelist; }

    public static PatternList getPatternList(UUID playerUUID) {
        return playerPatternList.getOrDefault(playerUUID, getDefaultPatternList());
    }

    public static void setPlayerPatternList(UUID playerUUID, PatternList playerPatterns) {
        playerPatternList.put(playerUUID, playerPatterns);
    }

    public static void addToPlayerPatternList(UUID playerUUID, ArrayList<String> additions) {
        PatternList playerPatterns = getPatternList(playerUUID);

        playerPatterns.angleSignatureList.addAll(additions);

        setPlayerPatternList(playerUUID, playerPatterns);
    }

    public static boolean getIsPlayerPatternsWhitelist(UUID playerUUID) { return getPatternList(playerUUID).isWhitelist; }
    public static void setIsPlayerPatternsWhitelist(UUID playerUUID, boolean isWhitelist) {
        PatternList playerPatterns = getPatternList(playerUUID);

        playerPatterns.isWhitelist = isWhitelist;

        setPlayerPatternList(playerUUID, playerPatterns);
    }

    public static HashMap<String, String> getPlayerRedirects(UUID playerUUID) {
        return getPatternList(playerUUID).redirectListRaw;
    }

    public static void addPlayerRedirect(UUID playerUUID, String input, String output) {
        PatternList playerPatterns = getPatternList(playerUUID);

        playerPatterns.addRedirect(input, output);

        setPlayerPatternList(playerUUID, playerPatterns);
    }

    static void load(NbtCompound nbt) {
        for (int i = 0; i < nbt.getInt("saved_player_count"); i++) {
            // Get the saved patterns
            PatternList playerPatterns = new PatternList(nbt.getBoolean("player_"+i+"_is_whitelist"));
            for (int x = 0; x < nbt.getInt("player_"+i+"_pattern_count"); x++) {
                playerPatterns.angleSignatureList.add(nbt.getString("player_"+i+"_pattern_"+x));
            }

            // Get the saved redirects
            for (int x = 0; x < nbt.getInt("player_"+i+"_redirect_count"); x++) {
                playerPatterns.addRedirect(
                        nbt.getString("player_"+i+"_redirect_"+x+"_input"),
                        nbt.getString("player_"+i+"_redirect_"+x+"_output")
                );
            }

            // Add to list
            playerPatternList.put(
                    nbt.getUuid("player_"+i),
                    playerPatterns
            );
        }
    }

    static NbtCompound save(NbtCompound nbt) {
        nbt.putInt("saved_player_count", playerPatternList.size());

        int i = 0;
        for (UUID key : playerPatternList.keySet()) {
            nbt.putUuid("player_"+i, key);

            // Save patterns
            PatternList playerPatterns = playerPatternList.get(key);
            nbt.putBoolean("player_"+i+"_is_whitelist", playerPatterns.isWhitelist);

            nbt.putInt("player_"+i+"_pattern_count", playerPatterns.angleSignatureList.size());
            for (int x = 0; x < playerPatterns.angleSignatureList.size(); x++) {
                nbt.putString("player_"+i+"_pattern_"+x, playerPatterns.angleSignatureList.get(x));
            }

            // Save redirects
            nbt.putInt("player_"+i+"_redirect_count", playerPatterns.redirectListRaw.size());
            int x = 0;
            for (String input : playerPatterns.redirectListRaw.keySet()) {
                    nbt.putString("player_"+i+"_redirect_"+x+"_input", input);
                    nbt.putString("player_"+i+"_redirect_"+x+"_output", playerPatterns.redirectListRaw.get(input));
                x++;
            }

            i++;
        }

        return nbt;
    }

    public static StorageManager createFromNbt(NbtCompound nbt) {
        StorageManager output = new StorageManager();

        // Load values
        load(nbt);

        // Return
        return output;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        // Save values
        nbt = save(nbt);

        // Return
        return nbt;
    }
}
