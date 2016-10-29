package net.frozenorb.potpvp.kit.menu.kits;

import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.kittype.menu.SelectKitTypeMenu;
import net.frozenorb.qlib.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

final class KitBackButton extends Button {

    @Override
    public String getName(Player player) {
        return ChatColor.RED.toString() + ChatColor.BOLD + "Back";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.RED + "Click here to return to the",
            ChatColor.RED + "kit type selection menu."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.REDSTONE;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();

        new SelectKitTypeMenu(kitType -> {
            new KitsMenu(kitType).openMenu(player);
        }).openMenu(player);
    }

}