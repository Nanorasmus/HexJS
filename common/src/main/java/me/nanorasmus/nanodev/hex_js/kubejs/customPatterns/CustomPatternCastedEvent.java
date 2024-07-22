package me.nanorasmus.nanodev.hex_js.kubejs.customPatterns;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.entity.Entity;

import java.util.ArrayList;

public class CustomPatternCastedEvent extends EventJS {

    private Entity caster;
    private HexPattern pattern;
    private ArrayList<Iota> stack;
    private Iota ravenmind;
    private boolean shouldMishap = false;
    private String mishapMessage = "Mishap message not found!";
    private int cost = 0;

    public CustomPatternCastedEvent(Entity caster, HexPattern pattern, ArrayList<Iota> stack, Iota ravenmind) {
        this.caster = caster;
        this.pattern = pattern;
        this.stack = stack;
        this.ravenmind = ravenmind;
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

    public int getCost() {
        return cost;
    }
    public void setCost(int newCost) {
        cost = newCost;
    }
    public void setCostInDust(double newCost) {
        cost = (int) Math.floor(newCost * MediaConstants.DUST_UNIT);
    }

    public void finish() throws EventExit {
        success(this);
    }
}
