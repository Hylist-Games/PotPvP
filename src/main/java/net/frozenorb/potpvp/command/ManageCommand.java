package net.frozenorb.potpvp.command;

import net.frozenorb.potpvp.kit.menu.manage.ManageKitTypeMenu;
import net.frozenorb.potpvp.kittype.menu.SelectKitTypeMenu;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ManageCommand {
    @Command(names = {"manage"}, permission = "potpvp.admin")
    public static void manage(Player sender) {
        new ManageMenu().openMenu(sender);
    }

    static class ManageMenu extends Menu {
        public ManageMenu() {
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

    static class ManageKitButton extends Button {
        @Override
        public String getName(Player player) {
            return "Manage and modify a kit!";
        }

        @Override
        public List<String> getDescription(Player player) {
            return Collections.emptyList();
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
            }).openMenu(player);
        }
    }

    static class ManageArenaButton extends Button {
        @Override
        public String getName(Player player) {
            return "Manage an Arena!";
        }

        @Override
        public List<String> getDescription(Player player) {
            return Collections.emptyList();
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.IRON_PICKAXE;
        }

        // does nothing ATM. TODO
    }
}
