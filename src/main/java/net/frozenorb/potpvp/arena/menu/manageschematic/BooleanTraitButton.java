package net.frozenorb.potpvp.arena.menu.manageschematic;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.qlib.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

final class BooleanTraitButton extends Button {

    private final ArenaSchematic schematic;
    private final String trait;
    private final BiConsumer<ArenaSchematic, Boolean> setFunction;
    private final Function<ArenaSchematic, Boolean> readFunction;

    BooleanTraitButton(ArenaSchematic schematic, String trait, BiConsumer<ArenaSchematic, Boolean> setFunction, Function<ArenaSchematic, Boolean> readFunction) {
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
            ChatColor.YELLOW + "Current: " + ChatColor.WHITE + (readFunction.apply(schematic) ? "On" : "Off"),
            "",
            ChatColor.GREEN.toString() + ChatColor.BOLD + "Click to toggle"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return readFunction.apply(schematic) ? Material.REDSTONE_TORCH_ON : Material.LEVER;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        boolean current = readFunction.apply(schematic);
        setFunction.accept(schematic, !current);

        try {
            PotPvPSI.getInstance().getArenaHandler().saveSchematics();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        player.sendMessage(ChatColor.GREEN + "Set " + schematic.getName() + "'s " + trait + " trait to " + (current ? "off" : "on"));
    }

}
