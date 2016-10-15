package net.frozenorb.potpvp;

import org.bukkit.ChatColor;

import lombok.experimental.UtilityClass;

/**
 * Constants for messages/strings commonly shown to players
 */
@UtilityClass
public final class PotPvPLang {

    /**
     * `&9&l» ` - Arrow used on the left side of item display names
     * Named left arrow due to its usage on the left side of items, despite the fact
     * the arrow is actually pointing to the right.
     * @see net.frozenorb.potpvp.lobby.LobbyItems usage
     * @see net.frozenorb.potpvp.queue.QueueItems usage
     */
    public static final String LEFT_ARROW = ChatColor.BLUE.toString() + ChatColor.BOLD + "» ";

    /**
     * ` &9&l«` - Arrow used on the right side of item display names
     * Named right arrow due to its usage on the right side of items, despite the fact
     * the arrow is actually pointing to the left.
     * @see net.frozenorb.potpvp.lobby.LobbyItems usage
     * @see net.frozenorb.potpvp.queue.QueueItems usage
     */
    public static final String RIGHT_ARROW = " " + ChatColor.BLUE.toString() + ChatColor.BOLD + "«";

}