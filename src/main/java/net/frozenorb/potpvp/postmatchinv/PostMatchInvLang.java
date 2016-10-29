package net.frozenorb.potpvp.postmatchinv;

import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.qlib.util.UUIDUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class PostMatchInvLang {

    private static final TextComponent TEAM_1_HEADER_COMPONENT = new TextComponent("Team 1: ");
    private static final TextComponent TEAM_2_HEADER_COMPONENT = new TextComponent("Team 2: ");
    private static final TextComponent YOUR_TEAM_COMPONENT = new TextComponent("Your team: ");
    private static final TextComponent ENEMY_TEAM_COMPONENT = new TextComponent("Enemy team: ");
    private static final TextComponent LINE_COMPONENT = new TextComponent("-----------------------------------------------------");
    private static final TextComponent INVENTORY_HEADER_COMPONENT = new TextComponent("Post-Match Inventories ");
    private static final TextComponent COMMA_COMPONENT = new TextComponent(", ");

    static {
        TEAM_1_HEADER_COMPONENT.setColor(ChatColor.LIGHT_PURPLE);
        TEAM_2_HEADER_COMPONENT.setColor(ChatColor.AQUA);
        YOUR_TEAM_COMPONENT.setColor(ChatColor.GREEN);
        ENEMY_TEAM_COMPONENT.setColor(ChatColor.RED);
        LINE_COMPONENT.setColor(ChatColor.GRAY);
        LINE_COMPONENT.setStrikethrough(true);
        INVENTORY_HEADER_COMPONENT.setColor(ChatColor.GOLD);
        COMMA_COMPONENT.setColor(ChatColor.YELLOW);

        TextComponent clickToView = new TextComponent("(click name to view)");
        clickToView.setColor(ChatColor.GRAY);
        INVENTORY_HEADER_COMPONENT.addExtra(clickToView);
    }

    public static TextComponent[][] spectatorMessages(MatchTeam team1, MatchTeam team2) {
        return new TextComponent[][] {
            { LINE_COMPONENT },
            { INVENTORY_HEADER_COMPONENT },
            { TEAM_1_HEADER_COMPONENT },
            clickToViewLine(team1.getAllMembers()),
            { TEAM_2_HEADER_COMPONENT },
            clickToViewLine(team2.getAllMembers()),
            { LINE_COMPONENT }
        };
    }

    public static TextComponent[][] teamMessages(MatchTeam yourTeam, MatchTeam enemyTeam) {
        return new TextComponent[][] {
            { LINE_COMPONENT },
            { INVENTORY_HEADER_COMPONENT },
            { YOUR_TEAM_COMPONENT },
            clickToViewLine(yourTeam.getAllMembers()),
            { ENEMY_TEAM_COMPONENT },
            clickToViewLine(enemyTeam.getAllMembers()),
            { LINE_COMPONENT }
        };
    }

    private static TextComponent[] clickToViewLine(Set<UUID> members) {
        List<TextComponent> components = new ArrayList<>();

        for (UUID member : members) {
            String memberName = UUIDUtils.name(member);
            TextComponent component = new TextComponent();

            component.setText(memberName);
            component.setColor(ChatColor.YELLOW);
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to view inventory of " + ChatColor.GOLD + memberName).create()));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/_ " + memberName));

            components.add(component);
            components.add(COMMA_COMPONENT);
        }

        components.remove(components.size() - 1); // remove trailing comma
        return components.toArray(new TextComponent[components.size()]);
    }


}