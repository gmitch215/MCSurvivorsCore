package me.gamercoder215.chambertrials;

import me.gamercoder215.chambertrials.biome.CTBiome;
import me.gamercoder215.chambertrials.biome.CTBiomeManager;
import me.gamercoder215.chambertrials.commands.CTCommands;
import me.gamercoder215.chambertrials.commands.CTCommandsUser;
import me.gamercoder215.chambertrials.events.RaceEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class CTCore extends JavaPlugin implements Listener {

    public static BukkitCommandHandler handler;

    public static final class CancelHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }

    @NotNull
    public static Logger getPluginLogger() {
        return JavaPlugin.getPlugin(CTCore.class).getLogger();
    }

    @NotNull
    public static File getPluginDataFolder() {
        return JavaPlugin.getPlugin(CTCore.class).getDataFolder();
    }

    @NotNull
    public static String prefix() {
        return ChatColor.translateAlternateColorCodes('&',
                "&6[&eCT&6]&r &a");
    }

    @NotNull
    public static File getBiomesFolder() {
        File f = new File(getPluginDataFolder(), "biomes");
        if (!f.exists()) f.mkdir();
        return f;
    }

    public static void print(@NotNull Throwable t) {
        getPluginLogger().severe(t.getClass().getSimpleName());
        getPluginLogger().severe("-----------");
        getPluginLogger().severe(t.getMessage());
        for (StackTraceElement e : t.getStackTrace()) getPluginLogger().severe(e.toString());
    }

    @Override
    public void onEnable() {
        getLogger().info("CTCore - Created by gmitch215");
        getLogger().info("Beginning Initialization...");

        saveDefaultConfig();

        // Config
        File dataFolder = getDataFolder();
        FileConfiguration config = getConfig();
        if (!config.isConfigurationSection("Races")) config.createSection("Races");
        if (!config.isDouble("Races.OrangeVelocity")) config.set("Races.OrangeVelocity", 0.2);
        if (!config.isDouble("Races.OrangeVelocityDirectional")) config.set("Races.OrangeVelocityDirectional", 0.15);
        if (!config.isDouble("Races.LimeVelocity")) config.set("Races.LimeVelocity", 0.75);

        try {
            config.save(new File(dataFolder, "config.yml"));
        } catch (IOException e) {
            print(e);
        }
        reloadConfig();

        getLogger().info("Loaded Files...");

        CTBiomeManager.registerBiomes();
        CTBiome.getAllBiomes(); // Load Cache

        getLogger().info("Loaded Biomes...");

        handler = BukkitCommandHandler.create(this);

        new CTCommands(this, handler);
        new CTCommandsUser(this, handler);
        new RaceEvents(this);
        Bukkit.getPluginManager().registerEvents(this, this);

        getLogger().info("Loaded Classes...");
        getLogger().info("Done!");
    }

    // Events

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent e) {
        if (e.getClickedInventory().getHolder() instanceof CancelHolder) e.setCancelled(true);
    }

    @EventHandler
    public void onDrag(@NotNull InventoryDragEvent e) {
        if (e.getView().getTopInventory().getHolder() instanceof CancelHolder) e.setCancelled(true);
    }

}
