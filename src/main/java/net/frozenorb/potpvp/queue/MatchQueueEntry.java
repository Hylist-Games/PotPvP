package net.frozenorb.potpvp.queue;

import com.google.common.collect.ImmutableSet;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;

/**
 * Represents a group of at least one player
 * in a {@link MatchQueue}
 *
 * For multi player fights, 
 */
public final class MatchQueueEntry {

    /**
     * Immutable set of all members who are queued together.
     * All players will be put on one team when their match
     * starts.
     */
    // safe to use @Getter as the constructor already
    // creates an immutable copy
    @Getter private final Set<UUID> members;

    /**
     * Time this MatchQueueEntry joined its {@link MatchQueue}
     */
    @Getter private final Instant timeJoined;

    MatchQueueEntry(Set<UUID> members) {
        this.members = ImmutableSet.copyOf(members);
        this.timeJoined = Instant.now();
    }

    /**
     * Gets how long, in seconds, this MatchQueueEntry has been waiting in a queue
     * @return the duration, in seconds, this entry has been waiting in a queue
     */
    public int getWaitTime() {
        return (int) ChronoUnit.SECONDS.between(timeJoined, Instant.now());
    }

}