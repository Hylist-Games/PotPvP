package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class KitToggleItemSpawnCommand {

    @Command(names = "kit toggleItemSpawn", permission = "op")
    public static void kitToggleItemSpawn(Player sender, @Param(name="kit type") KitType kitType, @Param(name="allowed") boolean allowed) {
        kitType.getMeta().setEditorSpawnAllowed(allowed);
        kitType.saveMetaAsync();

        sender.sendMessage(ChatColor.YELLOW + "Kit editor item spawns are now " + (allowed ? "allowed" : "disallowed"));
    }

}