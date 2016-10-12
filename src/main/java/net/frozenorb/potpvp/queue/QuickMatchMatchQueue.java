package net.frozenorb.potpvp.queue;

/**
 * MatchQueue implementation for creating quick matches (unranked matches)
 * Because quick matches create matches as players join (without any kind of
 * sorting), no actual {@link java.util.Queue} is needed here.
 *
 * QuickMatchMatchQueue, although at first sight an odd name, best represents
 * what this class does (and goes along with our 'QuickMatch' naming convention)
 */
public final class QuickMatchMatchQueue extends MatchQueue {

    private MatchQueueEntry other;

}