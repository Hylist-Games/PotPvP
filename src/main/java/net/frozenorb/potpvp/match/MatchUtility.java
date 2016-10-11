package net.frozenorb.potpvp.match;

import net.frozenorb.potpvp.common.setting.Setting;
import net.frozenorb.potpvp.PotPvPSlave;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class MatchUtility {

    // spectator items
    public static final ItemStack RED_CARPET_ITEM = new ItemStack(Material.CARPET, 1, DyeColor.RED.getWoolData());
    public static final ItemStack LOBBY_FIRE_ITEM = new ItemStack(Material.FIRE);
    public static final ItemStack TOGGLE_SPECTATORS_ITEM = new ItemStack(Material.EMERALD);
    public static final ItemStack VIEW_INV_ITEM = new ItemStack(Material.BOOK);

    static {
        ItemMeta lobbyFireItemMeta = LOBBY_FIRE_ITEM.getItemMeta();
        ItemMeta toggleSpectatorsItemMeta = TOGGLE_SPECTATORS_ITEM.getItemMeta();
        ItemMeta viewInvItemMeta = VIEW_INV_ITEM.getItemMeta();

        lobbyFireItemMeta.setDisplayName(ChatColor.YELLOW + "Return to lobby");
        toggleSpectatorsItemMeta.setDisplayName(ChatColor.YELLOW + "Toggle spectator visibility");
        viewInvItemMeta.setDisplayName(ChatColor.YELLOW + "View player inventory");

        LOBBY_FIRE_ITEM.setItemMeta(lobbyFireItemMeta);
        TOGGLE_SPECTATORS_ITEM.setItemMeta(toggleSpectatorsItemMeta);
        VIEW_INV_ITEM.setItemMeta(viewInvItemMeta);
    }

    public static void resetInventory(Player player) {
        Match match = PotPvPSlave.getInstance().getMatchHandler().getMatchSpectating(player.getUniqueId());

        if (match == null) {
            return;
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        // if they've been on any team or are staff they'll be able to
        // use this item on at least 1 player. if they can't use it all
        // we just don't give it to them (UX purposes)
        boolean giveViewInvItem = player.hasPermission("basic.staff");

        if (!giveViewInvItem) {
            for (MatchTeam team : match.getTeams()) {
                if (team.getAllMembers().contains(player.getUniqueId())) {
                    giveViewInvItem = true;
                    break;
                }
            }
        }

        // fill inventory with spectator items
        player.getInventory().setItem(0, MatchUtility.RED_CARPET_ITEM);
        player.getInventory().setItem(1, MatchUtility.TOGGLE_SPECTATORS_ITEM);

        if (giveViewInvItem) {
            player.getInventory().setItem(2, MatchUtility.VIEW_INV_ITEM);
        }

        player.getInventory().setItem(8, MatchUtility.LOBBY_FIRE_ITEM);

        Bukkit.getScheduler().runTaskLater(PotPvPSlave.getInstance(), player::updateInventory, 1L);
    }

    public static void updateVisibility(Player updateFor) {
        Match updateForMatch = PotPvPSlave.getInstance().getMatchHandler().getMatchPlayingOrSpectating(updateFor.getUniqueId());

        if (updateForMatch == null) {
            return;
        }

        // reads as "updateFor is spectator?"
        boolean updateForIsSpectator = updateForMatch.isSpectator(updateFor.getKiller().getUniqueId());

        for (Player otherPlayer : PotPvPSlave.getInstance().getServer().getOnlinePlayers()) {
            // we don't care about if we can see ourself
            if (updateFor == otherPlayer) {
                continue;
            }

            MatchTeam otherPlayerTeam = updateForMatch.getCurrentTeam(otherPlayer);
            boolean otherPlayerIsSpectator = updateForMatch.isSpectator(otherPlayer.getUniqueId());

            // we're not in a match together, don't show each other
            if (otherPlayerTeam == null && !otherPlayerIsSpectator) {
                updateFor.hidePlayer(otherPlayer);
                otherPlayer.hidePlayer(updateFor);
                continue;
            }

            if (updateForIsSpectator) {
                // we're a spectator

                if (otherPlayerIsSpectator) {
                    // we're a spectator, show us them based on our settings
                    if (PotPvPSlave.getInstance().getSettingHandler().isSettingEnabled(updateFor.getUniqueId(), Setting.VIEW_OTHER_SPECTATORS)) {
                        updateFor.showPlayer(otherPlayer);
                    } else {
                        updateFor.hidePlayer(otherPlayer);
                    }

                    // they're a spectator, show them us based on their settings
                    if (PotPvPSlave.getInstance().getSettingHandler().isSettingEnabled(otherPlayer.getUniqueId(), Setting.VIEW_OTHER_SPECTATORS)) {
                        otherPlayer.showPlayer(updateFor);
                    } else {
                        otherPlayer.hidePlayer(updateFor);
                    }
                } else {
                    // they're in the match, show us them
                    // we're a spectator and they're in the match, don't show them us
                    updateFor.showPlayer(otherPlayer);
                    otherPlayer.hidePlayer(updateFor);
                }
            } else {
                // we're playing in the match

                if (otherPlayerIsSpectator) {
                    // they're a spectator,  don't show us them
                    // we're in the match and they're a spectator, show them us
                    updateFor.hidePlayer(otherPlayer);
                    otherPlayer.showPlayer(updateFor);
                } else {
                    // we're both in the match, show each other
                    updateFor.showPlayer(otherPlayer);
                    otherPlayer.showPlayer(updateFor);
                }
            }
        }
    }

}