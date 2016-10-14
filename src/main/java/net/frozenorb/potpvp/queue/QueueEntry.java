package net.frozenorb.potpvp.queue;

import net.frozenorb.potpvp.queue.solo.SoloQueue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;

public abstract class QueueEntry<T extends Queue> {

    /**
     * {@link Queue} this QueueEntry is entered in
     */
    @Getter private final T queue;

    /**
     * Time this QueueEntry joined its {@link SoloQueue}
     */
    @Getter private final Instant timeJoined;

    protected QueueEntry(T queue) {
        this.queue = queue;
        this.timeJoined = Instant.now();
    }

    public abstract Set<UUID> getMembers();

    /**
     * Gets how long, in seconds, this MatchQueueEntry has been waiting in a queue
     * @return the duration, in seconds, this entry has been waiting in a queue
     */
    public int getWaitTime() {
        return (int) ChronoUnit.SECONDS.between(timeJoined, Instant.now());
    }

}