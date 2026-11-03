package com.matumaka.guimenus.commands;

import com.matumaka.guimenus.Utils;
import com.matumaka.guimenus.gui.Menu;
import com.matumaka.guimenus.gui.MenuStyle;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TestUICommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        final Player player = (Player) sender;

        if (args.length >= 2 && args[0].equalsIgnoreCase("bg")) {
            return this.changeBackground(player, args[1], args.length >= 3 ? args[2] : null);
        }

        final Menu menu = Menu.getMenu(player);

        if (menu != null) {
            menu.close();
            return true;
        }

        final Menu mainMenu = new Menu("MAIN MENU");
        final Menu creditsMenu = new Menu("CREDITS");

        mainMenu.registerButton("Close", () -> {
            mainMenu.close();
            player.showTitle(Title.title(Utils.toComponent("<aqua>A TEST MENU"), Utils.toComponent("<gray>You have closed the main menu!")));
        });
        mainMenu.registerButton("Credits", () -> creditsMenu.displayTo(player));

        creditsMenu.registerButton("Creator: MatuMaKa", Material.DIAMOND_AXE, () -> give(player, Material.DIAMOND_AXE));
        creditsMenu.registerButton("Tester: MatuMaKa", () -> give(player, Material.GOLDEN_APPLE));
        creditsMenu.registerButton("Back", () -> mainMenu.displayTo(player));

        mainMenu.displayTo(player);
        return true;
    }

    /**
     * Handles {@code /testui bg <panel|button|hover> <material>} — changes the
     * menu or button background. Applies live to any open menu; otherwise a
     * fresh demo menu is opened with the new background.
     */
    private boolean changeBackground(Player player, String part, String materialName) {
        if (materialName == null) {
            player.sendActionBar(Utils.toComponent("<gray>Usage: /testui bg <panel|button> <material>"));
            return true;
        }

        final Material material = Material.matchMaterial(materialName);

        if (material == null || !material.isBlock()) {
            player.sendActionBar(Utils.toComponent("<red>" + materialName + " is not a valid block material."));
            return true;
        }

        final MenuStyle style;
        final Menu open = Menu.getMenu(player);

        if (open == null) {
            style = MenuStyle.defaults();
            new Menu("MAIN MENU", style).displayTo(player);
        } else {
            style = open.getStyle();
        }

        if (part.equalsIgnoreCase("panel")) {
            style.setPanelMaterial(material);
        } else if (part.equalsIgnoreCase("button")) {
            style.setButtonMaterial(material);
        } else if (part.equalsIgnoreCase("hover")) {
            style.setButtonHoverMaterial(material);
        } else {
            player.sendActionBar(Utils.toComponent("<gray>Usage: /testui bg <panel|button|hover> <material>"));
            return true;
        }

        if (open != null) {
            open.redraw();
        }

        player.sendActionBar(Utils.toComponent("<green>Background updated to <lang:" + material.translationKey() + ">."));
        return true;
    }

    private static void give(Player player, Material material) {
        if (player.getInventory().addItem(new ItemStack(material)).isEmpty()) {
            player.sendActionBar(Utils.toComponent("<green>Received <lang:" + material.translationKey() + ">."));
        } else {
            player.sendActionBar(Utils.toComponent("<red>Your inventory is full!"));
        }
    }
}
