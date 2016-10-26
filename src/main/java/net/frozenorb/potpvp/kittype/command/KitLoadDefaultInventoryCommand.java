package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class KitLoadDefaultInventoryCommand {

    @Command(names = "kit loadDefaultInventory", permission = "op")
    public static void kitLoadDefaultInventory(Player sender, @Param(name="kit type") KitType kitType) {
        sender.getInventory().setContents(kitType.getMeta().getDefaultInventory());
        sender.updateInventory();

        sender.sendMessage(ChatColor.YELLOW + "Loaded default inventory for " + kitType + ".");
    }

}