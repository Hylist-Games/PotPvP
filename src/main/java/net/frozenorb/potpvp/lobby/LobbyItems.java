package net.frozenorb.potpvp.lobby;

import net.frozenorb.potpvp.util.ItemUtils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

import static net.frozenorb.potpvp.PotPvPLang.LEFT_ARROW;
import static net.frozenorb.potpvp.PotPvPLang.RIGHT_ARROW;
import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.DARK_PURPLE;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

// Static imports generally harm readability but
// in this case it's too unreadable without them
@UtilityClass
public final class LobbyItems {

    public static final ItemStack OTHER_PARTIES_ITEM = new ItemStack(Material.SKULL_ITEM);
    public static final ItemStack PENDING_INVITES_ITEM = new ItemStack(Material.WATCH);

    public static final ItemStack REQUEST_REMATCH_ITEM = new ItemStack(Material.DIAMOND);
    public static final ItemStack SENT_REMATCH_ITEM = new ItemStack(Material.EMERALD);
    public static final ItemStack ACCEPT_REMATCH_ITEM = new ItemStack(Material.EMERALD);

    public static final ItemStack START_TEAM_SPLIT_ITEM = new ItemStack(Material.DIAMOND_SWORD);

    public static final ItemStack EVENTS_ITEM = new ItemStack(Material.EMERALD);

    static {
        ItemUtils.setDisplayName(OTHER_PARTIES_ITEM, LEFT_ARROW + GREEN.toString() + BOLD + "Other Parties" + RIGHT_ARROW);
        ItemUtils.setDisplayName(PENDING_INVITES_ITEM, LEFT_ARROW + YELLOW.toString() + BOLD + "Pending Invites" + RIGHT_ARROW);

        ItemUtils.setDisplayName(REQUEST_REMATCH_ITEM, LEFT_ARROW + DARK_PURPLE.toString() + BOLD + "Request Rematch" + RIGHT_ARROW);
        ItemUtils.setDisplayName(SENT_REMATCH_ITEM, LEFT_ARROW + GREEN.toString() + BOLD + "Sent Rematch" + RIGHT_ARROW);
        ItemUtils.setDisplayName(ACCEPT_REMATCH_ITEM, LEFT_ARROW + GREEN.toString() + BOLD + "Accept Rematch" + RIGHT_ARROW);

        ItemUtils.setDisplayName(START_TEAM_SPLIT_ITEM, LEFT_ARROW + YELLOW.toString() + BOLD + "Start Team Split" + RIGHT_ARROW);

        ItemUtils.setDisplayName(EVENTS_ITEM, LEFT_ARROW + LIGHT_PURPLE.toString() + BOLD + "Events" + RIGHT_ARROW);
    }

}