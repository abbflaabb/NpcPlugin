package com.Relxwe.npcPlugin;

import com.Relxwe.npcPlugin.API.NPC;
import com.Relxwe.npcPlugin.Commands.NPCCommand;
import com.Relxwe.npcPlugin.storage.NPCStorage;
import com.Relxwe.npcPlugin.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class NpcPlugin extends JavaPlugin {

    private List<NPC> npcs = new ArrayList<>();
    private NPCStorage npcStorage;

    @Override
    public void onEnable() {
        // Initialize NPCStorage
        npcStorage = new NPCStorage(this);

        // Register command
        getCommand("npc").setExecutor(new NPCCommand(this));

        // Load NPCs from storage
        npcs = npcStorage.loadNPCs();

        // Check for version compatibility
        if (!VersionUtils.isVersionSupported()) {
            getLogger().warning("This server version (" + VersionUtils.getVersion() + ") is not officially supported. Plugin may not function correctly.");
        }
    }

    @Override
    public void onDisable() {
        // Despawn all NPCs
        for (NPC npc : npcs) {
            if (npc instanceof SimpleNPC) {
                ((SimpleNPC) npc).despawn();
            }
        }
        // Save NPCs to storage
        npcStorage.saveNPCs(this, npcs);
    }

    public void addNPC(NPC npc) {
        this.npcs.add(npc);
    }

    public List<NPC> getNPCs() {
        return npcs;
    }
}

