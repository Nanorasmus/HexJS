package me.nanorasmus.nanodev.hex_js.mixin;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import me.nanorasmus.nanodev.hex_js.storage.StorageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CastingHarness.class)
public class ForcedCastMediaDrawMixin {

    // Ordinals of booleans in withdrawMedia
    // 0 = allowOvercast
    // 1 = isInCreative
    // 2 = Correct? No idea how seeing as the original value doesn't seem to change when doing it normally

    @ModifyVariable(
            method = "withdrawMedia",
            at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.isIn (Lnet/minecraft/tag/TagKey;)Z"), ordinal = 2)
    private boolean allowForcedCastMediaDraw(boolean original){
        CastingContext ctx = ((CastingHarness)(Object)this).getCtx();
        if (StorageManager.currentlyForcedPlayers.contains(ctx.getCaster().getUuid())) {
            return true;
        }
        return original;
    }

}
