package net.frozenorb.potpvp.event.event;

import net.frozenorb.potpvp.event.Event;
import net.frozenorb.potpvp.event.EventType;

import java.util.Set;
import java.util.UUID;

public final class LastManStandingEvent extends Event {

    public LastManStandingEvent() {
        super(EventType.LAST_MAN_STANDING);
    }

    @Override
    public void startEvent(Set<UUID> queued) {

    }

}