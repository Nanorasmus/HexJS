package me.nanorasmus.nanodev.hex_js;

import at.petrak.hexcasting.api.PatternRegistry;
import dev.architectury.event.events.common.LifecycleEvent;
import me.nanorasmus.nanodev.hex_js.storage.StorageManager;
import net.minecraft.server.MinecraftServer;


public class HexJS
{
	public static final String MOD_ID = "hex_js";

	public static void init() {
		LifecycleEvent.SERVER_STARTING.register((MinecraftServer server) -> {
			StorageManager.load(server);
		});
		LifecycleEvent.SERVER_STOPPING.register((MinecraftServer server) -> {
			StorageManager.save(server);
		});
	}

	public static Runnable initClient() {
        return null;
    }

	public static Runnable initServer() {
		return null;
	}
}
