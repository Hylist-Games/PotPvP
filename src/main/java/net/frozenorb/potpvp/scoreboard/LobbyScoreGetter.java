package net.frozenorb.potpvp.scoreboard;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.elo.EloHandler;
import net.frozenorb.potpvp.event.Event;
import net.frozenorb.potpvp.follow.FollowHandler;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.queue.MatchQueue;
import net.frozenorb.potpvp.queue.MatchQueueEntry;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.qlib.autoreboot.AutoRebootHandler;
import net.frozenorb.qlib.util.TimeUtils;
import net.frozenorb.qlib.util.UUIDUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiConsumer;

final class LobbyScoreGetter implements BiConsumer<Player, List<String>> {

    @Override
    public void accept(Player player, List<String> scores) {
        FollowHandler followHandler = PotPvPSI.getInstance().getFollowHandler();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        EloHandler eloHandler = PotPvPSI.getInstance().getEloHandler();

        Party party = partyHandler.getParty(player);

        if (party != null) {
            scores.add("&9&lYour Party: &f" + party.getMembers().size());
        }

        scores.add("&eOnline: *&f" + Bukkit.getOnlinePlayers().size());
        scores.add("&dIn Fights: *&f" + matchHandler.countPlayersPlayingInProgressMatches());
        scores.add("&bIn Queues: *&f" + queueHandler.getQueuedCount());

        followHandler.getFollowing(player).ifPresent(following -> {
            scores.add("&6Following: *&f" + UUIDUtils.name(following));
        });

        MatchQueueEntry entry;

        if (party != null) {
           entry = queueHandler.getQueueEntry(party);
        } else {
            entry = queueHandler.getQueueEntry(player.getUniqueId());
        }

        if (entry != null) {
            String waitTimeFormatted = TimeUtils.formatIntoMMSS(entry.getWaitSeconds());
            MatchQueue queue = entry.getQueue();

            scores.add("&b&7&m--------------------");
            scores.add(queue.getKitType().getDisplayColor() + (queue.isRanked() ? "Ranked" : "Unranked") + " " + queue.getKitType().getDisplayName());
            scores.add("&eTime: *&f" + waitTimeFormatted);

            if (queue.isRanked()) {
                int elo = eloHandler.getElo(entry.getMembers(), queue.getKitType());
                int window = entry.getWaitSeconds() * QueueHandler.RANKED_WINDOW_GROWTH_PER_SECOND;

                scores.add("&eSearch range: *&f" + Math.max(0, elo - window) + " - " + (elo + window));
            }
        }

        if (AutoRebootHandler.isRebooting()) {
            String secondsStr = TimeUtils.formatIntoMMSS(AutoRebootHandler.getRebootSecondsRemaining());
            scores.add("&c&lRebooting: &c" + secondsStr);
        }

        if (player.hasMetadata("ModMode")) {
            scores.add(ChatColor.GRAY.toString() + ChatColor.BOLD + "In silent mode");
        }

        Event nearest = eventHandler.getNearestEvent();

        if (nearest != null) {
            scores.add("&l&7&m--------------------");
            scores.add("&b&l" + nearest.getType().getName());
            scores.add("&f  Starts in &b&l" + TimeUtils.formatIntoMMSS(nearest.getCountdown()));
            scores.add("&f  Join via emerald in hotbar");
        }
    }

}