package me.nanorasmus.nanodev.hex_js.kubejs.customPatterns;

import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import dev.latvian.mods.kubejs.event.EventJS;
import me.nanorasmus.nanodev.hex_js.kubejs.types.IotaList;
import me.nanorasmus.nanodev.hex_js.kubejs.types.IotaPattern;
import net.minecraft.entity.Entity;

import java.awt.*;

public class CustomPatternCastedEvent extends EventJS {

    private Entity caster;
    private IotaPattern pattern;
    private IotaList stack;
    private Iota ravenmind;
    private boolean shouldMishap = false;
    private String mishapMessage = "Mishap message not found!";
    private int cost = 0;

    public CustomPatternCastedEvent(Entity caster, IotaPattern pattern, IotaList stack, Iota ravenmind) {
        this.caster = caster;
        this.pattern = pattern;
        this.stack = stack;
        this.ravenmind = ravenmind;
    }

    public Entity getCaster() {
        return caster;
    }

    public IotaList getStack() {
        return stack;
    }

    public Iota getRavenmind() {
        return ravenmind;
    }

    public IotaPattern getPattern() {
        return pattern;
    }

    public void mishap(String mishapMessage) {
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
}
