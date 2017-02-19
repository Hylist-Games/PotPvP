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
            for (UUID member : team.getAliveMembers()) {
                playerData.put(member, matchPlayers);
            }
        }

        for (UUID spectator : match.getSpectators()) {
            playerData.put(spectator, matchPlayers);
        }
    }

    private void messagePlayers(Match match) {
        Map<UUID, TextComponent[][]> messages = new HashMap<>();
        List<MatchTeam> teams = match.getTeams();

        // matches with 2 teams get relational sections,
        // any other match just gets a big 'participants' section
        if (teams.size() == 2) {
            MatchTeam team1 = teams.get(0);
            MatchTeam team2 = teams.get(1);

            TextComponent[][] team1Messages = PostMatchInvLang.teamMessages(team1, team2);
            TextComponent[][] team2Messages = PostMatchInvLang.teamMessages(team2, team1);
            TextComponent[][] spectatorMessages = PostMatchInvLang.spectatorMessages(team1, team2);

            // we specifically call spectators first so anyone who was in a team
            // gets their messages overriden by their relational messages.
            // we have to have the if + use getAllMembers() to ensure all members get
            // their messagtes overriden, not just those alive at the end of the match
            for (UUID spectator : match.getSpectators()) {
                messages.put(spectator, spectatorMessages);
            }

            for (UUID member : team1.getAllMembers()) {
                if (messages.containsKey(member) || team1.isAlive(member)) {
                    messages.put(member, team1Messages);
                }
            }

            for (UUID member : team2.getAllMembers()) {
                if (messages.containsKey(member) || team2.isAlive(member)) {
                    messages.put(member, team2Messages);
                }
            }
        } else {
            TextComponent[][] generic = PostMatchInvLang.genericMessages(teams);

            for (UUID spectator : match.getSpectators()) {
                messages.put(spectator, generic);
            }

            for (MatchTeam team : teams) {
                for (UUID member : team.getAliveMembers()) {
                    messages.put(member, generic);
                }
            }
        }

        messages.forEach((uuid, lines) -> {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                for (TextComponent[] line : lines) {
                    player.spigot().sendMessage(line);
                }
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