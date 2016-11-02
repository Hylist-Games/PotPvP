package net.frozenorb.potpvp.command;

import net.frozenorb.potpvp.util.VisibilityUtils;
import net.frozenorb.qlib.command.Command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class UpdateVisibilityCommand {

    @Command(names = {"updatevisibility", "updatevis", "upvis", "uv"}, permission = "")
    public static void updateVisibility(Player sender) {
        VisibilityUtils.updateVisibility(sender);
        sender.sendMessage(ChatColor.GREEN + "Updated your visibility.");
    }

}