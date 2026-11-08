package com.matumaka.guimenus.events;

import com.matumaka.guimenus.gui.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a player is about to receive a menu, before the camera rig is
 * mounted. Cancelling prevents the menu from being displayed entirely.
 */
public class MenuOpenEvent extends MenuEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled;

    public MenuOpenEvent(Player player, Menu menu) {
        super(player, menu);
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
