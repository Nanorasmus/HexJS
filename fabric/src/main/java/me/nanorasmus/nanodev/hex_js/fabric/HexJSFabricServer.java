package me.nanorasmus.nanodev.hex_js.fabric;

import me.nanorasmus.nanodev.hex_js.HexJS;
import net.fabricmc.api.DedicatedServerModInitializer;

public class HexJSFabricServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        HexJS.initServer();
    }
}
