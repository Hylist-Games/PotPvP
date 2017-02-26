package net.frozenorb.potpvp.event;

import org.bukkit.event.Listener;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;

public abstract class Event implements Listener {

    @Getter private final EventType type;

    @Getter private UUID host = null;
    @Getter private Instant started = null;
    @Getter private boolean active = false;
    @Getter private int countdown;

    @Getter protected Set<UUID> players = new HashSet<>();

    protected Event(EventType type) {
        this.type = type;
    }

    void initialize(UUID host, int countdown) {
        this.host = host;
        this.countdown = countdown;
    }

    public abstract void startEvent(Set<UUID> queued);
    public abstract Set<UUID> getParticipants();
    public abstract List<String> getLiveStatus();

}