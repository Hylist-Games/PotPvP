package net.frozenorb.potpvp.elo;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.party.Party;

import org.bukkit.entity.Player;

public final class EloHandler {

    public static final int DEFAULT_ELO = 1_500;

    public EloHandler() {

    }

    public int getElo(Player player, KitType kitType) {
        return DEFAULT_ELO;
    }

    public int getElo(Party party, KitType kitType) {
        return DEFAULT_ELO;
    }

}