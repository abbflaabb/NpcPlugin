package com.Relxwe.npcPlugin.storage;

import com.Relxwe.npcPlugin.API.NPC;
import com.Relxwe.npcPlugin.NpcPlugin;
import com.Relxwe.npcPlugin.SimpleNPC;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NPCStorage {

    private final NpcPlugin plugin;
    private final Gson gson;
    private final File storageFile;

    public NPCStorage(NpcPlugin plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.storageFile = new File(plugin.getDataFolder(), "npcs.json");
    }

    public void saveNPCs(NpcPlugin plugin, List<NPC> npcs) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        try (FileWriter writer = new FileWriter(storageFile)) {
            gson.toJson(npcs, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save NPCs: " + e.getMessage());
        }
    }

    public List<NPC> loadNPCs() {
        if (!storageFile.exists()) {
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(storageFile)) {
            Type type = new TypeToken<List<SimpleNPC>>() {}.getType();
            List<SimpleNPC> loadedNPCs = gson.fromJson(reader, type);
            if (loadedNPCs != null) {
                return new ArrayList<>(loadedNPCs);
            } else {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Could not load NPCs: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}

