package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class KitLoadDefaultArmorCommand {

    @Command(names = "kit loadDefaultArmor", permission = "op")
    public static void kitLoadDefaultArmor(Player sender, @Param(name="kit type") KitType kitType) {
        sender.getInventory().setArmorContents(kitType.getMeta().getDefaultArmor());
        sender.updateInventory();

        sender.sendMessage(ChatColor.YELLOW + "Loaded default armor for " + kitType + ".");
    }

}