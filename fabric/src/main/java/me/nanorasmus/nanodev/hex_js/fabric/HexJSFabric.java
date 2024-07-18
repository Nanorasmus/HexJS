package me.nanorasmus.nanodev.hex_js.fabric;

import me.nanorasmus.nanodev.hex_js.HexJS;
import net.fabricmc.api.ModInitializer;

public class HexJSFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        HexJS.init();
    }
}