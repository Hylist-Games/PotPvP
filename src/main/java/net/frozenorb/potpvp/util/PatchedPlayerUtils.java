package net.frozenorb.potpvp.util;

import net.frozenorb.qlib.util.PlayerUtils;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

// we mess with fly mode in PotPvP, so we need to reset that with PlayerUtils (in qLib)
// unfortunately, that class doesn't reset fly mode - and plugins like qHub, which use doublejump
// (implemented with fly mode if you're not familiar) have already started using that method.
// in order to avoid scanning the entire codebase for usages, we just wrap qLib's method with our own
// utility class
public class PatchedPlayerUtils {

    public static void resetInventory(Player player) {
        resetInventory(player, null);
    }

    public static void resetInventory(Player player, GameMode gameMode) {
        PlayerUtils.resetInventory(player, gameMode);

        player.setAllowFlight(false);
        player.setFlying(false);
    }

}