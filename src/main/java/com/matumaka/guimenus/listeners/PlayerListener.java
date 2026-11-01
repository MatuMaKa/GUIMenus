package com.matumaka.guimenus.listeners;

import com.matumaka.guimenus.gui.Menu;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerListener implements Listener {

    @EventHandler
    public void onSwing(PlayerArmSwingEvent event) {
        final Menu menu = Menu.getMenu(event.getPlayer());

        if (menu != null && event.getHand() == EquipmentSlot.HAND) {
            menu.click();
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (Menu.getMenu(event.getPlayer()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if (event.getCause() == PlayerKickEvent.Cause.SELF_INTERACTION) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            this.close(event.getPlayer());
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        this.close(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        this.close(event.getEntity());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.close(event.getPlayer());
    }

    private void close(Player player) {
        final Menu menu = Menu.getMenu(player);

        if (menu != null) {
            menu.close();
        }
    }
}
