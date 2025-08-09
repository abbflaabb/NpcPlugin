package com.Relxwe.npcPlugin.Commands;

import com.Relxwe.npcPlugin.API.NPC;
import com.Relxwe.npcPlugin.NpcPlugin;
import com.Relxwe.npcPlugin.SimpleNPC;
import com.Relxwe.npcPlugin.SkinFetcher;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NPCCommand implements CommandExecutor {
    private final NpcPlugin plugin;

    public NPCCommand(NpcPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("npc")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Usage: /npc <create|list|delete>");
                return true;
            }

            // /npc create <name> [skinPlayerName]
            if (args[0].equalsIgnoreCase("create")) {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /npc create <name> [skinPlayerName]");
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can create NPCs.");
                    return true;
                }

                Player player = (Player) sender;
                String name = args[1];
                String skinPlayerName = args.length > 2 ? args[2] : null;

                SimpleNPC simpleNPC = new SimpleNPC(name);

                if (skinPlayerName != null) {
                    SkinFetcher.SkinData skinData = SkinFetcher.getSkinData(skinPlayerName);
                    if (skinData != null) {
                        simpleNPC.setSkinTexture(skinData.getTexture());
                        simpleNPC.setSkinSignature(skinData.getSignature());
                    } else {
                        player.sendMessage(ChatColor.RED + "Could not fetch skin for player: " + skinPlayerName);
                        return true;
                    }
                }

                simpleNPC.spawn(player.getWorld(), player.getLocation());
                plugin.addNPC(simpleNPC);

                player.sendMessage(ChatColor.GREEN + "NPC created: " + name + ChatColor.GRAY + " (ID: " + simpleNPC.getId() + ")");
                return true;
            }

            // /npc list
            if (args[0].equalsIgnoreCase("list")) {
                if (plugin.getNPCs().isEmpty()) {
                    sender.sendMessage(ChatColor.YELLOW + "No NPCs have been created.");
                    return true;
                }

                sender.sendMessage(ChatColor.AQUA + "List of NPCs:");
                for (NPC npc : plugin.getNPCs()) {
                    sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.YELLOW + npc.getName() + ChatColor.GRAY + " (ID: " + ChatColor.GREEN + npc.getId() + ChatColor.GRAY + ")");
                }
                return true;
            }

            // /npc delete <id>
            if (args[0].equalsIgnoreCase("delete")) {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /npc delete <id>");
                    return true;
                }

                String npcId = args[1];
                NPC npcToRemove = null;
                for (NPC npc : plugin.getNPCs()) {
                    if (npc.getId().equals(npcId)) {
                        npcToRemove = npc;
                        break;
                    }
                }

                if (npcToRemove != null) {
                    npcToRemove.despawn();
                    plugin.getNPCs().remove(npcToRemove);
                    sender.sendMessage(ChatColor.GREEN + "NPC deleted: " + npcToRemove.getName() + ChatColor.GRAY + " (ID: " + npcToRemove.getId() + ")");
                } else {
                    sender.sendMessage(ChatColor.RED + "NPC with ID " + npcId + " not found.");
                }
                return true;
            }
        }
        return false;
    }
}

