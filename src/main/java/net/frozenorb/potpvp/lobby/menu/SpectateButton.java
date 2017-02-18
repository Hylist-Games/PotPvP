package net.frozenorb.potpvp.lobby.menu;

import com.google.common.base.Preconditions;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.util.UUIDUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

final class SpectateButton extends Button {

    private final Match match;

    SpectateButton(Match match) {
        this.match = Preconditions.checkNotNull(match, "match");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + match.getSimpleDescription();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();
        MatchTeam teamA = match.getTeams().get(0);
        MatchTeam teamB = match.getTeams().get(1);

        if (match.isRanked()) {
            description.add(ChatColor.GREEN + "Ranked");
        } else {
            description.add(ChatColor.GRAY + "Unranked");
        }

        description.add("");
        description.add(ChatColor.YELLOW + "Kit: " + ChatColor.WHITE + match.getKitType().getName());
        description.add(ChatColor.YELLOW + "Arena: " + ChatColor.WHITE + match.getArena().getSchematic());
        description.add(ChatColor.YELLOW + "Spectators: " + ChatColor.WHITE + match.getSpectators().size());

        if (teamA.getAliveMembers().size() != 1 || teamB.getAliveMembers().size() != 1) {
            description.add("");

            for (UUID member : teamA.getAliveMembers()) {
                description.add(ChatColor.AQUA + UUIDUtils.name(member));
            }

            description.add(ChatColor.YELLOW + "   vs.");

            for (UUID member : teamB.getAliveMembers()) {
                description.add(ChatColor.AQUA + UUIDUtils.name(member));
            }
        }

        description.add("");
        description.add(ChatColor.GREEN + "» Click to spectate «");

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.PAPER;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        if (!PotPvPValidation.canUseSpectateItemIgnoreMatchSpectating(player)) {
            return;
        }

        Match currentlySpectating = PotPvPSI.getInstance().getMatchHandler().getMatchSpectating(player);

        if (currentlySpectating != null) {
            currentlySpectating.removeSpectator(player);
        }

        match.addSpectator(player, null);
    }

}