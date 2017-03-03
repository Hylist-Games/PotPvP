package net.frozenorb.potpvp.event;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.event.listener.EventItemListener;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;

public final class EventHandler {

    @Getter private final Set<Event> activeEvents = new HashSet<>();

    public EventHandler() {
        Bukkit.getPluginManager().registerEvents(new EventItemListener(), PotPvPSI.getInstance());
    }

    public Event beginEvent(EventType type, UUID host, int countdown, boolean restricted) {
        Event event = type.createInstance();
        event.initialize(host, countdown, restricted);

        activeEvents.add(event);
        return event;
    }

    public Event getNearestEvent() {
        List<Event> events = new ArrayList<>(activeEvents);

        events.removeIf(Event::isActive);
        events.sort(Comparator.comparing(Event::getCountdown));

        return events.isEmpty() ? null : events.get(0);
    }

}