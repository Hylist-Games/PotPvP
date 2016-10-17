package net.frozenorb.potpvp.party;

import net.frozenorb.qlib.util.UUIDUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class PartyLang {

    private static final TextComponent INVITE_PREFIX = new TextComponent("Invite > ");

    private static final TextComponent INVITED_YOU_TO_JOIN = new TextComponent(" invited you to join. ");

    private static final TextComponent ACCEPT_BUTTON = new TextComponent("[Accept]");
    private static final TextComponent INFO_BUTTON = new TextComponent("[Info]");

    static {
        INVITE_PREFIX.setColor(ChatColor.AQUA);
        INVITE_PREFIX.setBold(true);

        INVITED_YOU_TO_JOIN.setColor(ChatColor.YELLOW);

        HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT; // readability

        BaseComponent[] acceptTooltip = new ComponentBuilder("Click to join party").color(ChatColor.GREEN).create();
        BaseComponent[] infoTooltip = new ComponentBuilder("Click to show party info").color(ChatColor.YELLOW).create();

        ACCEPT_BUTTON.setColor(ChatColor.GREEN);
        ACCEPT_BUTTON.setHoverEvent(new HoverEvent(showText, acceptTooltip));

        INFO_BUTTON.setColor(ChatColor.AQUA);
        INFO_BUTTON.setHoverEvent(new HoverEvent(showText, infoTooltip));
    }

    public static TextComponent inviteAcceptPrompt(Party party) {
        ClickEvent.Action runCommand = ClickEvent.Action.RUN_COMMAND;
        String partyLeader = UUIDUtils.name(party.getLeader());

        // create copies via constructor (we're going to update their click event)
        TextComponent acceptButton = new TextComponent(ACCEPT_BUTTON);
        TextComponent infoButton = new TextComponent(INFO_BUTTON);

        acceptButton.setClickEvent(new ClickEvent(runCommand, "/p join " + partyLeader));
        infoButton.setClickEvent(new ClickEvent(runCommand, "/p info " + partyLeader));

        TextComponent builder = new TextComponent("");

        builder.addExtra(INVITE_PREFIX);
        builder.addExtra(hoverablePartyName(party));
        builder.addExtra(INVITED_YOU_TO_JOIN);
        builder.addExtra(acceptButton);
        builder.addExtra(new TextComponent(" "));
        builder.addExtra(infoButton);

        return builder;
    }

    public static TextComponent hoverablePartyName(Party party) {
        TextComponent previewComponent = new TextComponent();
        String leaderName = UUIDUtils.name(party.getLeader());

        // only show an actual tooltip for parties with >= 2 members,
        // parties that (to the user) don't exist yet just show up as a name
        if (party.getMembers().size() > 1) {
            HoverEvent hoverEvent = hoverablePreviewTooltip(party);

            previewComponent.setText("[" + leaderName + "'s Party]");
            previewComponent.setHoverEvent(hoverEvent);
        } else {
            previewComponent.setText(leaderName);
        }

        previewComponent.setColor(ChatColor.BLUE);
        return previewComponent;
    }

    public static HoverEvent hoverablePreviewTooltip(Party party) {
        int linesCreated = 0; // used to cut off preview at certain line length
        Set<UUID> partyMembers = party.getMembers();
        int partySize = partyMembers.size();

        // if we're a 3/4 member party we present we already wrote 2 entries (leader + ghost player)
        // instead of 1 (just the leader) so we wrap into a 2x2 (wrapping code thinks it's a 3x2 though)
        int index = partySize == 3 || partySize == 4 ? 2 : 1;

        // leader is added manually, this set isn't mutated at all
        Set<UUID> membersWithoutLeader = new HashSet<>(partyMembers);
        membersWithoutLeader.remove(party.getLeader());

        // builds long text component that will be hoverable
        ComponentBuilder builder = new ComponentBuilder("Members(").color(ChatColor.BLUE);

        builder.append(String.valueOf(partySize)).color(ChatColor.GOLD);
        builder.append("):").color(ChatColor.BLUE);
        builder.append("\n"); // \n is the proper way to do a line break in tooltips
        // manually add leader (with * to indicate status)
        builder.append(UUIDUtils.name(party.getLeader()) + "*, ").color(ChatColor.YELLOW);

        // used to store each line of the grid as it's being created
        StringBuilder lineBuilder = new StringBuilder();

        for (UUID member : membersWithoutLeader) {
            lineBuilder.append(UUIDUtils.name(member));

            if (index == 0) {
                lineBuilder.append(", ");
            }

            if (linesCreated == 3 && partyMembers.size() > 12) {
                builder.append((partyMembers.size() - 12) + " more.").color(ChatColor.GRAY);
                break;
            }

            builder.append(lineBuilder.toString()).color(ChatColor.YELLOW);

            if (index > 2) {
                builder.append("\n"); // actually move to next line
                lineBuilder = new StringBuilder();
                // we reset to 0 instead of 2 or 1 so we do a full 3 entries on all non-initial lines
                index = 0;
                linesCreated++;

                continue;
            }

            index++;
        }

        HoverEvent.Action action = HoverEvent.Action.SHOW_TEXT;
        return new HoverEvent(action, builder.create());
    }

}