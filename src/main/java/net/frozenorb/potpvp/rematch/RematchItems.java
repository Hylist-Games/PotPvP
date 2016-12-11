package net.frozenorb.potpvp.rematch;

import net.frozenorb.qlib.util.ItemUtils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

import static net.frozenorb.potpvp.PotPvPLang.LEFT_ARROW;
import static net.frozenorb.potpvp.PotPvPLang.RIGHT_ARROW;
import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.DARK_PURPLE;
import static org.bukkit.ChatColor.GREEN;

@UtilityClass
public final class RematchItems {

    public static final ItemStack REQUEST_REMATCH_ITEM = new ItemStack(Material.DIAMOND);
    public static final ItemStack SENT_REMATCH_ITEM = new ItemStack(Material.EMERALD);
    public static final ItemStack ACCEPT_REMATCH_ITEM = new ItemStack(Material.EMERALD);

    static {
        ItemUtils.setDisplayName(REQUEST_REMATCH_ITEM, LEFT_ARROW + DARK_PURPLE.toString() + BOLD + "Request Rematch" + RIGHT_ARROW);
        ItemUtils.setDisplayName(SENT_REMATCH_ITEM, LEFT_ARROW + GREEN.toString() + BOLD + "Sent Rematch" + RIGHT_ARROW);
        ItemUtils.setDisplayName(ACCEPT_REMATCH_ITEM, LEFT_ARROW + GREEN.toString() + BOLD + "Accept Rematch" + RIGHT_ARROW);
    }

}