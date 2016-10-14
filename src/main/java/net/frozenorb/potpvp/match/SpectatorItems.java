package net.frozenorb.potpvp.match;

import net.frozenorb.potpvp.util.ItemUtils;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class SpectatorItems {

    public static final ItemStack CARPET_ITEM = new ItemStack(Material.CARPET, 1, DyeColor.RED.getWoolData());
    public static final ItemStack RETURN_TO_LOBBY_ITEM = new ItemStack(Material.FIRE);
    public static final ItemStack TOGGLE_SPECTATORS_ITEM = new ItemStack(Material.EMERALD);
    public static final ItemStack VIEW_INVENTORY_ITEM = new ItemStack(Material.BOOK);

    static {
        ItemUtils.setDisplayName(RETURN_TO_LOBBY_ITEM, ChatColor.YELLOW + "Return to lobby");
        ItemUtils.setDisplayName(TOGGLE_SPECTATORS_ITEM, ChatColor.YELLOW + "Toggle spectator visibility");
        ItemUtils.setDisplayName(VIEW_INVENTORY_ITEM, ChatColor.YELLOW + "View player inventory");
    }

}