package net.frozenorb.potpvp.util;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchUtility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class InventoryUtils {

    public static void resetInventoryLater(Player player, int ticks) {
        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> resetInventory(player), ticks);
    }

    public static void resetInventory(Player player) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (matchHandler.isPlayingOrSpectatingMatch(player)) {
            MatchUtility.resetInventory(player);
        } else {
            //LobbyHandler.resetInventory(player);
        }
    }

}