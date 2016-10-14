package net.frozenorb.potpvp.queue;

import net.frozenorb.potpvp.util.ItemUtils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

import static net.frozenorb.potpvp.PotPvPLang.LEFT_ARROW;
import static net.frozenorb.potpvp.PotPvPLang.RIGHT_ARROW;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

// Static imports generally harm readability but
// in this case it's too unreadable without them
@UtilityClass
public final class QueueItems {

    public static final ItemStack JOIN_SOLO_QUEUE_ITEM = new ItemStack(Material.PAPER);
    public static final ItemStack LEAVE_SOLO_QUEUE_ITEM = new ItemStack(Material.EMPTY_MAP);

    //public static final ItemStack JOIN_SOLO_RANKED_QUEUE_ITEM = new ItemStack(Material.EYE_OF_ENDER);
    //public static final ItemStack LEAVE_SOLO_RANKED_QUEUE_ITEM = new ItemStack(Material.ENDER_PEARL);

    public static final ItemStack JOIN_PARTY_QUEUE_ITEM = new ItemStack(Material.APPLE);
    public static final ItemStack LEAVE_PARTY_QUEUE_ITEM = new ItemStack(Material.ARROW);

    //public static final ItemStack JOIN_PARTY_RANKED_QUEUE_ITEM = new ItemStack(Material.GOLDEN_APPLE);
    //public static final ItemStack LEAVE_PARTY_RANKED_QUEUE_ITEM = new ItemStack(Material.ARROW);

    static {
        ItemUtils.setDisplayName(JOIN_SOLO_QUEUE_ITEM, LEFT_ARROW + AQUA.toString() + BOLD + "Join Quickmatch Queue" + RIGHT_ARROW);
        ItemUtils.setDisplayName(LEAVE_SOLO_QUEUE_ITEM, LEFT_ARROW + RED.toString() + BOLD + "Leave Quickmatch Queue" + RIGHT_ARROW);

        //ItemUtils.setDisplayName(JOIN_SOLO_RANKED_QUEUE_ITEM, LEFT_ARROW_STRING + GREEN.toString() + BOLD + "Join " + AQUA.toString() + BOLD + "Ranked " + GREEN.toString() + BOLD + "Match Queue" + RIGHT_ARROW_STRING);
        //ItemUtils.setDisplayName(LEAVE_SOLO_RANKED_QUEUE_ITEM, LEFT_ARROW_STRING + RED.toString() + BOLD + "Leave " + AQUA.toString() + BOLD + "Ranked " + RED.toString() + BOLD + "Match Queue" + RIGHT_ARROW_STRING);

        ItemUtils.setDisplayName(JOIN_PARTY_QUEUE_ITEM, LEFT_ARROW + GREEN.toString() + BOLD + "Join 2v2 Unranked" + RIGHT_ARROW);
        ItemUtils.setDisplayName(LEAVE_PARTY_QUEUE_ITEM, LEFT_ARROW + RED.toString() + BOLD + "Leave 2v2 Unranked" + RIGHT_ARROW);

        //ItemUtils.setDisplayName(JOIN_PARTY_RANKED_QUEUE_ITEM, LEFT_ARROW_STRING + GREEN.toString() + BOLD + "Join 2v2 Ranked" + RIGHT_ARROW_STRING);
        //ItemUtils.setDisplayName(LEAVE_PARTY_RANKED_QUEUE_ITEM, LEFT_ARROW_STRING + RED.toString() + BOLD + "Leave 2v2 Ranked" + RIGHT_ARROW_STRING);
    }

}