package net.frozenorb.potpvp.command;

import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.arena.menu.manageschematics.ManageSchematicsMenu;
import net.frozenorb.potpvp.kit.menu.manage.ManageKitTypeMenu;
import net.frozenorb.potpvp.kittype.menu.SelectKitTypeMenu;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ManageCommand {

    @Command(names = {"manage"}, permission = "potpvp.admin")
    public static void manage(Player sender) {
        new ManageMenu().openMenu(sender);
    }

    private static class ManageMenu extends Menu {

        ManageMenu() {
            super("Admin Management Menu");
        }

        @Override
        public Map<Integer, Button> getButtons(Player player) {
            Map<Integer, Button> buttons = new HashMap<>();

            for (int i = 0; i < 3; i++) {
                buttons.put(i, Button.placeholder(Material.OBSIDIAN));
            }

            buttons.put(3, new ManageKitButton());
            // 4 (5th slot) is empty
            buttons.put(5, new ManageArenaButton());

            for (int i = 6; i < 9; i++) {
                buttons.put(i, Button.placeholder(Material.OBSIDIAN));
            }

            return buttons;
        }

    }

    private static class ManageKitButton extends Button {

        @Override
        public String getName(Player player) {
            return ChatColor.YELLOW + "Manage kit type definitions";
        }

        @Override
        public List<String> getDescription(Player player) {
            return ImmutableList.of();
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.DIAMOND_SWORD;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();

            new SelectKitTypeMenu((kitType) -> {
                player.closeInventory();
                new ManageKitTypeMenu(kitType).openMenu(player);
            }, false).openMenu(player);
        }

    }

    private static class ManageArenaButton extends Button {

        @Override
        public String getName(Player player) {
            return ChatColor.YELLOW + "Manage the arena grid";
        }

        @Override
        public List<String> getDescription(Player player) {
            return ImmutableList.of();
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.IRON_PICKAXE;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            new ManageSchematicsMenu().openMenu(player);
        }

    }

}