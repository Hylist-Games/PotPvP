package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class KitEditorItemsCommand {

    @Command(names = "kit editorItems", permission = "op")
    public static void kitEditorItems(Player sender, @Param(name="kit type") KitType kitType) {
        sender.sendMessage(ChatColor.RED + "This feature is not complete yet.");
    }

}