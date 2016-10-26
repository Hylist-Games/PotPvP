package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class KitSaveDefaultInventoryCommand {

    @Command(names = "kit saveDefaultInventory", permission = "op")
    public static void kitSaveDefaultInventory(Player sender, @Param(name="kit type") KitType kitType) {
        kitType.getMeta().setDefaultInventory(sender.getInventory().getContents());
        kitType.saveMetaAsync();

        sender.sendMessage(ChatColor.YELLOW + "Saved default inventory for " + kitType + ".");
    }

}