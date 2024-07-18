package me.nanorasmus.nanodev.hex_js.kubejs;

import me.nanorasmus.nanodev.hex_js.storage.StorageManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class HexJSBindings {
    public void sendMessageToPlayer(ServerPlayerEntity player, String message) {
        player.sendMessage(Text.of(message));
    }
    /**
     * Sets whether the pattern list of all new players are blacklists or whitelists
     * Takes in a boolean
     * False = Blacklist, True = Whitelist
     */
    public void setDefaultPatternListType(boolean isWhitelist) {
        StorageManager.setDefaultIsWhitelist(isWhitelist);
    }

    /**
     * Sets whether the pattern list of a player is a blacklist or a whitelist
     * Takes in a UUID and a boolean
     * False = Blacklist, True = Whitelist
     */
    public void setPlayerPatternListType(UUID playerUUID, boolean isWhitelist) {
        StorageManager.setIsPlayerPatternsWhitelist(playerUUID, isWhitelist);
    }

    /**
     * Adds a pattern to the blacklist/whitelist of the specified player
     * Takes in a UUID and a string consisting of angles of the pattern to add, as an example "qaq" is mind's reflection
     */
    public void addPatternToPlayer(UUID playerUUID, String patternAngles) {
        StorageManager.addToPlayerPatternList(playerUUID, new ArrayList<>(Collections.singletonList(patternAngles)));
    }
}
