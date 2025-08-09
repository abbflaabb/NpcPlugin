package com.Relxwe.npcPlugin;

import com.Relxwe.npcPlugin.API.NPC;
import com.Relxwe.npcPlugin.Commands.NPCCommand;
import com.Relxwe.npcPlugin.storage.NPCStorage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class NpcPlugin extends JavaPlugin implements Listener {

    private List<NPC> npcs = new ArrayList<>();
    private NPCStorage npcStorage;

    @Override
    public void onEnable() {
        // Initialize NPCStorage
        npcStorage = new NPCStorage(this);

        // Register command
        getCommand("npc").setExecutor(new NPCCommand(this));

        // Register this class as a listener for CitizensEnableEvent
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        // Load NPCs from storage (after Citizens is enabled)
        // This will be handled in CitizensEnableEvent
    }

    @Override
    public void onDisable() {
        // Despawn all Citizens NPCs managed by this plugin
        for (NPC npc : npcs) {
            if (npc.getCitizensNPC() != null && npc.getCitizensNPC().isSpawned()) {
                npc.getCitizensNPC().despawn();
            }
        }
        // Save NPCs to storage
        npcStorage.saveNPCs(this, npcs);
    }

    @EventHandler
    public void onCitizensEnable(CitizensEnableEvent event) {
        // Citizens is enabled, now load NPCs
        npcs = npcStorage.loadNPCs();
        getLogger().info("Citizens enabled. Loaded " + npcs.size() + " NPCs.");
    }

    public void addNPC(NPC npc) {
        this.npcs.add(npc);
    }

    public List<NPC> getNPCs() {
        return npcs;
    }
}

