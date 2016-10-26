package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class KitSaveDefaultArmorCommand {

    @Command(names = "kit saveDefaultArmor", permission = "op")
    public static void kitSaveDefaultArmor(Player sender, @Param(name="kit type") KitType kitType) {
        kitType.getMeta().setDefaultArmor(sender.getInventory().getArmorContents());
        kitType.saveMetaAsync();

        sender.sendMessage(ChatColor.YELLOW + "Saved default armor for " + kitType + ".");
    }

}