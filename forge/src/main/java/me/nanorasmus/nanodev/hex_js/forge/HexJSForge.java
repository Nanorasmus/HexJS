package me.nanorasmus.nanodev.hex_js.forge;

import dev.architectury.platform.forge.EventBuses;
import me.nanorasmus.nanodev.hex_js.HexJS;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Supplier;

@Mod(HexJS.MOD_ID)
public class HexJSForge {
    public HexJSForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(HexJS.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        HexJS.init();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, new Supplier<Runnable>() {
            @Override
            public Runnable get() {
                return HexJS::initClient;
            }
        });
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, new Supplier<Runnable>() {
            @Override
            public Runnable get() {
                return HexJS::initServer;
            }
        });
    }
}