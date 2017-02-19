package net.frozenorb.potpvp.arena.menu.manageschematic;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.qlib.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

final class IntegerTraitButton extends Button {

    private final ArenaSchematic schematic;
    private final String trait;
    private final BiConsumer<ArenaSchematic, Integer> setFunction;
    private final Function<ArenaSchematic, Integer> readFunction;

    IntegerTraitButton(ArenaSchematic schematic, String trait, BiConsumer<ArenaSchematic, Integer> setFunction, Function<ArenaSchematic, Integer> readFunction) {
        this.schematic = Preconditions.checkNotNull(schematic, "schematic");
        this.trait = trait;
        this.setFunction = setFunction;
        this.readFunction = readFunction;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + "Edit " + trait;
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            ChatColor.YELLOW + "Current: " + ChatColor.WHITE + readFunction.apply(schematic),
            "",
            ChatColor.GREEN.toString() + ChatColor.BOLD + "LEFT-CLICK " + ChatColor.GREEN + "to increase by 1",
            ChatColor.GREEN.toString() + ChatColor.BOLD + "SHIFT LEFT-CLICK " + ChatColor.GREEN + "to increase by 10",
            "",
            ChatColor.RED.toString() + ChatColor.BOLD + "RIGHT-CLICK " + ChatColor.GREEN + "to decrease by 1",
            ChatColor.RED.toString() + ChatColor.BOLD + "SHIFT RIGHT-CLICK " + ChatColor.GREEN + "to decrease by 10"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.GHAST_TEAR;
    }

    @Override
    public int getAmount(Player player) {
        return readFunction.apply(schematic);
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        int current = readFunction.apply(schematic);
        int change = clickType.isShiftClick() ? 10 : 1;

        if (clickType.isRightClick()) {
            change = -change;
        }

        setFunction.accept(schematic, current + change);
        player.sendMessage(ChatColor.GREEN + "Set " + schematic.getName() + "'s " + trait + " trait to " + (current + change));
    }

}