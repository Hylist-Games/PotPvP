package net.frozenorb.potpvp.party;

import net.frozenorb.qlib.util.ItemUtils;
import net.frozenorb.qlib.util.UUIDUtils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

import static net.frozenorb.potpvp.PotPvPLang.LEFT_ARROW;
import static net.frozenorb.potpvp.PotPvPLang.RIGHT_ARROW;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

@UtilityClass
public final class PartyItems {

    public static final Material ICON_TYPE = Material.NETHER_STAR;

    public static final ItemStack LEAVE_PARTY_ITEM = new ItemStack(Material.FIRE);
    public static final ItemStack START_TEAM_SPLIT_ITEM = new ItemStack(Material.DIAMOND_SWORD);
    public static final ItemStack START_FFA_ITEM = new ItemStack(Material.GOLD_SWORD);
    public static final ItemStack OTHER_PARTIES_ITEM = new ItemStack(Material.SKULL_ITEM);

    static {
        ItemUtils.setDisplayName(LEAVE_PARTY_ITEM, LEFT_ARROW + RED.toString() + BOLD + "Leave Party" + RIGHT_ARROW);
        ItemUtils.setDisplayName(START_TEAM_SPLIT_ITEM, LEFT_ARROW + YELLOW.toString() + BOLD + "Start Team Split" + RIGHT_ARROW);
        ItemUtils.setDisplayName(START_FFA_ITEM, LEFT_ARROW + YELLOW.toString() + BOLD + "Start Party FFA" + RIGHT_ARROW);
        ItemUtils.setDisplayName(OTHER_PARTIES_ITEM, LEFT_ARROW + GREEN.toString() + BOLD + "Other Parties" + RIGHT_ARROW);
    }

    public static ItemStack icon(Party party) {
        ItemStack item = new ItemStack(ICON_TYPE);

        String leaderName = UUIDUtils.name(party.getLeader());
        String displayName = LEFT_ARROW + AQUA.toString() + BOLD + leaderName + AQUA + "'s Party" + RIGHT_ARROW;

        ItemUtils.setDisplayName(item, displayName);
        return item;
    }

}
