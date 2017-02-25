package net.frozenorb.potpvp.arena.menu.manageschematic;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.potpvp.arena.menu.manageschematics.ManageSchematicsMenu;
import net.frozenorb.potpvp.util.MenuBackButton;
import net.frozenorb.potpvp.util.menu.BooleanTraitButton;
import net.frozenorb.potpvp.util.menu.IntegerTraitButton;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class ManageSchematicMenu extends Menu {

    private final ArenaSchematic schematic;

    public ManageSchematicMenu(ArenaSchematic schematic) {
        super("Manage " + schematic.getName());
        setAutoUpdate(true);

        this.schematic = schematic;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new SchematicStatusButton(schematic));
        buttons.put(1, new ToggleEnabledButton(schematic));

        buttons.put(3, new TeleportToModelButton(schematic));
        buttons.put(4, new SaveModelButton(schematic));

        if (PotPvPSI.getInstance().getArenaHandler().getGrid().isBusy()) {
            Button busyButton = Button.placeholder(Material.WOOL, DyeColor.SILVER.getWoolData(), ChatColor.GRAY.toString() + ChatColor.BOLD + "Grid is busy");

            buttons.put(7, busyButton);
            buttons.put(8, busyButton);
        } else {
            buttons.put(7, new CreateCopiesButton(schematic));
            buttons.put(8, new RemoveCopiesButton(schematic));
        }

        buttons.put(9, new MenuBackButton(p -> new ManageSchematicsMenu().openMenu(p)));

        Consumer<ArenaSchematic> save = schematic -> {
            try {
                PotPvPSI.getInstance().getArenaHandler().saveSchematics();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        buttons.put(12, new IntegerTraitButton<>(schematic, "Max Player Count", ArenaSchematic::setMaxPlayerCount, ArenaSchematic::getMaxPlayerCount, save));
        buttons.put(13, new IntegerTraitButton<>(schematic, "Min Player Count", ArenaSchematic::setMinPlayerCount, ArenaSchematic::getMinPlayerCount, save));
        buttons.put(14, new BooleanTraitButton<>(schematic, "Supports Ranked", ArenaSchematic::setSupportsRanked, ArenaSchematic::isSupportsRanked, save));
        buttons.put(15, new BooleanTraitButton<>(schematic, "Archer Only", ArenaSchematic::setArcherOnly, ArenaSchematic::isArcherOnly, save));

        return buttons;
    }

}