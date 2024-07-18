package me.nanorasmus.nanodev.hex_js.fabric;

import me.nanorasmus.nanodev.hex_js.HexJS;
import net.fabricmc.api.ClientModInitializer;

public class HexJSFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HexJS.initClient();
    }
}
