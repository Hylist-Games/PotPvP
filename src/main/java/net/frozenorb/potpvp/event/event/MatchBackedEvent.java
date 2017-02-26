package net.frozenorb.potpvp.event.event;

import net.frozenorb.potpvp.event.Event;
import net.frozenorb.potpvp.event.EventType;
import net.frozenorb.potpvp.match.Match;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class MatchBackedEvent extends Event {

    protected Match match;

    protected MatchBackedEvent(EventType type) {
        super(type);
    }

    @Override
    public Set<UUID> getParticipants() {

    }

    @Override
    public List<String> getLiveStatus() {

    }

}