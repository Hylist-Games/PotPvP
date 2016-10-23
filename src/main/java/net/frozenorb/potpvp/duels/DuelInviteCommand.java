package net.frozenorb.potpvp.duels;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kittype.menu.SelectKitTypeMenu;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

import static net.frozenorb.qlib.uuid.FrozenUUIDCache.name;

public class DuelInviteCommand {

    @Command(names = {"duel", "1v1"}, permission = "")
    public static void duel(Player sender, @Param(name = "player") UUID target) {
        DuelHandler duelHandler = DuelHandler.instance();
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(sender);

        if (target.equals(sender.getUniqueId())) {
            sender.sendMessage(DuelLang.CANT_DUEL_YOURSELF.toString());
            return;
        }

        if (party != null && !party.isLeader(sender.getUniqueId())) {
            String targetName = name(target);
            Player partyLeader = Bukkit.getPlayer(party.getLeader());

            partyLeader.sendMessage(DuelLang.DUEL_PARTY_SUGGESTION_START.fill(sender.getName(), targetName));
            partyLeader.spigot().sendMessage(createInviteButton(targetName));

            sender.sendMessage(DuelLang.DUEL_PARTY_SUGGESTED.fill(partyLeader.getName(), targetName));
            return;
        }

        if (!duelHandler.canInvite(target)) {
            sender.sendMessage(DuelLang.CANNOT_INVITE_PLAYER.fill(name(target)));
            return;
        }

        new SelectKitTypeMenu((kitType) -> {
            DuelInvite sentInvite = duelHandler.inviteBy(sender.getUniqueId());
            DuelInvite targetInvite = duelHandler.inviteBy(target);

            if (targetInvite != null && targetInvite.matches(sender.getUniqueId(), kitType)) {
                DuelAcceptCommand.accept(sender, target); // accept the invite
                return;
            }

            if (sentInvite != null && sentInvite.matches(target, kitType)) {
                sender.sendMessage(DuelLang.ALREADY_INVITED_PLAYER.fill(name(target)));
                return;
            } else if (sentInvite != null) {
                sender.sendMessage(DuelLang.PREVIOUS_INVITE_DELETED.toString());
                duelHandler.purgeInvite(sender.getUniqueId());
            }

            Player targetPlayer = Bukkit.getPlayer(target);
            Party targetParty = PotPvPSI.getInstance().getPartyHandler().getParty(targetPlayer);
            boolean notLeader = targetParty != null && !targetParty.isLeader(target);
            String senderName = sender.getName();

            targetPlayer.sendMessage(DuelLang.INVITED_MESSAGE_START.fill(senderName, kitType.getName()));

            if (notLeader) {
                targetPlayer.sendMessage(DuelLang.PLAYER_LEAVE_WARNING.toString());
            }

            targetPlayer.spigot().sendMessage(createInviteNotifButton(senderName));
            targetPlayer.sendMessage(DuelLang.INVITED_MESSAGE_OR_COMMAND.fill(senderName));

            sender.sendMessage(DuelLang.SUCCESSFULLY_SENT_INVITE.fill(targetPlayer.getName()));

            DuelInvite invite = new DuelInvite(sender.getUniqueId(), target, party != null,
                    kitType, System.currentTimeMillis());
            duelHandler.insertInvite(invite);
        }).openMenu(sender);
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
