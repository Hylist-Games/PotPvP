package net.frozenorb.potpvp.matchstats;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.qlib.qLib;

import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class MatchStatsHandler {

    // match id -> (match participant -> statistics at time of death)
    private final Map<String, Map<UUID, PlayerStats>> matchStats = new ConcurrentHashMap<>();

    private final Map<UUID, Integer> swings = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> swingHits = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> bowShots = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> bowHits = new ConcurrentHashMap<>();

    public MatchStatsHandler() {
        Bukkit.getPluginManager().registerEvents(new MatchStatsListener(this), PotPvPSI.getInstance());
    }

    void saveStatistics(UUID playerUuid, Match match) {
        matchStats.computeIfAbsent(match.getId(), (i) -> new ConcurrentHashMap<>());
        Map<UUID, PlayerStats> playerStats = matchStats.get(match.getId());

        playerStats.put(playerUuid, getPlayerStats(playerUuid));
    }

    void finalizeStatistics(Match match) {
        // grab existing statistics from dead players
        matchStats.computeIfAbsent(match.getId(), (i) -> new ConcurrentHashMap<>());
        Map<UUID, PlayerStats> playerStats = matchStats.get(match.getId());

        // record statistics for players who lived until the end of the match
        for (MatchTeam team : match.getTeams()) {
            for (UUID member : team.getAllMembers()) {
                playerStats.computeIfAbsent(member, this::getPlayerStats);
            }
        }
    }

    void contributeDatabaseInfo(Match match, Document databaseInfo) {
        Map<UUID, PlayerStats> playerStats = matchStats.remove(match.getId());
        Document playerStatsJson = Document.parse(qLib.GSON.toJson(playerStats));

        databaseInfo.put("playerStats", playerStatsJson);
    }

    void recordSwing(UUID playerUuid) {
        int existing = swings.getOrDefault(playerUuid, 0);
        swings.put(playerUuid, existing + 1);
    }

    void recordSwingHit(UUID playerUuid) {
        int existing = swingHits.getOrDefault(playerUuid, 0);
        swingHits.put(playerUuid, existing + 1);
    }

    void recordBowShot(UUID playerUuid) {
        int existing = bowShots.getOrDefault(playerUuid, 0);
        bowShots.put(playerUuid, existing + 1);
    }

    void recordBowHit(UUID playerUuid) {
        int existing = bowHits.getOrDefault(playerUuid, 0);
        bowHits.put(playerUuid, existing + 1);
    }

    void resetPlayerStats(UUID playerUuid) {
        swings.remove(playerUuid);
        swingHits.remove(playerUuid);
        bowShots.remove(playerUuid);
        bowHits.remove(playerUuid);
    }


    PlayerStats getOrCreateStats(UUID playerUuid, Match context) {

    }

}