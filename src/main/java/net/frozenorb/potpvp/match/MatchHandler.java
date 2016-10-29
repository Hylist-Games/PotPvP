package net.frozenorb.potpvp.match;

import com.google.common.collect.ImmutableSet;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.arena.Arena;
import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.match.listener.KitSelectionListener;
import net.frozenorb.potpvp.match.listener.MatchCountdownListener;
import net.frozenorb.potpvp.match.listener.MatchDeathMessageListener;
import net.frozenorb.potpvp.match.listener.MatchDurationLimitListener;
import net.frozenorb.potpvp.match.listener.MatchGeneralListener;
import net.frozenorb.potpvp.match.listener.MatchSoupListener;
import net.frozenorb.potpvp.match.listener.SpectatorItemListener;
import net.frozenorb.potpvp.match.listener.SpectatorPreventionListener;
import net.frozenorb.qlib.util.UUIDUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import lombok.Getter;
import lombok.Setter;

public final class MatchHandler {

    private final Set<Match> hostedMatches = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Getter @Setter private boolean rankedMatchesDisabled;
    @Getter @Setter private boolean unrankedMatchesDisabled;

    public MatchHandler() {
        Bukkit.getPluginManager().registerEvents(new KitSelectionListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchCountdownListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchDeathMessageListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchDurationLimitListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchGeneralListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchSoupListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new SpectatorItemListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new SpectatorPreventionListener(), PotPvPSI.getInstance());
    }

    public Match startMatch(List<MatchTeam> teams, KitType kitType) {
        return startMatch(teams, kitType, false);
    }

    public Match startMatch(List<MatchTeam> teams, KitType kitType, boolean startedViaDuel) {
        for (MatchTeam team : teams) {
            for (UUID member : team.getAllMembers()) {
                if (isPlayingOrSpectatingMatch(member)) {
                    throw new IllegalArgumentException(UUIDUtils.name(member) + " is already in a match!");
                }
            }
        }
        
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        long matchSize = teams.stream()
            .mapToInt(t -> t.getAllMembers().size())
            .count();

        // the archer only logic here was often a source of confusion while
        // this code was being written. below is a table of the desired
        // results / if a match can run in a given arena
        //
        //              Arena is archer only    Arena is not archer only
        //  Is Archer           Yes                         Yes
        // Not Archer           No                          Yes
        //
        // the left side of the or statement covers the top row, and the
        // right side covers the right side
        Arena openArena = arenaHandler.allocateUnusedArena(schematic ->
            matchSize <= schematic.getMaxPlayerCount() &&
            matchSize >= schematic.getMinPlayerCount() /*&&
            (kitType == KitType.ARCHER || !schematic.isArcherOnly())*/
        );

        if (openArena == null) {
            return null;
        }

        Match match = new Match(kitType, openArena, teams, startedViaDuel);

        hostedMatches.add(match);
        match.startCountdown();

        return match;
    }

    void removeMatch(Match match) {
        hostedMatches.remove(match);
    }

    /**
     * Gets a read-only set containing all matches currently being hosted.
     * This includes matches that are pre-start and ending.
     * @return a read-only representation of all hosted matches
     */
    public Set<Match> getHostedMatches() {
        return ImmutableSet.copyOf(hostedMatches);
    }

    /**
     * Returns a sum of all players who are playing in a match
     * @return number of players playing in matches
     */
    public int countPlayersPlayingMatches() {
        return countPlayersPlayingMatches(i -> true);
    }

    /**
     * Returns a sum of all players who are playing in a {@link Match}
     * that passes the {@link Predicate} provided.
     * @return number of players playing in matches that
     *          pass the {@link Predicate} provided\
     */
    public int countPlayersPlayingMatches(Predicate<Match> inclusionPredicate) {
        int result = 0;

        for (Match match : hostedMatches) {
            if (inclusionPredicate.test(match)) {
                for (MatchTeam team : match.getTeams()) {
                    result += team.getAliveMembers().size();
                }
            }
        }

        return result;
    }

    /**
     * Gets the match currently being played by the player provided.
     * In this context, played means a player who alive and fighting on a team
     *
     * @param player player to look up match for
     * @return the match being played by the provided player
     */
    public Match getMatchPlaying(Player player) {
        return getMatchPlaying(player.getUniqueId());
    }

    /**
     * Gets the match currently being spectated by the player provided.
     * In this context, spectated includes both players who died while
     * fighting who have not left and players who joined via /spectate.
     *
     * @param player player to look up match for
     * @return the match being spectated by the provided player
     */
    public Match getMatchSpectating(Player player) {
        return getMatchSpectating(player.getUniqueId());
    }

    /**
     * Gets the match currently being spectated or played by the player provided.
     * This method acts as a combination of {@link MatchHandler#getMatchPlaying(UUID)}
     * and {@link MatchHandler#getMatchSpectating(UUID)}.
     *
     * @param player player to look up match for
     * @return the match being played or spectated by the provided player
     */
    public Match getMatchPlayingOrSpectating(Player player) {
        return getMatchPlayingOrSpectating(player.getUniqueId());
    }

    /**
     * Gets the match currently being played by the player provided.
     * In this context, played means a player who alive and fighting on a team
     *
     * @param playerUuid player to look up match for
     * @return the match being played by the provided player
     */
    public Match getMatchPlaying(UUID playerUuid) {
        for (Match match : hostedMatches) {
            for (MatchTeam team : match.getTeams()) {
                if (team.getAliveMembers().contains(playerUuid)) {
                    return match;
                }
            }
        }

        return null;
    }

    /**
     * Gets the match currently being spectated by the player provided.
     * In this context, spectated includes both players who died while
     * fighting who have not left and players who joined via /spectate.
     *
     * @param playerUuid player to look up match for
     * @return the match being spectated by the provided player
     */
    public Match getMatchSpectating(UUID playerUuid) {
        for (Match match : hostedMatches) {
            if (match.isSpectator(playerUuid)) {
                return match;
            }
        }

        return null;
    }

    /**
     * Gets the match currently being spectated or played by the player provided.
     * This method acts as a combination of {@link MatchHandler#getMatchPlaying(UUID)}
     * and {@link MatchHandler#getMatchSpectating(UUID)}.
     *
     * @param playerUuid player to look up match for
     * @return the match being played or spectated by the provided player
     */
    public Match getMatchPlayingOrSpectating(UUID playerUuid) {
        for (Match match : hostedMatches) {
            if (match.isSpectator(playerUuid)) {
                return match;
            }

            for (MatchTeam team : match.getTeams()) {
                if (team.getAliveMembers().contains(playerUuid)) {
                    return match;
                }
            }
        }

        return null;
    }

    /**
     * Checks if the player specified is playing a match.
     * See {@link MatchHandler#getMatchPlaying(UUID)} for a definition
     * of the term playing.
     * @param player player to look up match for
     * @return if a match is being played by the provided player
     */
    public boolean isPlayingMatch(Player player) {
        return isPlayingMatch(player.getUniqueId());
    }

    /**
     * Checks if the player specified is spectating a match.
     * See {@link MatchHandler#getMatchSpectating(UUID)} (UUID)} for a
     * definition of the term spectating.
     * @param player player to look up match for
     * @return if a match is being spectated by the provided player
     */
    public boolean isSpectatingMatch(Player player) {
        return isSpectatingMatch(player.getUniqueId());
    }

    /**
     * Checks if the player specified is playing or spectating a match.
     * See {@link MatchHandler#getMatchPlayingOrSpectating(UUID)} (UUID)} for a definition
     * of the term playing or spectating.
     * @param player player to look up match for
     * @return if a match is being played or spectated by the provided player
     */
    public boolean isPlayingOrSpectatingMatch(Player player) {
        return isPlayingOrSpectatingMatch(player.getUniqueId());
    }

    /**
     * Checks if the player specified is playing a match.
     * See {@link MatchHandler#getMatchPlaying(UUID)} for a definition
     * of the term playing.
     * @param playerUuid player to look up match for
     * @return if a match is being played by the provided player
     */
    public boolean isPlayingMatch(UUID playerUuid) {
        return getMatchPlaying(playerUuid) != null;
    }

    /**
     * Checks if the player specified is spectating a match.
     * See {@link MatchHandler#getMatchSpectating(UUID)} (UUID)} for a
     * definition of the term spectating.
     * @param playerUuid player to look up match for
     * @return if a match is being spectated by the provided player
     */
    public boolean isSpectatingMatch(UUID playerUuid) {
        return getMatchSpectating(playerUuid) != null;
    }

    /**
     * Checks if the player specified is playing or spectating a match.
     * See {@link MatchHandler#getMatchPlayingOrSpectating(UUID)} (UUID)} for a definition
     * of the term playing or spectating.
     * @param playerUuid player to look up match for
     * @return if a match is being played or spectated by the provided player
     */
    public boolean isPlayingOrSpectatingMatch(UUID playerUuid) {
        return getMatchPlayingOrSpectating(playerUuid) != null;
    }

}