package me.nanorasmus.nanodev.hex_js.mixin;

import at.petrak.hexcasting.api.spell.math.HexAngle;
import at.petrak.hexcasting.api.spell.math.HexDir;
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

import java.util.ArrayList;

@Mixin(MsgNewSpellPatternSyn.class)
public class SpellPatternBlockerC2SMixin {

    @Mutable @Shadow(remap = false) @Final
    private HexPattern pattern;

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    public void handle(MinecraftServer server, ServerPlayerEntity sender, CallbackInfo ci) {
        PatternList playerPatternList = StorageManager.getPatternList(sender.getUuid());
        PatternList globalPatternList = StorageManager.getGlobalPatternList();

        String signature = pattern.anglesSignature();

        // Handle number literals
        if (signature.startsWith("aqaa")) {
            // If the base positive number literal is blocked by HexJS
            if (
                    playerPatternList.blocks("aqaa") || (!playerPatternList.contains("aqaa") && globalPatternList.blocks("aqaa"))
            ) {
                // Cancel the event
                sender.sendMessage(Text.of("A strange force is prohibiting me from forming this pattern"));
                ci.cancel();
            }
        } else if (signature.startsWith("dedd")) {
            // If the base negative number literal is blocked by HexJS
            if (
                    playerPatternList.blocks("dedd") || (!playerPatternList.contains("dedd") && globalPatternList.blocks("dedd"))
            ) {
                // Cancel the event
                sender.sendMessage(Text.of("A strange force is prohibiting me from forming this pattern"));
                ci.cancel();
            }
        } else {

            // Handle Bookkeeper's Gambit
            boolean isBookKeepers = true;
            int bookKeeperLength = 0;

            ArrayList<HexDir> directions = new ArrayList<>(pattern.directions());
            HexDir flatDir = pattern.getStartDir();
            if (!pattern.getAngles().isEmpty() && pattern.getAngles().get(0) == HexAngle.LEFT_BACK) {
                flatDir = directions.get(0).rotatedBy(HexAngle.LEFT);
            }

            for (int i = 0; i < directions.size(); i++) {
                // Angle with respect to the *start direction*
                var angle = directions.get(i).angleFrom(flatDir);
                if (angle == HexAngle.FORWARD) {
                    bookKeeperLength++;
                    continue;
                }
                if (i >= directions.size() - 1) {
                    // then we're out of angles!
                    isBookKeepers = false;
                    break;
                }
                var angle2 = directions.get(i + 1).angleFrom(flatDir);
                if (angle == HexAngle.RIGHT && angle2 == HexAngle.LEFT) {
                    bookKeeperLength++;
                    i++;
                    continue;
                }

                isBookKeepers = false;
                break;
            }

            if (isBookKeepers) {
                // Check if a set bookkeeper's length is prohibiting the cast, per-player length having priority
                if (
                        (playerPatternList.maxBookKeepersLength != -1 && playerPatternList.maxBookKeepersLength < bookKeeperLength)
                        || (playerPatternList.maxBookKeepersLength == -1 && globalPatternList.maxBookKeepersLength != -1 && globalPatternList.maxBookKeepersLength < bookKeeperLength)
                ) {
                    // Cancel the event
                    sender.sendMessage(Text.of("A strange force is prohibiting me from forming a Bookkeeper's Gambit this long"));
                    ci.cancel();
                }
            } else {

                // -- Normal pattern logic --

                // If it is blocked by HexJS
                if (playerPatternList.blocks(pattern) || (!playerPatternList.contains(pattern) && globalPatternList.blocks(pattern))) {
                    // Cancel the event
                    sender.sendMessage(Text.of("A strange force is prohibiting me from forming this pattern"));
                    ci.cancel();
                }
                HexPattern playerRedirect = playerPatternList.handleRedirects(pattern);
                HexPattern globalRedirect = globalPatternList.handleRedirects(pattern);
                if (playerRedirect != null) {
                    pattern = playerRedirect;
                } else if (globalRedirect != null) {
                    pattern = globalRedirect;
                }
            }
        }

    }
}
