package net.frozenorb.potpvp.event;

import net.frozenorb.qlib.util.ItemUtils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

import static net.frozenorb.potpvp.PotPvPLang.LEFT_ARROW;
import static net.frozenorb.potpvp.PotPvPLang.RIGHT_ARROW;
import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.LIGHT_PURPLE;

@UtilityClass
public final class EventItems {

    public static final ItemStack EVENTS_ITEM = new ItemStack(Material.EMERALD);

    static {
        ItemUtils.setDisplayName(EVENTS_ITEM, LEFT_ARROW + LIGHT_PURPLE.toString() + BOLD + "Events" + RIGHT_ARROW);
    }

}