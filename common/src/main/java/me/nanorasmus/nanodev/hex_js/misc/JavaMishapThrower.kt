package me.nanorasmus.nanodev.hex_js.misc

import at.petrak.hexcasting.api.spell.casting.CastingHarness
import at.petrak.hexcasting.api.spell.casting.eval.ContinuationFrame
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import net.minecraft.server.world.ServerWorld
import kotlin.jvm.Throws


/**
 * kotlin doesn't check its exceptions so code extending kotlin compiled stuff can't actually properly throw exceptions
 * This has been stolen straight from Hex Gloop
 */
class JavaMishapThrower {
    companion object {
        @JvmStatic
        @Throws(Mishap::class)
        fun evaluate(next: ContinuationFrame, continuation: SpellContinuation, world: ServerWorld, harness: CastingHarness): CastingHarness.CastResult {
            try {
                return next.evaluate((continuation as SpellContinuation.NotDone).next, world, harness);
            } catch (mishap: Mishap) {
                throw mishap;
            }
        }
    }
}