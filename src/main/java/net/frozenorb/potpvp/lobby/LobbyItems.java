package net.frozenorb.potpvp.lobby;

import net.frozenorb.qlib.util.ItemUtils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

import static net.frozenorb.potpvp.PotPvPLang.LEFT_ARROW;
import static net.frozenorb.potpvp.PotPvPLang.RIGHT_ARROW;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.YELLOW;

@UtilityClass
public final class LobbyItems {

    public static final ItemStack SPECTATE_RANDOM_ITEM = new ItemStack(Material.COMPASS);
    public static final ItemStack SPECTATE_MENU_ITEM = new ItemStack(Material.PAPER);
    public static final ItemStack ENABLE_SPEC_MODE_ITEM = new ItemStack(Material.REDSTONE_TORCH_ON);
    public static final ItemStack DISABLE_SPEC_MODE_ITEM = new ItemStack(Material.LEVER);
    public static final ItemStack MANAGE_ITEM = new ItemStack(Material.ANVIL);

    static {
        ItemUtils.setDisplayName(SPECTATE_RANDOM_ITEM, LEFT_ARROW + YELLOW.toString() + BOLD + "Spectate Random Match" + RIGHT_ARROW);
        ItemUtils.setDisplayName(SPECTATE_MENU_ITEM, LEFT_ARROW + GREEN.toString() + BOLD + "Spectate Menu" + RIGHT_ARROW);
        ItemUtils.setDisplayName(ENABLE_SPEC_MODE_ITEM, LEFT_ARROW + AQUA.toString() + BOLD + "Enable Spectator Mode" + RIGHT_ARROW);
        ItemUtils.setDisplayName(DISABLE_SPEC_MODE_ITEM, LEFT_ARROW + AQUA.toString() + BOLD + "Disable Spectator Mode" + RIGHT_ARROW);
        ItemUtils.setDisplayName(MANAGE_ITEM, LEFT_ARROW + GRAY.toString() + BOLD.toString() + "Manage PotPvP" + RIGHT_ARROW);
    }

}