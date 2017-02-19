package net.frozenorb.potpvp.lobby.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.lobby.LobbyItems;
import net.frozenorb.potpvp.lobby.menu.SpectateMenu;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchState;
import net.frozenorb.potpvp.util.ItemListener;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import net.frozenorb.qlib.qLib;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class LobbyItemListener extends ItemListener {

    private final Map<UUID, Long> canUseRandomSpecItem = new HashMap<>();

    public LobbyItemListener(LobbyHandler lobbyHandler) {
        addHandler(LobbyItems.EVENTS_ITEM, p -> p.sendMessage(ChatColor.RED + "Events are not yet completed! They will be done soon!"));
        addHandler(LobbyItems.DISABLE_SPEC_MODE_ITEM, p -> lobbyHandler.setSpectatorMode(p, false));

        addHandler(LobbyItems.SPECTATE_MENU_ITEM, player -> {
            if (PotPvPValidation.canUseSpectateItemIgnoreMatchSpectating(player)) {
                new SpectateMenu().openMenu(player);
            }
        });

        addHandler(LobbyItems.ENABLE_SPEC_MODE_ITEM, player -> {
            if (PotPvPValidation.canUseSpectateItem(player)) {
                lobbyHandler.setSpectatorMode(player, true);
            }
        });

        addHandler(LobbyItems.SPECTATE_RANDOM_ITEM, player -> {
            MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

            if (!PotPvPValidation.canUseSpectateItemIgnoreMatchSpectating(player)) {
                return;
            }

            if (canUseRandomSpecItem.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis()) {
                player.sendMessage(ChatColor.RED + "Please wait before spectating again.");
                return;
            }

            List<Match> matches = new ArrayList<>(matchHandler.getHostedMatches());
            matches.removeIf(m -> m.isSpectator(player.getUniqueId()) || m.getState() == MatchState.ENDING);

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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        canUseRandomSpecItem.remove(event.getPlayer().getUniqueId());
    }

}