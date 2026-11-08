package com.matumaka.guimenus.events;

import com.matumaka.guimenus.gui.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired after a menu has been closed and the player fully restored.
 * This event is informational and cannot be cancelled.
 */
public class MenuCloseEvent extends MenuEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Reason reason;

    public MenuCloseEvent(Player player, Menu menu, Reason reason) {
        super(player, menu);

        this.reason = reason;
    }

    /**
     * Why the menu closed.
     */
    public Reason getReason() {
        return this.reason;
    }

    /**
     * The possible reasons a menu can close for.
     */
    public enum Reason {
        /** The menu's close button or an explicit {@code Menu#close()} call. */
        BUTTON,
        /** The player sneaked while the menu is open. */
        SNEAK,
        /** The player teleported. */
        TELEPORT,
        /** The player died. */
        DEATH,
        /** The player disconnected. */
        QUIT,
        /** The API was shut down, e.g. from {@code onDisable()}. */
        SHUTDOWN
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
