package org.silvius.ethoshorses;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
public final class EthosHorses extends JavaPlugin {
    private static EthosHorses plugin;


    @Override
    public void onEnable() {
        plugin = this;

        getCommand("horse").setExecutor(new HorseCommand());
        final PluginManager pluginManager = Bukkit.getPluginManager();
        //pluginManager.registerEvents(new ListenersNew(), this);
        pluginManager.registerEvents(new HorseCommand(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static EthosHorses getPlugin() {
        return plugin;
    }
}
