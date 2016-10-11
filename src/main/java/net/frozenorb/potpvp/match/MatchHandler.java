package net.frozenorb.potpvp.match;

import com.google.common.collect.ImmutableSet;

import net.frozenorb.potpvp.PotPvPSI;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.Setter;

public final class MatchHandler {

    private final Set<Match> hostedMatches = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Getter @Setter private boolean rankedMatchesDisabled;
    @Getter @Setter private boolean quickMatchesDisabled;

    /*
    public enum MatchStartResult {

    SUCCESSFUL,
    NO_MAPS_AVAILABLE;

}

    public MatchStartResult startMatch(Set<MatchTeam> teams, Set<UUID> initialSpectators, KitType kitType) {
        Match match = new Match();

        match.setState(MatchState.WAITING_FOR_PLAYERS);
        match.setId(request.getMatch().getId());
        match.setRanked(request.getMatch().isRanked());
        match.setHostedOn(PotPvPSlave.getInstance().getServerHandler().getBungeeId());
        match.setDetailedKitType(DetailedKitType.fromEncodedName(request.getMatch().getKitType()));

        // Map GCD Team models to StandardTeams
        List<MatchTeam> teams = request.getMatch().getTeams().stream().map(MatchTeam::fromGcdMatchTeam).collect(Collectors.toList());
        match.setTeams(teams);

        // Process any spectators sent with the match (usually team splits)
        for (GcdSpectator spectator : request.getMatch().getSpectators()) {
            SpectatorData spectatorData = SpectatorData.fromGcdSpectator(spectator);
            match.waitForSpectator(spectator.getPlayerUuid(), spectatorData);
        }

        List<Arena> abstractMaps = new ArrayList<>(PotPvPSI.getInstance().getMapHandler().getUnusedMaps());
        Iterator<Arena> mapIterator = abstractMaps.iterator();
        int matchSize = 0;

        for (MatchTeam team : match.getTeams()) {
            matchSize += team.getAllMembers().size();
        }

        // Calculate all potential maps
        while (mapIterator.hasNext()) {
            Arena map = mapIterator.next();
            ArenaTag mapTag = PotPvPSI.getInstance().getMapHandler().getTag(map);

            if (matchSize < mapTag.getMinPlayers() || matchSize > mapTag.getMaxPlayers()) {
                mapIterator.remove(); // map player requirements not met
            }

            if (mapTag.isArcher() && match.getKitType() != KitType.ARCHER) {
                mapIterator.remove(); // map is archer only, but this kit isnt archer
            }

            // map is unranked only, match is ranked
            if (!mapTag.isRanked() && match.isRanked()) {
                mapIterator.remove();
            }
        }

        Arena[] unusedMaps = abstractMaps.toArray(new Arena[abstractMaps.size()]);

        if (unusedMaps.length != 0) {
            return unusedMaps[qLib.RANDOM.nextInt(unusedMaps.length)];
        } else {
            return null;
        }

        if (map == null) {
            PotPvPSlave.getInstance().getLogger().severe("Failed to setup match " + request.getMatch().getId() + " - No open maps.");
            return new RequestFailedResponse("Found no open maps!");
        }

        try {
            PotPvPSlave.getInstance().getMapHandler().useMap(map); // claims this map for this match
            match.setMap(map);
            match.setupMatch();
            PotPvPSlave.getInstance().getMatchHandler().hostMatch(match);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new RequestFailedResponse(ex.getMessage());
        }

        return new InitializeMatchResponse();
    }

    public void requestSpectate(UUID target, Player spectator) {
        Match match = PotPvPSlave.getInstance().getMatchHandler().getMatchById(request.getMatchId());

        if (match != null) {
            GcdSpectator spectator = request.getSpectator();

            SpectatorData spectatorData = SpectatorData.fromGcdSpectator(spectator);
            match.waitForSpectator(spectator.getPlayerUuid(), spectatorData);

            Player spectatorPlayer = PotPvPSlave.getInstance().getServer().getPlayer(spectator.getPlayerUuid());

            if (spectatorPlayer != null) {
                spectatorPlayer.teleport(match.getMap().getSpectatorSpawn());
            }
        }

        return new SpectateMatchResponse();
    }
    */

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