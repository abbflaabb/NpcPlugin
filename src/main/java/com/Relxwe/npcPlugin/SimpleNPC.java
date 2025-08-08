package com.Relxwe.npcPlugin;

import com.Relxwe.npcPlugin.API.NPC;
import com.Relxwe.npcPlugin.storage.NPCStorage;
import com.Relxwe.npcPlugin.utils.VersionUtils;
import com.Relxwe.npcPlugin.API.VersionHandler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SimpleNPC implements NPC {

    private final String id;
    private String name;
    private String skinTexture;
    private String skinSignature;

    public SimpleNPC(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setSkinTexture(String skinTexture) {
        this.skinTexture = skinTexture;
    }

    public void setSkinSignature(String skinSignature) {
        this.skinSignature = skinSignature;
    }

    @Override
    public void spawn(World world, Location location) {
        try {
            Class<?> versionHandlerClass = Class.forName("com.Relxwe.npcPlugin.versions." + VersionUtils.getVersion() + ".VersionHandlerImpl");
            VersionHandler versionHandler = (VersionHandler) versionHandlerClass.newInstance();
            versionHandler.spawnNPC(id, name, world, location, skinTexture);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to spawn NPC due to version incompatibility: " + e.getMessage());
        }
    }

    @Override
    public void despawn() {
        try {
            Class<?> versionHandlerClass = Class.forName("com.Relxwe.npcPlugin.versions." + VersionUtils.getVersion() + ".VersionHandlerImpl");
            VersionHandler versionHandler = (VersionHandler) versionHandlerClass.newInstance();
            versionHandler.despawnNPC(id);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to despawn NPC due to version incompatibility: " + e.getMessage());
        }
    }

    public void sendPacket(Player player, Object packet) {
        try {
            Class<?> versionHandlerClass = Class.forName("com.Relxwe.npcPlugin.versions." + VersionUtils.getVersion() + ".VersionHandlerImpl");
            VersionHandler versionHandler = (VersionHandler) versionHandlerClass.newInstance();
            versionHandler.sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to send packet due to version incompatibility: " + e.getMessage());
        }
    }
}

