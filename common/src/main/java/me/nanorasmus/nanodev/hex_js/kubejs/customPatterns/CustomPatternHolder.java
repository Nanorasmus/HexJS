package me.nanorasmus.nanodev.hex_js.kubejs.customPatterns;

import at.petrak.hexcasting.api.spell.Action;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.casting.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.api.spell.mishaps.Mishap;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.script.ScriptType;
import me.nanorasmus.nanodev.hex_js.kubejs.HexKubeJSPlugin;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomPatternHolder implements Action {

    Text name;
    HexPattern pattern;
    boolean isGreat;
    boolean causesBlindDiversion;

    public CustomPatternHolder(String name, HexPattern pattern, boolean isGreat, boolean causesBlindDiversion) {
        this.name = Text.of(name);
        this.pattern = pattern;
        this.isGreat = isGreat;
        this.causesBlindDiversion = causesBlindDiversion;
    }

    @Override
    public boolean getAlwaysProcessGreatSpell() {
        return false;
    }

    @Override
    public boolean getCausesBlindDiversion() {
        return causesBlindDiversion;
    }

    @NotNull
    @Override
    public Text getDisplayName() {
        return name;
    }

    @Override
    public boolean isGreat() {
        return isGreat;
    }

    @NotNull
    @Override
    public OperationResult operate(@NotNull SpellContinuation spellContinuation, @NotNull List<Iota> stack, @Nullable Iota ravenmind, @NotNull CastingContext castingContext) {
        CustomPatternCastedEvent event = new CustomPatternCastedEvent(
                castingContext.getCaster(),
                pattern,
                new ArrayList<>(stack),
                ravenmind
        );
        EventResult result = HexKubeJSPlugin.patternCastedEventHandler.post(ScriptType.SERVER, event);

        event = (CustomPatternCastedEvent) result.value();

        if (event == null) {
            ArrayList<OperatorSideEffect> sideEffects = new ArrayList<>();

            sideEffects.add(new OperatorSideEffect.DoMishap(
                    new CustomPatternMishap("The HexJS pattern you just cast is missing a call to the \"finish()\" function at the end of the event!"),
                    new Mishap.Context(pattern, this)
            ));

            return new OperationResult(spellContinuation, stack, ravenmind, sideEffects);
        }


        ArrayList<OperatorSideEffect> sideEffects = new ArrayList<>();

        if (event.getCost() > 0)
            sideEffects.add(new OperatorSideEffect.ConsumeMedia(event.getCost()));

        if (event.isMishapping())
            sideEffects.add(new OperatorSideEffect.DoMishap(
                    new CustomPatternMishap(event.getMishapMessage()),
                    new Mishap.Context(pattern, this)
            ));


        return new OperationResult(spellContinuation, event.getStack(), event.getRavenmind(), sideEffects);
    }
}
