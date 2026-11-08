package com.matumaka.guimenus.events;

import com.matumaka.guimenus.gui.Button;
import com.matumaka.guimenus.gui.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a player clicks a button of an open menu, before the button's
 * action runs. Cancelling prevents the action (and its click sound) from
 * executing.
 */
public class MenuClickEvent extends MenuEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Button button;
    private boolean cancelled;

    public MenuClickEvent(Player player, Menu menu, Button button) {
        super(player, menu);

        this.button = button;
    }

    /**
     * The button that was clicked.
     */
    public Button getButton() {
        return this.button;
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
