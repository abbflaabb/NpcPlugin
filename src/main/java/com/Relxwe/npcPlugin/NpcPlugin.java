package com.Relxwe.npcPlugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class NpcPlugin extends JavaPlugin {

    public Logger logger;


    @Override
    public void onEnable() {
        logger.info("NpcPlugin has been enabled!");
        // Plugin startup logic
        this.logger = this.getLogger();
        // Register commands, events, etc.

    }

    @Override
    public void onDisable() {
        logger.info("NpcPlugin has been disabled!");
        // Plugin shutdown logic
        // Clean up resources, save data, etc.
    }
}
