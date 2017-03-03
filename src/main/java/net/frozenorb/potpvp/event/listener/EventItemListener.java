package net.frozenorb.potpvp.event.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.event.EventHandler;
import net.frozenorb.potpvp.event.EventItems;
import net.frozenorb.potpvp.event.menu.EventsMenu;
import net.frozenorb.potpvp.util.ItemListener;

import org.bukkit.ChatColor;

public final class EventItemListener extends ItemListener {

    public EventItemListener() {
        addHandler(EventItems.EVENTS_ITEM, player -> {
            EventHandler eventHandler = PotPvPSI.getInstance().getEventHandler();

            if (eventHandler.getActiveEvents().isEmpty()) {
                player.sendMessage(ChatColor.RED + "There are no active events.");
            } else {
                new EventsMenu().openMenu(player);
            }
        });
    }

}