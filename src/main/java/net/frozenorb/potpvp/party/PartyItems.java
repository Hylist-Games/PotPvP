package net.frozenorb.potpvp.party;

import net.frozenorb.potpvp.util.ItemUtils;
import net.frozenorb.qlib.util.UUIDUtils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

import static net.frozenorb.potpvp.PotPvPLang.LEFT_ARROW;
import static net.frozenorb.potpvp.PotPvPLang.RIGHT_ARROW;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.BOLD;

// Static imports generally harm readability but
// in this case it's too unreadable without them
@UtilityClass
public final class PartyItems {

    public static ItemStack icon(Party party) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);

        String leaderName = UUIDUtils.name(party.getLeader());
        String displayName = LEFT_ARROW + AQUA.toString() + BOLD + leaderName + AQUA + "'s Party" + RIGHT_ARROW;

        ItemUtils.setDisplayName(item, displayName);
        return item;
    }

}