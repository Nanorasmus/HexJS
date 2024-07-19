package me.nanorasmus.nanodev.hex_js.kubejs.customPatterns;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.mishaps.Mishap;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomPatternMishap extends Mishap {
    private final String message;

    public CustomPatternMishap(String message) {
        this.message = message;
    }

    @NotNull
    @Override
    public FrozenColorizer accentColor(@NotNull CastingContext castingContext, @NotNull Mishap.Context context) {
        return dyeColor(DyeColor.RED);
    }

    @NotNull
    @Override
    public Text errorMessage(@NotNull CastingContext castingContext, @NotNull Mishap.Context context) {
        return Text.of(message);
    }

    @Override
    public void execute(@NotNull CastingContext castingContext, @NotNull Mishap.Context context, @NotNull List<Iota> list) {

    }
}
