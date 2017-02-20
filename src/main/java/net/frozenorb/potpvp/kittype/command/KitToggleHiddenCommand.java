package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class KitToggleHiddenCommand {

    @Command(names = "kit toggleHidden", permission = "op")
    public static void kitToggleHidden(Player sender, @Param(name="kit type") KitType kitType, @Param(name="hidden") boolean hidden) {
        kitType.setHidden(hidden);
        kitType.saveAsync();

        sender.sendMessage(ChatColor.YELLOW + "Kit is now " + (hidden ? "hidden" : "public") + ".");
    }

}