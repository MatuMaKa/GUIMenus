package com.matumaka.guimenus;

import com.matumaka.guimenus.gui.Menu;
import com.matumaka.guimenus.listeners.PlayerListener;
import org.bukkit.plugin.Plugin;

/**
 * Entry point of the GUI Menus API.
 *
 * <p>Call {@link #init(Plugin)} once from your plugin's {@code onEnable()},
 * and {@link #shutdown()} from your {@code onDisable()}.</p>
 */
public final class GUIMenusAPI {

    private static Plugin plugin;

    private GUIMenusAPI() {}

    /**
     * Initializes the API and hooks it into your plugin.
     *
     * @param plugin your plugin instance, used for events and scheduling
     */
    public static void init(Plugin plugin) {
        GUIMenusAPI.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), plugin);
        plugin.getServer().getScheduler().runTaskTimer(plugin, Menu::tickAll, 1, 1);
    }

    /**
     * Closes every open menu. Call this from your plugin's {@code onDisable()}.
     */
    public static void shutdown() {
        Menu.closeAll();

        plugin = null;
    }

    /**
     * Returns the plugin the API was initialized with.
     */
    public static Plugin getPlugin() {
        return plugin;
    }
}
