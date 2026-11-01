package com.matumaka.guimenus;

import com.matumaka.guimenus.GUIMenusAPI;
import com.matumaka.guimenus.commands.TestUICommand;
import com.matumaka.guimenus.gui.Menu;

import org.bukkit.plugin.java.JavaPlugin;

public final class GUIMenus extends JavaPlugin {

    @Override
    public void onEnable() {
        GUIMenusAPI.init(this);

        this.getCommand("testui").setExecutor(new TestUICommand());
    }

    @Override
    public void onDisable() {
        GUIMenusAPI.shutdown();
    }
}
