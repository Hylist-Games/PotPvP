package net.frozenorb.potpvp.event.event;

import net.frozenorb.potpvp.event.Event;
import net.frozenorb.potpvp.event.EventType;

import java.util.Set;
import java.util.UUID;

public final class EvolutionEvent extends Event {

    public EvolutionEvent() {
        super(EventType.EVOLUTION);
    }

    @Override
    public void startEvent(Set<UUID> queued) {

    }

}