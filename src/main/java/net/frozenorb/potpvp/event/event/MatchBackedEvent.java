package net.frozenorb.potpvp.event.event;

import net.frozenorb.potpvp.event.Event;
import net.frozenorb.potpvp.event.EventType;
import net.frozenorb.potpvp.match.Match;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class MatchBackedEvent extends Event {

    protected Match match;

    MatchBackedEvent(EventType type) {
        super(type);
    }

    @Override
    public Set<UUID> getParticipants() {
        return match.getTeams().stream()
            .flatMap(t -> t.getAliveMembers().stream())
            .collect(Collectors.toSet());
    }

    @Override
    public boolean isParticipant(UUID player) {
        return match.getTeam(player) != null;
    }

}