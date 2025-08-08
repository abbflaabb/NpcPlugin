package com.Relxwe.npcPlugin.API;

import org.bukkit.Location;
import org.bukkit.World;

public interface NPC {
    String getId();
    String getName();
    void setSkinTexture(String skinTexture);
    void spawn(World world, Location location);
    void despawn();
}

