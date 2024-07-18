package me.nanorasmus.nanodev.hex_js.mixin;

import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.network.MsgNewSpellPatternSyn;
import me.nanorasmus.nanodev.hex_js.storage.PatternList;
import me.nanorasmus.nanodev.hex_js.storage.StorageManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MsgNewSpellPatternSyn.class)
public class SpellPatternBlockerC2SMixin {

    @Mutable @Shadow(remap = false) @Final
    private HexPattern pattern;

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    public void handle(MinecraftServer server, ServerPlayerEntity sender, CallbackInfo ci) {
        PatternList playerPatternList = StorageManager.getPatternList(sender.getUuid());
        // If it is blocked by HexJS
        if (playerPatternList.blocks(pattern)) {
            // Cancel the event
            sender.sendMessage(Text.of("A strange force is prohibiting me from forming this pattern clearly"));
            ci.cancel();
        }
        pattern = playerPatternList.handleRedirects(pattern);
    }
}
