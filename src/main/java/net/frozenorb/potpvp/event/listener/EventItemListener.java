package net.frozenorb.potpvp.event.listener;

import net.frozenorb.potpvp.event.EventItems;
import net.frozenorb.potpvp.event.menu.EventsMenu;
import net.frozenorb.potpvp.util.ItemListener;

public final class EventItemListener extends ItemListener {

    public EventItemListener() {
        addHandler(EventItems.EVENTS_ITEM, p -> new EventsMenu().openMenu(p));
    }

}