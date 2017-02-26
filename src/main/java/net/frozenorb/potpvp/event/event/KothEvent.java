package net.frozenorb.potpvp.event.event;

import net.frozenorb.potpvp.event.Event;
import net.frozenorb.potpvp.event.EventType;

import java.util.Set;
import java.util.UUID;

public final class KothEvent extends Event {

    public KothEvent() {
        super(EventType.KOTH);
    }

    @Override
    public void startEvent(Set<UUID> queued) {

    }

}