package net.frozenorb.potpvp.kit;

import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.util.ItemUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class KitItems {

    public static final ItemStack OPEN_EDITOR_ITEM = new ItemStack(Material.BOOK);

    static {
        ItemUtils.setDisplayName(OPEN_EDITOR_ITEM, PotPvPLang.LEFT_ARROW + ChatColor.YELLOW.toString() + ChatColor.BOLD + "Kit Editor" + PotPvPLang.RIGHT_ARROW);
    }

}