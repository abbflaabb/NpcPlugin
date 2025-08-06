package com.Relxwe.npcPlugin;

import com.Relxwe.npcPlugin.API.NPC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class NPCEntityListener implements Listener {
    private final List<NPC> npcs;

    public NPCEntityListener(List<NPC> npcs) { this.npcs = npcs; }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        Entity clicked = e.getRightClicked();
        Player player = e.getPlayer();

        for (NPC npc : npcs) {
            if (npc instanceof SimpleNPC) {
                Player npcEntity = ((SimpleNPC) npc).getEntity();
                if (npcEntity != null && npcEntity.getEntityId() == clicked.getEntityId()) {
                    player.sendMessage("You interacted with NPC: " + npc.getName());
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (NPC npc : npcs) {
            if (npc instanceof SimpleNPC) {
                ((SimpleNPC) npc).showTo(player);
            }
        }
    }

}
