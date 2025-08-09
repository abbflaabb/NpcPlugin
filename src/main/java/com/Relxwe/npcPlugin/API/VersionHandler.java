package com.Relxwe.npcPlugin.API;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface VersionHandler {
    void spawnNPC(String id, String name, World world, Location location, String skinTexture);
    void despawnNPC(String id);
    void sendPacket(Player player, Object packet);
    String getVersion();
}

