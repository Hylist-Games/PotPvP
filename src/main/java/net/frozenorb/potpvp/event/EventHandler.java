package net.frozenorb.potpvp.event;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.event.listener.EventItemListener;

import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;

public final class EventHandler {

    @Getter private final Set<Event> activeEvents = new HashSet<>();

    public EventHandler() {
        Bukkit.getPluginManager().registerEvents(new EventItemListener(), PotPvPSI.getInstance());
    }

    public Event beginEvent(EventType type, UUID host) {
        Event event = type.createInstance();
        event.initialize(host);
    }

}