package net.frozenorb.potpvp.arena.menu.manageschematic;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.arena.ArenaGrid;
import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.qlib.menu.Button;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

final class TeleportToModelButton extends Button {

    private final ArenaSchematic schematic;

    TeleportToModelButton(ArenaSchematic schematic) {
        this.schematic = Preconditions.checkNotNull(schematic, "schematic");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + "Teleport to model";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.YELLOW + "Click to teleport to the model arena, which",
            ChatColor.YELLOW + "will allow you to make edits to the schematic."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.BREWING_STAND_ITEM;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();

        int xModifier = ArenaGrid.GRID_SPACING_X * schematic.getGridIndex();

        player.teleport(new Location(
            Bukkit.getWorlds().get(0),
            ArenaGrid.STARTING_POINT.getBlockX() - xModifier,
            ArenaGrid.STARTING_POINT.getBlockY(),
            ArenaGrid.STARTING_POINT.getBlockZ()
        ));
    }

}