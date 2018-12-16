package net.frozenorb.potpvp.duel;

import java.util.Set;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.party.Party;

public final class PartyDuelInvite extends DuelInvite<Party> {

    public PartyDuelInvite(Party sender, Party target, KitType kitType, Set<String> maps) {
        super(sender, target, kitType, maps);
    }

}