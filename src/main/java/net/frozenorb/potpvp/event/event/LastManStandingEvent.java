package net.frozenorb.potpvp.event.event;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import net.frozenorb.potpvp.event.Event;
import net.frozenorb.potpvp.event.EventType;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class LastManStandingEvent extends MatchBackedEvent {

    public LastManStandingEvent() {
        super(EventType.LAST_MAN_STANDING);
    }

    @Override
    public void startEvent(Set<UUID> queued) {

    }

    @Override
    public List<String> getLiveStatus() {
        return ImmutableList.of(
            ChatColor.YELLOW + "12" + ChatColor.WHITE + " players remain"
        );
    }

}