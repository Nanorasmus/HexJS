package me.nanorasmus.nanodev.hex_js.kubejs.customPatterns;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class CustomPatternCastedEvent extends EventJS {

    private Entity caster;
    private HexPattern pattern;
    private ArrayList<Iota> stack;
    private Iota ravenmind;
    private CastingContext context;
    @HideFromJS
    private CastingHarness harness = null;

    private boolean shouldMishap = false;
    private String mishapMessage = "Mishap message not found!";

    public CustomPatternCastedEvent(Entity caster, HexPattern pattern, ArrayList<Iota> stack, Iota ravenmind, CastingContext context) {
        this.caster = caster;
        this.pattern = pattern;
        this.stack = stack;
        this.ravenmind = ravenmind;
        this.context = context;
    }

    public Entity getCaster() {
        return caster;
    }

    public ArrayList<Iota> getStack() {
        return stack;
    }

    public void setStack(ArrayList<Iota> newStack) {
        stack = newStack;
    }

    public Iota getRavenmind() {
        return ravenmind;
    }

    public void setRavenmind(Iota newRavenmind) {
        ravenmind = newRavenmind;
    }

    public HexPattern getPattern() {
        return pattern;
    }

    public boolean isInAmbit(Vec3d location) {
        return context.isVecInRange(location);
    }

    public void scheduleMishap(String mishapMessage) {
        shouldMishap = true;
        this.mishapMessage = mishapMessage;
    }
    public boolean isMishapping() {
        return shouldMishap;
    }

    public String getMishapMessage() {
        return mishapMessage;
    }

    public int tryConsumeMedia(int media) {
        if (harness == null) {
            harness = IXplatAbstractions.INSTANCE.getHarness((ServerPlayerEntity) caster, context.getCastingHand());
        }

        return harness.withdrawMedia(media, context.getCanOvercast());
    }

    public void finish() throws EventExit {
        success(this);
    }
}
