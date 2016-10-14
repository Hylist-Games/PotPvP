package net.frozenorb.potpvp.scoreboard;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.queue.QueueEntry;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.qlib.util.TimeUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiConsumer;

final class LobbyScoreGetter implements BiConsumer<Player, List<String>> {

    @Override
    public void accept(Player player, List<String> scores) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        Party party = partyHandler.getParty(player);

        if (party != null) {
            scores.add("&9&lYour Party: &f" + party.getMembers().size());
        }

        scores.add("&eOnline: *&f" + Bukkit.getOnlinePlayers().size());
        scores.add("&dIn Fights: *&f" + matchHandler.countPlayersPlayingMatches());
        scores.add("&bIn Queues: *&f" + queueHandler.countPlayersQueued());

        QueueEntry queueEntry;

        if (party != null) {
           queueEntry = queueHandler.getQueueEntry(party);
        } else {
            queueEntry = queueHandler.getQueueEntry(player.getUniqueId());
        }

        if (queueEntry != null) {
            String queueTypeFormatted = queueEntry.getQueue().getKitType().getDisplayName();
            String waitTimeFormatted = TimeUtils.formatIntoMMSS(queueEntry.getWaitTime());

            scores.add("&b&7&m--------------------");
            scores.add("&cQueued: &f" + queueTypeFormatted);
            scores.add("&bQueued for: *&f" + waitTimeFormatted);
        }
    }

}