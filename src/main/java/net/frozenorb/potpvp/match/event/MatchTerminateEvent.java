package net.frozenorb.potpvp.match.event;

import com.google.common.base.Preconditions;

import net.frozenorb.potpvp.match.Match;

import org.bson.Document;
import org.bukkit.event.HandlerList;

import lombok.Getter;

/**
 * Called when a match is terminated (when its {@link net.frozenorb.potpvp.match.MatchState} changes
 * to {@link net.frozenorb.potpvp.match.MatchState#TERMINATED})
 * @see net.frozenorb.potpvp.match.MatchState#TERMINATED
 */
public final class MatchTerminateEvent extends MatchEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    /**
     * Mutable {@link Document} which will be saved to the database
     * to represent this match. Any clients wishing to store custom data
     * in the ended match document should mutate this Document to do so.
     */
    @Getter private final Document databaseEntry;

    public MatchTerminateEvent(Match match, Document databaseEntry) {
        super(match);

        this.databaseEntry = Preconditions.checkNotNull(databaseEntry, "databaseEntry");
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}