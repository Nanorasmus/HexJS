package me.nanorasmus.nanodev.hex_js;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.latvian.mods.rhino.Context;
import me.nanorasmus.nanodev.hex_js.kubejs.customIotas.CustomIota;
import me.nanorasmus.nanodev.hex_js.storage.StorageManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.logging.Logger;


public class HexJS
{
	public static final String MOD_ID = "hex_js";
	public static final Logger LOGGER = Logger.getLogger("HexJS");
	public static MinecraftServer server;

	public static Context ctx;

	public static void init() {
		LifecycleEvent.SERVER_STARTING.register(StorageManager::load);
		LifecycleEvent.SERVER_STOPPING.register(StorageManager::save);
		TickEvent.SERVER_PRE.register((MinecraftServer server) -> HexJS.server = server);

		Registry.register(HexIotaTypes.REGISTRY, "js_custom_iota", CustomIota.TYPE);
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
