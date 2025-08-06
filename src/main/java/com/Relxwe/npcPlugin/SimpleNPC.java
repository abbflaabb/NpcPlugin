package com.Relxwe.npcPlugin;

import com.Relxwe.npcPlugin.API.NPC;
import com.Relxwe.npcPlugin.storage.util.NPCPacketUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SimpleNPC implements NPC {

    private final String id;
    private String name;
    private String skinTexture;
    public String skinSignature;
    private EntityPlayer entityPlayer;

    public SimpleNPC(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public void setSkinSignature(String signature) {
        this.skinSignature = signature;
    }

    @Override public String getId() { return id; }
    @Override public String getName() { return name; }
    @Override public void setName(String name) { this.name = name; }
    @Override public String getSkinTexture() { return skinTexture; }
    @Override public void setSkinTexture(String skinTexture) { this.skinTexture = skinTexture; }

    public void spawn(World world, Location location) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) world).getHandle();

        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        profile.getProperties().put("textures", new Property("textures", skinTexture, skinSignature));

        entityPlayer = new EntityPlayer(server, nmsWorld, profile, new PlayerInteractManager(nmsWorld));
        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
        sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));

        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("npcPlugin"), () -> {
            sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
        }, 60L);
    }

    public void despawn() {
        if (entityPlayer != null) {
            sendPacket(new PacketPlayOutEntityDestroy(entityPlayer.getId()));
            sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
        }
    }

    public Player getEntity() {
        return entityPlayer != null ? (Player) entityPlayer.getBukkitEntity() : null;
    }

    private void sendPacket(Packet<?> packet) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
    }
    public void showTo(Player player) {
        NPCPacketUtils.spawnNPCPacket(player, this);
    }

}
