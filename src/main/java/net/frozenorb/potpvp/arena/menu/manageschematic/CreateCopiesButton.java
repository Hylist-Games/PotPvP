package net.frozenorb.potpvp.arena.menu.manageschematic;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.qlib.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

final class CreateCopiesButton extends Button {

    private final ArenaSchematic schematic;

    CreateCopiesButton(ArenaSchematic schematic) {
        this.schematic = Preconditions.checkNotNull(schematic, "schematic");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + "Create copies of " + schematic.getName() + "";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.GREEN.toString() + ChatColor.BOLD + "CLICK " + ChatColor.GREEN + "to create 1 new copy",
            ChatColor.GREEN.toString() + ChatColor.BOLD + "SHIFT-CLICK " + ChatColor.GREEN + "to create 10 new copies"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.EMERALD_BLOCK;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        int existing = arenaHandler.countArenas(schematic);
        int create = clickType.isShiftClick() ? 10 : 1;

        arenaHandler.getGrid().scaleCopies(schematic, existing + create);
    }

}