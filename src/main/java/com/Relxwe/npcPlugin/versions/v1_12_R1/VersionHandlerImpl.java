package com.Relxwe.npcPlugin.versions.v1_12_R1;

import com.Relxwe.npcPlugin.API.VersionHandler;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.UUID;

public class VersionHandlerImpl implements VersionHandler {

    @Override
    public void spawnNPC(String id, String name, World world, Location location, String skinTexture) {
        try {
            Class<?> minecraftServerClass = Class.forName("net.minecraft.server.v1_12_R1.MinecraftServer");
            Method getServerMethod = Class.forName("org.bukkit.craftbukkit.v1_12_R1.CraftServer").getMethod("getServer");
            Object minecraftServer = getServerMethod.invoke(Bukkit.getServer());

            Class<?> worldServerClass = Class.forName("net.minecraft.server.v1_12_R1.WorldServer");
            Method getHandleMethod = Class.forName("org.bukkit.craftbukkit.v1_12_R1.CraftWorld").getMethod("getHandle");
            Object worldServer = getHandleMethod.invoke(world);

            GameProfile gameProfile = new GameProfile(UUID.fromString(id), name);
            gameProfile.getProperties().put("textures", new Property("textures", skinTexture, ""));

            Class<?> playerInteractManagerClass = Class.forName("net.minecraft.server.v1_12_R1.PlayerInteractManager");
            Constructor<?> playerInteractManagerConstructor = playerInteractManagerClass.getConstructor(worldServerClass);
            Object playerInteractManager = playerInteractManagerConstructor.newInstance(worldServer);

            Class<?> entityPlayerClass = Class.forName("net.minecraft.server.v1_12_R1.EntityPlayer");
            Constructor<?> entityPlayerConstructor = entityPlayerClass.getConstructor(minecraftServerClass, worldServerClass, GameProfile.class, playerInteractManagerClass);
            Object npc = entityPlayerConstructor.newInstance(minecraftServer, worldServer, gameProfile, playerInteractManager);

            Method setLocationMethod = entityPlayerClass.getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
            setLocationMethod.invoke(npc, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            Class<?> packetPlayOutPlayerInfoClass = Class.forName("net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo");
            Class<?> enumPlayerInfoActionClass = Class.forName("net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
            Object addPlayerAction = enumPlayerInfoActionClass.getField("ADD_PLAYER").get(null);
            Constructor<?> packetPlayOutPlayerInfoConstructor = packetPlayOutPlayerInfoClass.getConstructor(enumPlayerInfoActionClass, Class.forName("net.minecraft.server.v1_12_R1.EntityPlayer"));
            Object packetPlayOutPlayerInfoAdd = packetPlayOutPlayerInfoConstructor.newInstance(addPlayerAction, npc);

            Class<?> packetPlayOutEntityTeleportClass = Class.forName("net.minecraft.server.v1_12_R1.PacketPlayOutEntityTeleport");
            Constructor<?> packetPlayOutEntityTeleportConstructor = packetPlayOutEntityTeleportClass.getConstructor(Class.forName("net.minecraft.server.v1_12_R1.Entity"));
            Object packetPlayOutEntityTeleport = packetPlayOutEntityTeleportConstructor.newInstance(npc);

            Class<?> packetPlayOutNamedEntitySpawnClass = Class.forName("net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn");
            Constructor<?> packetPlayOutNamedEntitySpawnConstructor = packetPlayOutNamedEntitySpawnClass.getConstructor(Class.forName("net.minecraft.server.v1_12_R1.EntityHuman"));
            Object packetPlayOutNamedEntitySpawn = packetPlayOutNamedEntitySpawnConstructor.newInstance(npc);

            for (Player player : Bukkit.getOnlinePlayers()) {
                sendPacket(player, packetPlayOutPlayerInfoAdd);
                sendPacket(player, packetPlayOutNamedEntitySpawn);
                sendPacket(player, packetPlayOutEntityTeleport);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void despawnNPC(String id) {
        // This needs to be handled by storing the NPC entity ID when spawned
        // For now, we'll send a packet to remove a player with the given ID (assuming it's the NPC's UUID)
        // This is a placeholder and needs proper implementation with actual NPC entity tracking.
        // int entityId = getEntityIdForNPC(id); // Need a way to get the entity ID
        // PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(entityId);
        // for (Player player : Bukkit.getOnlinePlayers()) {
        //     sendPacket(player, packetPlayOutEntityDestroy);
        // }
    }

    @Override
    public void sendPacket(Player player, Object packet) {
        try {
            Object craftPlayer = Class.forName("org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer").cast(player);
            Method getHandleMethod = craftPlayer.getClass().getMethod("getHandle");
            Object entityPlayer = getHandleMethod.invoke(craftPlayer);

            Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
            Method sendPacketMethod = playerConnection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server.v1_12_R1.Packet"));
            sendPacketMethod.invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getVersion() {
        return "v1_12_R1";
    }
}

