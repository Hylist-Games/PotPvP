package net.frozenorb.potpvp.arena.menu.manageschematic;

import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

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

        buttons.put(7, new CreateCopiesButton(schematic));
        buttons.put(8, new RemoveCopiesButton(schematic));

        return buttons;
    }

}