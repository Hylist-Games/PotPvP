package net.frozenorb.potpvp.lobby;

import net.frozenorb.qlib.util.ItemUtils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

import static net.frozenorb.potpvp.PotPvPLang.LEFT_ARROW;
import static net.frozenorb.potpvp.PotPvPLang.RIGHT_ARROW;
import static org.bukkit.ChatColor.*;

@UtilityClass
public final class LobbyItems {

    public static final ItemStack EVENTS_ITEM = new ItemStack(Material.EMERALD);
    public static final ItemStack SPECTATE_RANDOM_ITEM = new ItemStack(Material.COMPASS);
    public static final ItemStack SPECTATE_MENU_ITEM = new ItemStack(Material.PAPER);

    static {
        ItemUtils.setDisplayName(EVENTS_ITEM, LEFT_ARROW + LIGHT_PURPLE.toString() + BOLD + "Events" + RIGHT_ARROW);
        ItemUtils.setDisplayName(SPECTATE_RANDOM_ITEM, LEFT_ARROW + YELLOW.toString() + BOLD + "Spectate Random Match" + RIGHT_ARROW);
        ItemUtils.setDisplayName(SPECTATE_MENU_ITEM, LEFT_ARROW + GREEN.toString() + BOLD + "Spectate Menu" + RIGHT_ARROW);
    }

}