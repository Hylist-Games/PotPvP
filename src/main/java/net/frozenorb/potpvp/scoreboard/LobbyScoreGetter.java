package net.frozenorb.potpvp.scoreboard;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.follow.FollowHandler;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.queue.MatchQueueEntry;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.qlib.util.TimeUtils;
import net.frozenorb.qlib.util.UUIDUtils;

import org.bukkit.Bukkit;
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

            scores.add("&b&7&m--------------------");
            scores.add("&fQueued for &a" + entry.getQueue().getKitType().getName());
            scores.add("&a" + (entry.getQueue().isRanked() ? "Ranked" : "Unranked"));
            scores.add("&aTime: *&f" + waitTimeFormatted);
        }
    }

}