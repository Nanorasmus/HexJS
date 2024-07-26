package me.nanorasmus.nanodev.hex_js.kubejs.customIotas;

import at.petrak.hexcasting.api.spell.iota.Iota;
import kotlin.jvm.functions.Function2;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public class CustomIotaEntry {
    // Basic
    public String identifier;

    // Constructor
    public Consumer<CustomIota> init;

    // Comparisons
    public Function<CustomIota, Boolean> isTruthy = (CustomIota iota) -> true;
    public Function2<CustomIota, Iota, Boolean> toleratesOther = (CustomIota me, Iota other) -> false;

    // Serialization
    public Function<CustomIota, NbtCompound> serialize = (CustomIota iota) -> iota.persistentData;
    public Function2<CustomIota, World, CustomIota> deserialize = (CustomIota iota, World level) -> iota;

    public CustomIotaEntry(
            @NotNull String identifier,
            @NotNull Consumer<CustomIota> init,
            @Nullable Function<CustomIota, Boolean> isTruthy,
            @Nullable Function2<CustomIota, Iota, Boolean> toleratesOther,
            @Nullable Function<CustomIota, NbtCompound> serialize,
            @Nullable Function2<CustomIota, World, CustomIota> deserialize
    ) {
        this.identifier = identifier;
        this.init = init;

        if (isTruthy != null) {
            this.isTruthy = isTruthy;
        }
        if (toleratesOther != null) {
            this.toleratesOther = toleratesOther;
        }
        if (serialize != null) {
            this.serialize = serialize;
        }
        if (deserialize != null) {
            this.deserialize = deserialize;
        }
    }

    // Boilerplate entry that should only be used if an error has occurred to prevent a crash
    public CustomIotaEntry() {
        identifier = "ERR";

        init = (CustomIota iota) -> {};
    }
}
