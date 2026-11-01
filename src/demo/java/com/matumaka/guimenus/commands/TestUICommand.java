package com.matumaka.guimenus.commands;

import com.matumaka.guimenus.Utils;
import com.matumaka.guimenus.gui.Menu;
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

    private static void give(Player player, Material material) {
        if (player.getInventory().addItem(new ItemStack(material)).isEmpty()) {
            player.sendActionBar(Utils.toComponent("<green>Received <lang:" + material.translationKey() + ">."));
        } else {
            player.sendActionBar(Utils.toComponent("<red>Your inventory is full!"));
        }
    }
}
