package net.frozenorb.potpvp.match.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.match.SpectatorItems;
import net.frozenorb.potpvp.match.command.LeaveCommand;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class SpectatorItemListener implements Listener {

    private static final long TOGGLE_SPECTATORS_COOLDOWN_MILLIS = TimeUnit.SECONDS.toMillis(3);

    private final Map<UUID, Long> toggleVisiblityUsable = new ConcurrentHashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchSpectating(event.getPlayer());

        if (match == null) {
            return;
        }

        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        // carpet item does nothing, no point having an empty if block
        // view inventory item is handled in PlayerInteractEntityEvent

        if (item.isSimilar(SpectatorItems.RETURN_TO_LOBBY_ITEM)) {
            LeaveCommand.leave(player);
        } else if (item.isSimilar(SpectatorItems.TOGGLE_SPECTATORS_ITEM)) {
            SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
            UUID playerUuid = player.getUniqueId();
            boolean togglePermitted = toggleVisiblityUsable.getOrDefault(playerUuid, 0L) < System.currentTimeMillis();

            if (!togglePermitted) {
                player.sendMessage(ChatColor.RED + "Please wait before doing this again!");
                return;
            }

            boolean enabled = !settingHandler.getSetting(playerUuid, Setting.VIEW_OTHER_SPECTATORS);
            settingHandler.updateSetting(playerUuid, Setting.VIEW_OTHER_SPECTATORS, enabled);

            if (enabled) {
                player.sendMessage(ChatColor.GREEN + "Now showing other spectators.");
            } else {
                player.sendMessage(ChatColor.RED + "Now hiding other spectators.");
            }

            toggleVisiblityUsable.put(playerUuid, System.currentTimeMillis() + TOGGLE_SPECTATORS_COOLDOWN_MILLIS);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match clickerMatch = matchHandler.getMatchSpectating(event.getPlayer());
        Player clicker = event.getPlayer();

        if (clickerMatch == null || !clicker.getItemInHand().isSimilar(SpectatorItems.VIEW_INVENTORY_ITEM)) {
            return;
        }

        Player clicked = (Player) event.getRightClicked();
        MatchTeam clickedTeam = clickerMatch.getTeam(clicked.getUniqueId());

        // should only happen when clicking other spectators
        if (clickedTeam == null) {
            clicker.sendMessage(ChatColor.RED + "Cannot view inventory of " + clicked.getName());
            return;
        }

        boolean bypassPerm = clicker.hasPermission("potpvp.inventory.all");
        boolean sameTeam = clickedTeam.getAllMembers().contains(clicked.getUniqueId());

        if (bypassPerm || sameTeam) {
            clicker.sendMessage(ChatColor.AQUA + "Opening inventory of: " + clicked.getName());
            clicker.openInventory(clicked.getInventory());
        } else {
            clicker.sendMessage(ChatColor.RED + clicked.getName() + " is not on your team.");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        toggleVisiblityUsable.remove(event.getPlayer().getUniqueId());
    }

}