package com.matumaka.guimenus.events;

import com.matumaka.guimenus.gui.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for all GUIMenus events. Simply exposes the affected {@link Menu}.
 *
 * @see MenuOpenEvent
 * @see MenuCloseEvent
 * @see MenuClickEvent
 */
public abstract class MenuEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Menu menu;

    protected MenuEvent(Player player, Menu menu) {
        super(player);

        this.menu = menu;
    }

    /**
     * The menu this event concerns.
     */
    public Menu getMenu() {
        return this.menu;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
