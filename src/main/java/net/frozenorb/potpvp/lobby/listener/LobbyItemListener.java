package net.frozenorb.potpvp.lobby.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.lobby.LobbyItems;

import net.frozenorb.potpvp.lobby.menu.SpectateMenu;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchState;
import net.frozenorb.potpvp.util.ItemListener;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import net.frozenorb.qlib.qLib;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public final class LobbyItemListener extends ItemListener {

    public LobbyItemListener() {
        addHandler(LobbyItems.EVENTS_ITEM, p -> p.sendMessage(ChatColor.RED + "Events are not yet completed! They will be done soon!"));
        addHandler(LobbyItems.SPECTATE_MENU_ITEM, player -> {
            if (PotPvPValidation.canSpectateIgnoreMatchSpectating(player)) {
                new SpectateMenu().openMenu(player);
            }
        });
        addHandler(LobbyItems.SPECTATE_RANDOM_ITEM, player -> {
            MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

            if (!PotPvPValidation.canSpectateIgnoreMatchSpectating(player)) {
                return;
            }

            List<Match> matches = new ArrayList<>(matchHandler.getHostedMatches());
            matches.removeIf(m -> m.isSpectator(player.getUniqueId()) || m.getState() == MatchState.ENDING || m.getTeams().size() != 2);

            if (matches.isEmpty()) {
                player.sendMessage(ChatColor.RED + "There are no matches available to spectate.");
            } else {
                Match currentlySpectating = matchHandler.getMatchSpectating(player);

                if (currentlySpectating != null) {
                    currentlySpectating.removeSpectator(player);
                }

                Match target = matches.get(qLib.RANDOM.nextInt(matches.size()));
                target.addSpectator(player, null);
            }
        });
    }

}