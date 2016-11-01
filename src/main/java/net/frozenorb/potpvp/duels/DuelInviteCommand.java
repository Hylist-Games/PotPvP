package net.frozenorb.potpvp.duels;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kittype.menu.SelectKitTypeMenu;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DuelInviteCommand {

    @Command(names = {"duel", "1v1"}, permission = "")
    public static void duel(Player sender, @Param(name = "player") Player target) {
        DuelHandler duelHandler = DuelHandler.instance();
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(sender);

        if (target == sender) {
            sender.sendMessage(DuelLang.CANT_DUEL_YOURSELF.toString());
            return;
        }

        if (party != null && !party.isLeader(sender.getUniqueId())) {
            Player leader = Bukkit.getPlayer(party.getLeader());

            if (party.isMember(target.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You cannot duel a player in your party.");
                return;
            }

            leader.sendMessage(DuelLang.DUEL_PARTY_SUGGESTION_START.fill(sender.getName(), target.getName()));
            leader.spigot().sendMessage(createInviteButton(target.getName()));

            sender.sendMessage(DuelLang.DUEL_PARTY_SUGGESTED.fill(leader.getName(), target.getName()));
            return;
        }

        if (!duelHandler.canInvite(target)) {
            sender.sendMessage(DuelLang.CANNOT_INVITE_PLAYER.fill(target.getName()));
            return;
        }

        new SelectKitTypeMenu(kitType -> {
            sender.closeInventory();
            duel(sender, target, kitType);
        }).openMenu(sender);
    }

    public static void duel(Player sender, Player target, KitType kitType) {
        DuelHandler duelHandler = DuelHandler.instance();
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(sender);

        if (target == sender) {
            sender.sendMessage(DuelLang.CANT_DUEL_YOURSELF.toString());
            return;
        }

        if (party != null && !party.isLeader(sender.getUniqueId())) {
            Player leader = Bukkit.getPlayer(party.getLeader());

            if (party.isMember(target.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You cannot duel a player in your party.");
                return;
            }

            leader.sendMessage(DuelLang.DUEL_PARTY_SUGGESTION_START.fill(sender.getName(), target.getName()));
            leader.spigot().sendMessage(createInviteButton(target.getName()));

            sender.sendMessage(DuelLang.DUEL_PARTY_SUGGESTED.fill(leader.getName(), target.getName()));
            return;
        }

        if (!duelHandler.canInvite(target)) {
            sender.sendMessage(DuelLang.CANNOT_INVITE_PLAYER.fill(target.getName()));
            return;
        }

        DuelInvite sentInvite = duelHandler.inviteBy(sender);
        DuelInvite targetInvite = duelHandler.inviteBy(target);

        if (targetInvite != null && targetInvite.matches(sender.getUniqueId(), kitType)) {
            DuelAcceptCommand.accept(sender, target); // accept the invite
            return;
        }

        if (sentInvite != null && sentInvite.matches(target.getUniqueId(), kitType)) {
            sender.sendMessage(DuelLang.ALREADY_INVITED_PLAYER.fill(target.getName()));
            return;
        } else if (sentInvite != null) {
            sender.sendMessage(DuelLang.PREVIOUS_INVITE_DELETED.toString());
            duelHandler.purgeInvite(sender);
        }

        Party targetParty = PotPvPSI.getInstance().getPartyHandler().getParty(target);
        boolean notLeader = targetParty != null && !targetParty.isLeader(target.getUniqueId());
        String senderName = sender.getName();

        target.sendMessage(DuelLang.INVITED_MESSAGE_START.fill(senderName, kitType.getName()));

        if (notLeader) {
            target.sendMessage(DuelLang.PLAYER_LEAVE_WARNING.toString());
        }

        target.spigot().sendMessage(createInviteNotifButton(senderName));
        target.sendMessage(DuelLang.INVITED_MESSAGE_OR_COMMAND.fill(senderName));

        sender.sendMessage(DuelLang.SUCCESSFULLY_SENT_INVITE.fill(target.getName()));

        DuelInvite invite = new DuelInvite(sender.getUniqueId(), target.getUniqueId(), party != null,
                kitType, System.currentTimeMillis());
        duelHandler.insertInvite(invite);
    }

    private static TextComponent createInviteNotifButton(String sender) {
        return createCommandButton(DuelLang.INVITED_MESSAGE_BUTTON.toString(), "/accept " + sender);
    }

    private static TextComponent createInviteButton(String targetName) {
        String text = DuelLang.DUEL_PARTY_SUGGESTION_CLICK.toString();
        return createCommandButton(text, "/duel " + targetName);
    }

    private static TextComponent createCommandButton(String text, String command) {
        BaseComponent[] hoverTooltip = { new TextComponent(text) };
        HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT;
        ClickEvent.Action runCommand = ClickEvent.Action.RUN_COMMAND;

        TextComponent inviteButton = new TextComponent(text);

        inviteButton.setHoverEvent(new HoverEvent(showText, hoverTooltip));
        inviteButton.setClickEvent(new ClickEvent(runCommand, command));

        return inviteButton;
    }

}