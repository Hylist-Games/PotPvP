package net.frozenorb.potpvp.postmatchinv;

import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.qlib.util.UUIDUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class PostMatchInvLang {

    private static final String TEAM_1 = ChatColor.LIGHT_PURPLE + "Team 1:";
    private static final String TEAM_2 = ChatColor.AQUA + "Team 2:";
    private static final String YOUR_TEAM = ChatColor.GREEN.toString() + ChatColor.BOLD + "Your team:";
    private static final String ENEMY_TEAM = ChatColor.RED.toString() + ChatColor.BOLD + "Enemy team:";
    private static final String PARTICIPANTS = ChatColor.GREEN + "Participants:";
    private static final String LINE = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------------------------";
    private static final String INVENTORY_HEADER = ChatColor.GOLD + "Post-Match Inventories " + ChatColor.GRAY + "(click name to view)";

    private static final TextComponent COMMA_COMPONENT = new TextComponent(", ");

    static {
        COMMA_COMPONENT.setColor(ChatColor.YELLOW);
    }

    static Object[] gen1v1PlayerMessages(UUID you, UUID enemy) {
        return new Object[] {
            LINE,
            INVENTORY_HEADER,
            new TextComponent[] {
                new TextComponent(ChatColor.GREEN + "You: "),
                clickToViewLine(you),
                new TextComponent(ChatColor.GRAY + " - " + ChatColor.RED + "Enemy: "),
                clickToViewLine(enemy)
            },
            LINE
        };
    }

    // when viewing a 2 team match as a spectator
    static Object[] genSpectatorMessages(MatchTeam team1, MatchTeam team2) {
        return new Object[] {
            LINE,
            INVENTORY_HEADER,
            TEAM_1,
            clickToViewLine(team1.getAllMembers()),
            TEAM_2,
            clickToViewLine(team2.getAllMembers()),
            LINE
        };
    }

    // when viewing a 2 team match as a participant
    static Object[] genTeamMessages(MatchTeam yourTeam, MatchTeam enemyTeam) {
        return new Object[] {
            LINE,
            INVENTORY_HEADER,
            YOUR_TEAM,
            clickToViewLine(yourTeam.getAllMembers()),
            ENEMY_TEAM,
            clickToViewLine(enemyTeam.getAllMembers()),
            LINE
        };
    }

    // when viewing a non-2 team match from any perspective
    static Object[] genGenericMessages(Collection<MatchTeam> teams) {
        Set<UUID> members = teams.stream()
            .flatMap(t -> t.getAllMembers().stream())
            .collect(Collectors.toSet());

        return new Object[] {
            LINE,
            INVENTORY_HEADER,
            PARTICIPANTS,
            clickToViewLine(members),
            LINE
        };
    }

    private static TextComponent clickToViewLine(UUID member) {
        String memberName = UUIDUtils.name(member);
        TextComponent component = new TextComponent();

        component.setText(memberName);
        component.setColor(ChatColor.YELLOW);
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to view inventory of " + ChatColor.GOLD + memberName).create()));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/_ " + memberName));

        return component;
    }

    private static TextComponent[] clickToViewLine(Set<UUID> members) {
        List<TextComponent> components = new ArrayList<>();

        for (UUID member : members) {
            components.add(clickToViewLine(member));
            components.add(COMMA_COMPONENT);
        }

        components.remove(components.size() - 1); // remove trailing comma
        return components.toArray(new TextComponent[components.size()]);
    }


}