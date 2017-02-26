package net.frozenorb.potpvp.event.menu;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.event.Event;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class EventsMenu extends Menu {

    public EventsMenu() {
        super("Events");
        setAutoUpdate(true);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        for (Event event : PotPvPSI.getInstance().getEventHandler().getActiveEvents()) {
            buttons.put(index++, new EventButton(event));
        }

        return buttons;
    }

}