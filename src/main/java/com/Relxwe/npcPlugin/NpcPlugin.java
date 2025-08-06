package com.Relxwe.npcPlugin;


import com.Relxwe.npcPlugin.API.NPC;
import com.Relxwe.npcPlugin.Commands.NPCCommand;
import com.Relxwe.npcPlugin.storage.NPCStorage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class NpcPlugin extends JavaPlugin {
    private final List<NPC> npcs = new ArrayList<>();

    @Override
    public void onEnable() {
        getCommand("npc").setExecutor(new NPCCommand(this));
        getServer().getPluginManager().registerEvents(new NPCEntityListener(npcs), this);
        NPCStorage.loadNPCs(this);
    }

    @Override
    public void onDisable() {
        for (NPC npc : npcs) {
            if (npc instanceof SimpleNPC) ((SimpleNPC) npc).despawn();
        }
        NPCStorage.saveNPCs(this, npcs);
    }

    public void addNPC(NPC npc) { npcs.add(npc); }
    public void removeNPC(NPC npc) { npcs.remove(npc); }
    public List<NPC> getNPCs() {
        return npcs;
    }
}
