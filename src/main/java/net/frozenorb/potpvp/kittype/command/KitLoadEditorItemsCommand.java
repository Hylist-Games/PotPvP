package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class KitLoadEditorItemsCommand {

    @Command(names = "kit loadEditorItems", permission = "op")
    public static void kitLoadEditorItems(Player sender, @Param(name="kit type") KitType kitType) {
        sender.getInventory().setContents(kitType.getMeta().getEditorItems());
        sender.updateInventory();

        sender.sendMessage(ChatColor.YELLOW + "Loaded editor items for " + kitType + ".");
    }

}