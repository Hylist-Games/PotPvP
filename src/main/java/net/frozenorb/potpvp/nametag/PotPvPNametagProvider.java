package net.frozenorb.potpvp.nametag;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.qlib.nametag.NametagInfo;
import net.frozenorb.qlib.nametag.NametagProvider;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public final class PotPvPNametagProvider extends NametagProvider {

    public PotPvPNametagProvider() {
        super("PotPvP Provider", 5);
    }

    @Override
    public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
        ChatColor prefixColor = getNameColor(toRefresh, refreshFor);
        return createNametag(prefixColor.toString(), "");
    }

    // in comments in this method we refer to 'toRefresh' as 'them' and
    // 'refreshFor' as 'us' to make the relationships portrayed less complicated
    public static ChatColor getNameColor(Player toRefresh, Player refreshFor) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match toRefreshMatch = matchHandler.getMatchPlayingOrSpectating(toRefresh);

        // they're not in a match with us, so (in theory) the only way
        // we can see them is if they're in a party with them.
        // TODO: Make this better
        if (toRefreshMatch == null) {
            return ChatColor.BLUE;
        }

        MatchTeam toRefreshTeam = toRefreshMatch.getTeam(toRefresh.getUniqueId());

        // they're a spectator, so we see them as gray
        if (toRefreshTeam == null) {
            return ChatColor.GRAY;
        }

        MatchTeam refreshForTeam = toRefreshMatch.getTeam(refreshFor.getUniqueId());

        // if we can't find a current team, check if they have any
        // previously teams we can use for this
        if (refreshForTeam == null) {
            refreshForTeam = toRefreshMatch.getPreviousTeam(refreshFor.getUniqueId());
        }

        // if we were/are both on teams display a friendly/enemy color
        if (refreshForTeam != null) {
            return toRefreshTeam == refreshForTeam ? ChatColor.GREEN : ChatColor.RED;
        }

        // if we're a spectator just display standard colors
        List<MatchTeam> teams = toRefreshMatch.getTeams();

        // we have predefined colors for 'normal' matches
        if (teams.size() == 2) {
            // team 1 = LIGHT_PURPLE, team 2 = AQUA
            if (toRefreshTeam == teams.get(0)) {
                return ChatColor.LIGHT_PURPLE;
            } else {
                return ChatColor.AQUA;
            }
        } else {
            // we don't have colors defined for larger matches
            // everyone is just red for spectators
            return ChatColor.RED;
        }
    }

}