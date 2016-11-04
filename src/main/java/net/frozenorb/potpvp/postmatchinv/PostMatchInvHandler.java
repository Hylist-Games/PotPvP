package net.frozenorb.potpvp.postmatchinv;

import com.google.common.collect.ImmutableMap;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.postmatchinv.listener.PostMatchInvGeneralListener;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PostMatchInvHandler {

    // uuid -> their "view" of their last match
    // this varies per player, so we must store them all individually
    private final Map<UUID, Map<UUID, PostMatchPlayer>> playerData = new ConcurrentHashMap<>();

    public PostMatchInvHandler() {
        Bukkit.getPluginManager().registerEvents(new PostMatchInvGeneralListener(), PotPvPSI.getInstance());
    }

    public void recordMatch(Match match) {
        saveInventories(match);
        messagePlayers(match);
    }

    private void saveInventories(Match match) {
        Map<UUID, PostMatchPlayer> matchPlayers = match.getPostMatchPlayers();

        for (MatchTeam team : match.getTeams()) {
            for (UUID member : team.getAllMembers()) {
                playerData.put(member, matchPlayers);
            }
        }

        for (UUID spectator : match.getSpectators()) {
            playerData.put(spectator, matchPlayers);
        }
    }

    private void messagePlayers(Match match) {
        List<MatchTeam> teams = match.getTeams();

        if (teams.size() != 2) {
            return;
        }

        MatchTeam team1 = teams.get(0);
        MatchTeam team2 = teams.get(1);

        TextComponent[][] team1Messages = PostMatchInvLang.teamMessages(team1, team2);
        TextComponent[][] team2Messages = PostMatchInvLang.teamMessages(team2, team1);
        TextComponent[][] spectatorMessages = PostMatchInvLang.spectatorMessages(team1, team2);

        Map<UUID, TextComponent[][]> messages = new HashMap<>();

        // we specifically call spectators first so anyone who was in a team
        // gets their messages overriden by their relational messages
        match.getSpectators().forEach(p -> messages.put(p, spectatorMessages));
        team1.getAllMembers().forEach(p -> messages.put(p, team1Messages));
        team2.getAllMembers().forEach(p -> messages.put(p, team2Messages));

        // used to avoid repeating these couple lines 3 times
        messages.forEach((player, lines) -> {
            Player playerBukkit = Bukkit.getPlayer(player);

            for (TextComponent[] line : lines) {
                playerBukkit.spigot().sendMessage(line);
            }
        });
    }

    public Map<UUID, PostMatchPlayer> getPostMatchData(UUID forWhom) {
        return playerData.getOrDefault(forWhom, ImmutableMap.of());
    }

    public void removePostMatchData(UUID forWhom) {
        playerData.remove(forWhom);
    }

}