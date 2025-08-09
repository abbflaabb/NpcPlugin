package com.Relxwe.npcPlugin.storage;

import com.Relxwe.npcPlugin.API.NPC;
import com.Relxwe.npcPlugin.NpcPlugin;
import com.Relxwe.npcPlugin.SimpleNPC;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCStorage {

    private final NpcPlugin plugin;
    private final Gson gson;
    private final File storageFile;

    public NPCStorage(NpcPlugin plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.storageFile = new File(plugin.getDataFolder(), "npcs.yml");

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
    }

    public void saveNPCs(NpcPlugin plugin, List<NPC> npcs) {
        List<NPCData> npcDataList = new ArrayList<>();
        for (NPC npc : npcs) {
            Location loc = npc.getCitizensNPC().getStoredLocation();
            String worldName = (loc != null && loc.getWorld() != null) ? loc.getWorld().getName() : null;
            String skinTexture = null;
            String skinSignature = null;

            if (npc.getCitizensNPC().hasTrait(SkinTrait.class)) {
                SkinTrait skinTrait = npc.getCitizensNPC().getTrait(SkinTrait.class);
                skinTexture = skinTrait.getTexture();
                skinSignature = skinTrait.getSignature();
            }

            npcDataList.add(new NPCData(npc.getId(), npc.getName(), worldName, loc != null ? loc.getX() : 0, loc != null ? loc.getY() : 0, loc != null ? loc.getZ() : 0, loc != null ? loc.getYaw() : 0, loc != null ? loc.getPitch() : 0, skinTexture, skinSignature));
        }

        try (FileWriter writer = new FileWriter(storageFile)) {
            gson.toJson(npcDataList, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save NPCs: " + e.getMessage());
        }
    }

    public List<NPC> loadNPCs() {
        List<NPC> npcs = new ArrayList<>();
        if (!storageFile.exists()) {
            return npcs;
        }

        try (FileReader reader = new FileReader(storageFile)) {
            Type type = new TypeToken<List<NPCData>>() {}.getType();
            List<NPCData> npcDataList = gson.fromJson(reader, type);

            if (npcDataList != null) {
                NPCRegistry registry = CitizensAPI.getNPCRegistry();
                for (NPCData data : npcDataList) {
                    SimpleNPC simpleNPC = new SimpleNPC(data.getName());
                    simpleNPC.setSkinTexture(data.getSkinTexture());
                    simpleNPC.setSkinSignature(data.getSkinSignature());

                    World world = Bukkit.getWorld(data.getWorldName());
                    if (world != null) {
                        Location location = new Location(world, data.getX(), data.getY(), data.getZ(), data.getYaw(), data.getPitch());
                        simpleNPC.spawn(world, location);
                        // Ensure the Citizens NPC has the correct UUID after creation
                        // Citizens handles UUIDs internally, so we don't need to set it manually here.
                        // simpleNPC.getCitizensNPC().setUniqueId(UUID.fromString(data.getId()));
                    } else {
                        plugin.getLogger().warning("World " + data.getWorldName() + " not found for NPC " + data.getName() + ". Skipping spawn.");
                    }
                    npcs.add(simpleNPC);
                }
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Could not load NPCs: " + e.getMessage());
        }
        return npcs;
    }

    private static class NPCData {
        private String id;
        private String name;
        private String worldName;
        private double x, y, z;
        private float yaw, pitch;
        private String skinTexture;
        private String skinSignature;

        public NPCData(String id, String name, String worldName, double x, double y, double z, float yaw, float pitch, String skinTexture, String skinSignature) {
            this.id = id;
            this.name = name;
            this.worldName = worldName;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
            this.skinTexture = skinTexture;
            this.skinSignature = skinSignature;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getWorldName() {
            return worldName;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }

        public float getYaw() {
            return yaw;
        }

        public float getPitch() {
            return pitch;
        }

        public String getSkinTexture() {
            return skinTexture;
        }

        public String getSkinSignature() {
            return skinSignature;
        }
    }
}

