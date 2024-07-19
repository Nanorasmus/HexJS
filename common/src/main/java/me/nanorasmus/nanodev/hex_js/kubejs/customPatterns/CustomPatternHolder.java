package me.nanorasmus.nanodev.hex_js.kubejs.customPatterns;

import at.petrak.hexcasting.api.spell.Action;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.casting.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.mishaps.Mishap;
import dev.latvian.mods.kubejs.event.EventResult;
import me.nanorasmus.nanodev.hex_js.kubejs.HexKubeJSPlugin;
import me.nanorasmus.nanodev.hex_js.kubejs.types.IotaList;
import me.nanorasmus.nanodev.hex_js.kubejs.types.IotaPattern;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomPatternHolder implements Action {

    Text name;
    IotaPattern pattern;
    boolean isGreat;
    boolean causesBlindDiversion;

    public CustomPatternHolder(String name, IotaPattern pattern, boolean isGreat, boolean causesBlindDiversion) {
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
        castingContext.getCaster().sendMessage(Text.of(String.valueOf(stack.size())));
        CustomPatternCastedEvent event = new CustomPatternCastedEvent(
                castingContext.getCaster(),
                pattern,
                new IotaList(stack),
                ravenmind
        );
        castingContext.getCaster().sendMessage(Text.of(String.valueOf(event.getStack().getLength())));
        EventResult result = HexKubeJSPlugin.patternCastedEventHandler.post(event);
        castingContext.getCaster().sendMessage(Text.of(String.valueOf(event.getStack().getLength())));

        ArrayList<OperatorSideEffect> sideEffects = new ArrayList<>();

        if (event.getCost() > 0)
            sideEffects.add(new OperatorSideEffect.ConsumeMedia(event.getCost()));

        if (event.isMishapping())
            sideEffects.add(new OperatorSideEffect.DoMishap(
                    new CustomPatternMishap(event.getMishapMessage()),
                    new Mishap.Context(pattern.toIota().getPattern(), this)
            ));


        return new OperationResult(spellContinuation, event.getStack().toIotaList(), event.getRavenmind(), sideEffects);
    }
}
