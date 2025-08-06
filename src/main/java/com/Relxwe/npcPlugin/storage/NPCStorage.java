package com.Relxwe.npcPlugin.storage;


import com.Relxwe.npcPlugin.API.NPC;
import com.Relxwe.npcPlugin.NpcPlugin;
import com.Relxwe.npcPlugin.SimpleNPC;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Location;

import java.io.File;
import java.util.List;

public class NPCStorage {

    public static void saveNPCs(NpcPlugin plugin, List<NPC> npcs) {
        File file = new File(plugin.getDataFolder(), "npcs.yml");
        YamlConfiguration config = new YamlConfiguration();

        int i = 0;
        for (NPC npc : npcs) {
            if (!(npc instanceof SimpleNPC)) continue;
            SimpleNPC s = (SimpleNPC) npc;

            Location loc = s.getEntity().getLocation();
            config.set("npcs." + i + ".name", s.getName());
            config.set("npcs." + i + ".skin", s.getSkinTexture());
            config.set("npcs." + i + ".signature", s.skinSignature);
            config.set("npcs." + i + ".world", loc.getWorld().getName());
            config.set("npcs." + i + ".x", loc.getX());
            config.set("npcs." + i + ".y", loc.getY());
            config.set("npcs." + i + ".z", loc.getZ());
            config.set("npcs." + i + ".yaw", loc.getYaw());
            config.set("npcs." + i + ".pitch", loc.getPitch());
            i++;
        }

        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadNPCs(NpcPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "npcs.yml");
        if (!file.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.contains("npcs")) return;

        for (String key : config.getConfigurationSection("npcs").getKeys(false)) {
            String name = config.getString("npcs." + key + ".name");
            String skin = config.getString("npcs." + key + ".skin");
            String sig = config.getString("npcs." + key + ".signature");

            String world = config.getString("npcs." + key + ".world");
            double x = config.getDouble("npcs." + key + ".x");
            double y = config.getDouble("npcs." + key + ".y");
            double z = config.getDouble("npcs." + key + ".z");
            float yaw = (float) config.getDouble("npcs." + key + ".yaw");
            float pitch = (float) config.getDouble("npcs." + key + ".pitch");

            SimpleNPC npc = new SimpleNPC(name);
            npc.setSkinTexture(skin);
            npc.setSkinSignature(sig);
            npc.spawn(plugin.getServer().getWorld(world), new Location(plugin.getServer().getWorld(world), x, y, z, yaw, pitch));
            plugin.addNPC(npc);
        }
    }
}
