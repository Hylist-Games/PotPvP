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
        Map<UUID, Object[]> messages = new HashMap<>();

        createMessages(match, messages);

        messages.forEach((uuid, lines) -> {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                return;
            }

            for (Object line : lines) {
                if (line instanceof TextComponent[]) {
                    player.spigot().sendMessage((TextComponent[]) line);
                } else if (line instanceof TextComponent) {
                    player.spigot().sendMessage((TextComponent) line);
                } else if (line instanceof String) {
                    player.sendMessage((String) line);
                }
            }
        });
    }

    private void createMessages(Match match, Map<UUID, Object[]> messages) {
        List<MatchTeam> teams = match.getTeams();

        if (teams.size() != 2) {
            // matches without 2 teams just get big 'participants' sections
            Object[] generic = PostMatchInvLang.genGenericMessages(teams);

            writeSpecMessages(match, messages, generic);
            writeTeamMessages(teams, messages, generic);
            return;
        }

        MatchTeam team1 = teams.get(0);
        MatchTeam team2 = teams.get(1);

        if (team1.getAllMembers().size() == 1 && team2.getAllMembers().size() == 1) {
            // 1v1 messages
            UUID player1 = team1.getAliveMembers().iterator().next();
            UUID player2 = team2.getAliveMembers().iterator().next();

            writeSpecMessages(match, messages, PostMatchInvLang.genGenericMessages(teams));
            writeTeamMessages(team1, messages, PostMatchInvLang.gen1v1PlayerMessages(player1, player2));
            writeTeamMessages(team2, messages, PostMatchInvLang.gen1v1PlayerMessages(player2, player1));
        } else {
            // normal 2 team messages
            writeSpecMessages(match, messages, PostMatchInvLang.genSpectatorMessages(team1, team2));
            writeTeamMessages(team1, messages, PostMatchInvLang.genTeamMessages(team1, team2));
            writeTeamMessages(team2, messages, PostMatchInvLang.genTeamMessages(team2, team1));
        }
    }

    private void writeTeamMessages(Iterable<MatchTeam> teams, Map<UUID, Object[]> messageMap, Object[] messages) {
        for (MatchTeam team : teams) {
            writeTeamMessages(team, messageMap, messages);
        }
    }

    private void writeTeamMessages(MatchTeam team, Map<UUID, Object[]> messageMap, Object[] messages) {
        for (UUID member : team.getAllMembers()) {
            // on this containsKey check:
            // we only want to send messages to players who are alive or were on this team in the match
            // we always add messages from the least specific (ex generic for spectators)
            // to most specific (ex per team messages), so checking if they're already added is a good
            // way to check if they're going to get a message.
            // we can't just use getAllMembers because players could've left and started a new fight
            if (messageMap.containsKey(member) || team.isAlive(member)) {
                messageMap.put(member, messages);
            }
        }
    }

    private void writeSpecMessages(Match match, Map<UUID, Object[]> messageMap, Object[] messages) {
        for (UUID spectator : match.getSpectators()) {
            messageMap.put(spectator, messages);
        }
    }

    public Map<UUID, PostMatchPlayer> getPostMatchData(UUID forWhom) {
        return playerData.getOrDefault(forWhom, ImmutableMap.of());
    }

    public void removePostMatchData(UUID forWhom) {
        playerData.remove(forWhom);
    }

}