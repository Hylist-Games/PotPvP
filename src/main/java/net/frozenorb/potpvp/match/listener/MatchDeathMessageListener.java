package net.frozenorb.potpvp.match.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.nametag.PotPvPNametagProvider;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public final class MatchDeathMessageListener implements Listener {

    private static final String NO_KILLER_MESSAGE = ChatColor.translateAlternateColorCodes('&', "%s&7 died.");
    private static final String KILLED_BY_OTHER_MESSAGE = ChatColor.translateAlternateColorCodes('&', "%s&7 killed %s&f.");

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getEntity());

        if (match == null) {
            return;
        }

        Player killed = event.getEntity();
        Player killer = killed.getKiller();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            UUID onlinePlayerUuid = onlinePlayer.getUniqueId();

            // if this player has no relation to the match skip
            if (match.getTeam(onlinePlayerUuid) == null && !match.isSpectator(onlinePlayerUuid)) {
                continue;
            }

            ChatColor killedNameColor = PotPvPNametagProvider.getNameColor(killed, onlinePlayer);
            String killedFormattedName = killedNameColor + killed.getName();

            // if the killer died before the player did we just pretend they weren't
            // involved (their name would show up as a spectator, which would be confusing
            // for players)
            if (killer == null || match.isSpectator(killer.getUniqueId())) {
                onlinePlayer.sendMessage(String.format(NO_KILLER_MESSAGE, killedFormattedName));
            } else {
                ChatColor killerNameColor = PotPvPNametagProvider.getNameColor(killer, onlinePlayer);
                String killerFormattedName = killerNameColor + killer.getName();

                onlinePlayer.sendMessage(String.format(KILLED_BY_OTHER_MESSAGE, killerFormattedName, killedFormattedName));
            }
        }
    }

}