package net.frozenorb.potpvp.kittype.command;


import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class KitSaveEditorItemsCommand {

    @Command(names = "kit saveEditorItems", permission = "op")
    public static void kitSaveEditorItems(Player sender, @Param(name="kit type") KitType kitType) {
        kitType.getMeta().setEditorItems(sender.getInventory().getContents());
        kitType.saveMetaAsync();

        sender.sendMessage(ChatColor.YELLOW + "Saved editor items for " + kitType + ".");
    }

}