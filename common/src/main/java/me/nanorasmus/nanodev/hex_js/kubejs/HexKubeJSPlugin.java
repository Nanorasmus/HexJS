package me.nanorasmus.nanodev.hex_js.kubejs;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import me.nanorasmus.nanodev.hex_js.HexJS;
import me.nanorasmus.nanodev.hex_js.kubejs.customPatterns.CustomPatternCastedEvent;
import me.nanorasmus.nanodev.hex_js.kubejs.customPatterns.CustomPatternRegistry;

public class HexKubeJSPlugin extends KubeJSPlugin {
    public static HexKubeJSPlugin singleton;

    public static EventGroup hexcastingEventGroup = EventGroup.of("HexcastingEvents");

    public static EventHandler patternCastedEventHandler = hexcastingEventGroup.server("patternCasted", () -> CustomPatternCastedEvent.class);

    @Override
    public void init() {
        singleton = this;
        PatternRegistry.addSpecialHandler(HexJS.modLoc("custom_pattern"), (HexPattern pattern) -> CustomPatternRegistry.registry.getOrDefault(pattern.anglesSignature(), null));
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
