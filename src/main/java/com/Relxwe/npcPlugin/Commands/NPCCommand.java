package com.Relxwe.npcPlugin.Commands;

import com.Relxwe.npcPlugin.API.NPC;
import com.Relxwe.npcPlugin.NpcPlugin;
import com.Relxwe.npcPlugin.SimpleNPC;
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
                sender.sendMessage(ChatColor.RED + "Usage: /npc <create|list>");
                return true;
            }

            // /npc create <name> [skinTexture]
            if (args[0].equalsIgnoreCase("create")) {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /npc create <name> [skinTexture]");
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can create NPCs.");
                    return true;
                }

                Player player = (Player) sender;
                String name = args[1];
                String skinTexture = args.length > 2 ? args[2] : "default_skin_base64";

                NPC npc = new SimpleNPC(name);
                npc.setSkinTexture(skinTexture);
                ((SimpleNPC) npc).spawn(player.getWorld(), player.getLocation());
                plugin.addNPC(npc);

                player.sendMessage(ChatColor.GREEN + "NPC created: " + name + ChatColor.GRAY + " (ID: " + npc.getId() + ")");
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
        }
        return false;
    }
}
