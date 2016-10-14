/**
 * Records and presents player statistics.<br/>
 * Player statistics are presented via
 * {@link net.frozenorb.potpvp.matchstats.MatchStatsHandler#getStatisticsSnapshot(java.util.UUID)}
 * in the form of a {@link net.frozenorb.potpvp.matchstats.PlayerStats}.
 * The snapshot is an immutable representation of a player's statistics from either login or
 * since the last invokation of {@link net.frozenorb.potpvp.matchstats.MatchStatsHandler#resetPlayerStats(java.util.UUID)},
 * whichever is latest.
 */
package net.frozenorb.potpvp.matchstats;