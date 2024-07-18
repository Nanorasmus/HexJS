package me.nanorasmus.nanodev.hex_js.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.player.KubeJSPlayerEventHandler;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class HexKubeJSPlugin extends KubeJSPlugin {
    @Override
    public void registerBindings(BindingsEvent e) {
        e.add("hex_js", new HexJSBindings());
    }

    @Override
    public void registerEvents() {
    }
}
