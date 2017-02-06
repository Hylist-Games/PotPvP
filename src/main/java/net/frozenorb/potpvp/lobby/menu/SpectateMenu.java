package net.frozenorb.potpvp.lobby.menu;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchState;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.pagination.PaginatedMenu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class SpectateMenu extends PaginatedMenu {

    public SpectateMenu() {
        setAutoUpdate(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Spectate a match";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = 0;

        for (Match match : PotPvPSI.getInstance().getMatchHandler().getHostedMatches()) {
            // players can view this menu while spectating
            if (match.isSpectator(player.getUniqueId())) {
                continue;
            }

            if (match.getTeams().size() != 2 || match.getState() == MatchState.ENDING) {
                continue;
            }

            buttons.put(i++, new SpectateButton(match));
        }

        return buttons;
    }

}