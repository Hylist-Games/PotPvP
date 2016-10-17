package net.frozenorb.potpvp.party.command;

import net.frozenorb.qlib.command.Command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyCreateCommand {

    @Command(names = {"party create", "p create", "t create", "team create", "f create"}, permission = "")
    public static void partyCreate(Player sender) {
        sender.sendMessage(ChatColor.YELLOW + "To create a party, simply invite a player and a party will be created automatically.");
    }

}