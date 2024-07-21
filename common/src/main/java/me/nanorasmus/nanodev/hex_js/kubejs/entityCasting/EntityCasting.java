package me.nanorasmus.nanodev.hex_js.kubejs.entityCasting;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.ControllerInfo;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.network.MsgNewSpellPatternAck;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.authlib.GameProfile;
import me.nanorasmus.nanodev.hex_js.HexJS;
import me.nanorasmus.nanodev.hex_js.helpers.IotaHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.source.BiomeAccess;

import java.util.*;

import static me.nanorasmus.nanodev.hex_js.HexJS.server;

public class EntityCasting {
    private static ServerPlayerEntity secretCaster;
    private static HashMap<UUID, CastingHarness> casterEntities = new HashMap<>();

    public static void init(MinecraftServer server) {
        secretCaster = server.getPlayerManager().createPlayer(
                new GameProfile(UUID.fromString("8233774a-a3f3-44b8-b9ba-f9e82966b2dc"), "HexJS"),
                null
        );
        secretCaster.getInventory().setStack(secretCaster.getInventory().selectedSlot, HexItems.STAFF_OAK.getDefaultStack());
        secretCaster.giveItemStack(HexItems.CREATIVE_UNLOCKER.getDefaultStack());
    }

    public static void registerEntityAsCaster(Entity entity) {
        casterEntities.put(
            entity.getUuid(),
            new CastingHarness(
                new CastingContext(
                        secretCaster,
                        Hand.MAIN_HAND,
                        CastingContext.CastSource.STAFF
                ),
                FrozenColorizer.DEFAULT.get()
            )
        );
    }

    public static boolean isEntityCaster(Entity entity) {
        return entity instanceof ServerPlayerEntity || casterEntities.containsKey(entity.getUuid());
    }

    public static void castAsEntity(Entity entity, ArrayList<Iota> spell) {
        if (entity == null || !casterEntities.containsKey(entity.getUuid())) {
            HexJS.LOGGER.info("Tried to cast as an entity that was not a registered caster!");
            return;
        }

        // Get the harness
        CastingHarness harness = casterEntities.get(entity.getUuid());

        // Teleport secret player
        ServerWorld targetWorld = (ServerWorld) entity.getWorld();
        double x = entity.getX(), y = entity.getY(), z = entity.getZ();
        float yaw = entity.prevYaw, pitch = entity.prevPitch;
        secretCaster.stopRiding();
        if (targetWorld == secretCaster.world) {
            secretCaster.setPosition(x, y, z);
            secretCaster.setYaw(yaw);
            secretCaster.setPitch(pitch);
        } else {
            ServerWorld serverWorld = secretCaster.getWorld();
            secretCaster.server.getPlayerManager().sendCommandTree(secretCaster);
            serverWorld.removePlayer(secretCaster, Entity.RemovalReason.CHANGED_DIMENSION);
            secretCaster.refreshPositionAndAngles(x, y, z, yaw, pitch);
            secretCaster.setWorld(targetWorld);
            targetWorld.onPlayerTeleport(secretCaster);
            secretCaster.setPosition(x, y, z);
            secretCaster.setYaw(yaw);
            secretCaster.setPitch(pitch);

            // Handle secret caster health
            if (entity instanceof LivingEntity) {
                if (((LivingEntity) entity).isDead()) {
                    HexJS.LOGGER.info("Tried to cast as a dead entity!");
                    return;
                }
                secretCaster.setHealth(((LivingEntity) entity).getHealth());
            }

            ControllerInfo clientInfo = harness.executeIotas(spell, secretCaster.getWorld());

            casterEntities.put(entity.getUuid(), harness);
        }
    }
}
