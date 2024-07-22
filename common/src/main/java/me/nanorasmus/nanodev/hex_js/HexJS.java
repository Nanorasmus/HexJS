package me.nanorasmus.nanodev.hex_js;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import me.nanorasmus.nanodev.hex_js.storage.StorageManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.util.logging.Logger;


public class HexJS
{
	public static final String MOD_ID = "hex_js";
	public static final Logger LOGGER = Logger.getLogger("HexJS");
	public static MinecraftServer server;

	public static void init() {
		LifecycleEvent.SERVER_STARTING.register(StorageManager::load);
		LifecycleEvent.SERVER_STOPPING.register(StorageManager::save);
		TickEvent.SERVER_PRE.register((MinecraftServer server) -> HexJS.server = server);
	}

	public static Runnable initClient() {
        return null;
    }

	public static Runnable initServer() {
		return null;
	}

	public static Identifier modLoc(String key) {
		return new Identifier(MOD_ID, key);
	}
}
