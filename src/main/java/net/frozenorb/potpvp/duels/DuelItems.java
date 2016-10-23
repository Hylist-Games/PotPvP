package net.frozenorb.potpvp.duels;

import net.frozenorb.potpvp.util.ItemUtils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

import static net.frozenorb.potpvp.PotPvPLang.LEFT_ARROW;
import static net.frozenorb.potpvp.PotPvPLang.RIGHT_ARROW;
import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.YELLOW;

@UtilityClass
public final class DuelItems {

    public static final ItemStack OTHER_PARTIES_ITEM = new ItemStack(Material.SKULL_ITEM);
    public static final ItemStack PENDING_INVITES_ITEM = new ItemStack(Material.WATCH);

    static {
        ItemUtils.setDisplayName(OTHER_PARTIES_ITEM, LEFT_ARROW + GREEN.toString() + BOLD + "Other Parties" + RIGHT_ARROW);
        ItemUtils.setDisplayName(PENDING_INVITES_ITEM, LEFT_ARROW + YELLOW.toString() + BOLD + "Pending Invites" + RIGHT_ARROW);
    }

}