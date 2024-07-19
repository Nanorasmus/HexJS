package me.nanorasmus.nanodev.hex_js.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import me.nanorasmus.nanodev.hex_js.kubejs.customPatterns.CustomPatternCastedEvent;

public class HexKubeJSPlugin extends KubeJSPlugin {
    public static HexKubeJSPlugin singleton;

    public EventGroup hexcastingEventGroup = EventGroup.of("HexcastingEvents");

    public EventHandler patternCastedEventHandler = hexcastingEventGroup.server("patternCasted", () -> CustomPatternCastedEvent.class);

    @Override
    public void init() {
        singleton = this;
    }

    @Override
    public void registerBindings(BindingsEvent e) {
        e.add("hexcasting", new HexJSBindings());
    }

    @Override
    public void registerEvents() {
        hexcastingEventGroup.register();
    }
}
