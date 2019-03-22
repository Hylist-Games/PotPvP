
package net.frozenorb.potpvp.scoreboard;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.elo.EloHandler;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.queue.MatchQueue;
import net.frozenorb.potpvp.queue.MatchQueueEntry;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.qlib.autoreboot.AutoRebootHandler;
import net.frozenorb.qlib.util.LinkedList;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

final class GameScoreGetter implements BiConsumer<Player, LinkedList<String>> {

    @Override
    public void accept(Player player, LinkedList<String> scores) {
        Game game = GameQueue.INSTANCE.getCurrentGame(player);

        if (game == null) return;
        if (!game.getPlayers().contains(player)) return;

        scores.addAll(game.getEvent().getScoreboardScores(player, game));
    }

}