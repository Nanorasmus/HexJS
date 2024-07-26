package me.nanorasmus.nanodev.hex_js.kubejs;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.iota.*;
import at.petrak.hexcasting.api.spell.math.HexAngle;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import me.nanorasmus.nanodev.hex_js.HexJS;
import me.nanorasmus.nanodev.hex_js.helpers.IotaHelper;
import me.nanorasmus.nanodev.hex_js.kubejs.customIotas.CustomIota;
import me.nanorasmus.nanodev.hex_js.kubejs.customPatterns.CustomPatternCastedEvent;
import me.nanorasmus.nanodev.hex_js.kubejs.customPatterns.CustomPatternRegistry;

public class HexKubeJSPlugin extends KubeJSPlugin {
    public static HexKubeJSPlugin singleton;

    public static EventGroup hexcastingEventGroup = EventGroup.of("HexcastingEvents");

    public static EventHandler patternCastedEventHandler = hexcastingEventGroup.server("registeredPatternCastEvent", () -> CustomPatternCastedEvent.class).hasResult();

    @Override
    public void init() {
        singleton = this;
        PatternRegistry.addSpecialHandler(HexJS.modLoc("custom_pattern"), (HexPattern pattern) -> CustomPatternRegistry.registry.get(pattern.anglesSignature()));
    }

    @Override
    public void registerBindings(BindingsEvent e) {
        e.add("Hexcasting", new HexJSBindings());

        // The event itself
        e.add("registeredPatternCastEvent", CustomPatternCastedEvent.class);

        // Helpers
        e.add("IotaHelper", IotaHelper.class);

        // Iota stuff
        e.add("Iota", Iota.class);
        e.add("BooleanIota", BooleanIota.class);
        e.add("DoubleIota", DoubleIota.class);
        e.add("EntityIota", EntityIota.class);
        e.add("GarbageIota", GarbageIota.class);
        e.add("ListIota", ListIota.class);
        e.add("NullIota", NullIota.class);
        e.add("PatternIota", PatternIota.class);
        e.add("Vec3Iota", Vec3Iota.class);

        e.add("CustomIota", CustomIota.class);

        // Misc hex stuff
        e.add("HexDir", HexDir.class);
        e.add("HexAngle", HexAngle.class);
        e.add("HexPattern", HexPattern.class);
        e.add("MediaConstants", MediaConstants.class);
        e.add("HexIotaTypes", HexIotaTypes.class);
    }

    @Override
    public void registerEvents() {
        hexcastingEventGroup.register();
    }
}
