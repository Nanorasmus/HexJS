package me.nanorasmus.nanodev.hex_js.mixin;

import at.petrak.hexcasting.api.spell.Action;
import at.petrak.hexcasting.api.spell.SpellList;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.ControllerInfo;
import at.petrak.hexcasting.api.spell.casting.ResolvedPatternType;
import at.petrak.hexcasting.api.spell.casting.eval.ContinuationFrame;
import at.petrak.hexcasting.api.spell.casting.eval.FrameEvaluate;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.casting.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.api.spell.mishaps.Mishap;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import kotlin.Triple;
import me.nanorasmus.nanodev.hex_js.misc.JavaMishapThrower;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(CastingHarness.class)
public class PatternCostModifierMixin {
    @Shadow(remap = false)
    private List<Iota> stack;
    @Final
    @Shadow(remap = false)
    private CastingContext ctx;
    @Shadow(remap = false)
    private List<Iota> parenthesized;
    @Shadow(remap = false)
    private Iota ravenmind;
    @Shadow(remap = false)
    private int parenCount;

    @Shadow(remap = false)
    private HexPattern getPatternForFrame(ContinuationFrame frame) {
        return null;
    }

    @Shadow(remap = false)
    private Action getOperatorForFrame(ContinuationFrame frame, ServerWorld world) {
        return null;
    }

    @Shadow(remap = false)
    public void performSideEffects(@NotNull CastingHarness.TempControllerInfo info, @NotNull List<? extends OperatorSideEffect> sideEffects) {

    }

    @Shadow(remap = false)
    private boolean escapeNext;

    // Literal translated kotlin code from hex to completely override the function with my own snippets here and there
    @Inject(method = "executeIotas", at = @At("HEAD"), cancellable = true)
    public void executeIotas(List<? extends Iota> iotas, ServerWorld world, CallbackInfoReturnable<ControllerInfo> cir) {
        world.getPlayers().forEach((p) -> p.sendMessage(Text.of("You suck")));
        // Initialize the continuation stack to a single top-level eval for all iotas.
        var continuation = SpellContinuation.Done.INSTANCE.pushFrame(new FrameEvaluate(new SpellList.LList(0, iotas), false));
        // Begin aggregating info
        var info = new CastingHarness.TempControllerInfo(false);
        var lastResolutionType = ResolvedPatternType.UNRESOLVED;
        var sound = HexEvalSounds.NOTHING;
        while (continuation instanceof SpellContinuation.NotDone && !info.getEarlyExit()) {
            // Take the top of the continuation stack...
            var next = ((SpellContinuation.NotDone) continuation).getFrame();
            // ...and execute it.
            CastingHarness.CastResult result;
            try {
                result = JavaMishapThrower.evaluate(next, continuation, world, (CastingHarness) (Object) this);
            } catch (Mishap mishap) {
                var pattern = this.getPatternForFrame(next);
                Action operator;
                try {
                    operator = this.getOperatorForFrame(next, world);
                } catch (Throwable ignored) {
                    operator = null;
                }
                result = new CastingHarness.CastResult(
                        continuation,
                        null,
                        mishap.resolutionType(ctx),
                        Arrays.asList(
                                new OperatorSideEffect.DoMishap(
                                        mishap,
                                        new Mishap.Context(pattern == null ? new HexPattern(HexDir.WEST, new ArrayList<>()) : pattern, operator)
                                )
                        ),
                    HexEvalSounds.MISHAP
                );
            }
            // Then write all pertinent data back to the harness for the next iteration.
            if (result.getNewData() != null) {
                ((CastingHarness) (Object) this).applyFunctionalData(result.getNewData());
            }
            continuation = result.getContinuation();
            lastResolutionType = result.getResolutionType();

                    // DO THE THING NANO

            this.performSideEffects(info, result.getSideEffects());
            info.setEarlyExit(info.getEarlyExit() || !lastResolutionType.getSuccess());
            if (result.getSound() == HexEvalSounds.MISHAP) {
                sound = HexEvalSounds.MISHAP;
            } else {
                sound = sound.greaterOf(result.getSound());
            }
        }

        if (sound != null) {
            ((CastingHarness) (Object) this).getCtx().getWorld().playSound(
                    null, this.ctx.getPosition().x, this.ctx.getPosition().y, this.ctx.getPosition().z, sound.sound(),
                    SoundCategory.PLAYERS, 1f, 1f
            );

            this.ctx.getWorld().emitGameEvent(this.ctx.getCaster().getCameraEntity(), GameEvent.ITEM_INTERACT_FINISH, this.ctx.getPosition());
        }

        if (continuation instanceof SpellContinuation.NotDone) {

            if (lastResolutionType.getSuccess())
                lastResolutionType = ResolvedPatternType.EVALUATED;
            else
                lastResolutionType = ResolvedPatternType.ERRORED;
        }

        //(stackDescs, parenDescs, ravenmind) = generateDescs();
        Triple<List<NbtCompound>, List<NbtCompound>, NbtCompound> descs = hexJS$generateDescsCopy();

        cir.setReturnValue(new ControllerInfo(
                this.stack.isEmpty() && this.parenCount == 0 && !this.escapeNext,
                lastResolutionType,
                descs.getFirst(),
                descs.getSecond(),
                descs.getThird(),
                this.parenCount
        ));
        cir.cancel();
    }

    @Unique
    private Triple<List<NbtCompound>, List<NbtCompound>, NbtCompound> hexJS$generateDescsCopy() {
        return new Triple<>(
                stack == null || stack.isEmpty() ? new ArrayList<>() : stack.stream().map(HexIotaTypes::serialize).toList(),
                parenthesized == null || parenthesized.isEmpty() ? new ArrayList<>() : parenthesized.stream().map(HexIotaTypes::serialize).toList(),
                ravenmind == null ? null : HexIotaTypes.serialize(ravenmind)
        );
    }
}
