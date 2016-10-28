package net.frozenorb.potpvp.postmatchinv;

import com.google.common.collect.ImmutableMap;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.postmatchinv.listener.PostMatchInvGeneralListener;
import net.frozenorb.qlib.util.UUIDUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PostMatchInvHandler {

    // uuid -> their "view" of their last match
    // this varies per player, so we must store them all individually
    private final Map<UUID, Map<UUID, PostMatchPlayer>> postMatchData = new ConcurrentHashMap<>();

    public PostMatchInvHandler() {
        Bukkit.getPluginManager().registerEvents(new PostMatchInvGeneralListener(), PotPvPSI.getInstance());
    }

    public void registerInventories(Match match) {
        Map<UUID, PostMatchPlayer> postMatchPlayers = match.getPostMatchPlayers();

        for (MatchTeam team : match.getTeams()) {
            for (UUID member : team.getAliveMembers()) {
                postMatchData.put(member, postMatchPlayers);
            }
        }

        for (UUID spectator : match.getSpectators()) {
            postMatchData.put(spectator, postMatchPlayers);
        }

        /*if (match.getTe)

        EndedTeamData team1 = endedMatch.teamData().get(0);
        EndedTeamData team2 = endedMatch.teamData().get(0);

        TextComponent[] team1InvButtons = createInventoryComponents(team1);
        TextComponent[] team2InvButtons = createInventoryComponents(team2);

        BaseComponent[][] spectatorMessages = createSpectatorMessages(team1InvButtons, team2InvButtons);
        BaseComponent[][] team1Messages = createTeamMessages(team1InvButtons, team2InvButtons);
        BaseComponent[][] team2Messages = createTeamMessages(team2InvButtons, team1InvButtons);

        Map<UUID, BaseComponent[][]> messages = new HashMap<>();

        for (UUID specPlayer : endedMatch.match().spectators()) {
            messages.put(specPlayer, spectatorMessages);
        }

        for (EndedPlayer team1Player : team1.players()) {
            messages.put(team1Player.id(), team1Messages);
        }

        for (EndedPlayer team2Player : team2.players()) {
            messages.put(team2Player.id(), team2Messages);
        }

        queuedMessages.putAll(messages);

        // expire queued messages after 30 seconds (so we don't
        // confuse players if their join failed and they eventually
        // join this lobby later)
        Bukkit.getScheduler().runTaskLater(PotPvPPlugin.getInstance(), () -> messages.forEach(queuedMessages::remove), 30 * 20L);*/
    }

    public Map<UUID, PostMatchPlayer> getPostMatchData(UUID forWhom) {
        return postMatchData.getOrDefault(forWhom, ImmutableMap.of());
    }

}