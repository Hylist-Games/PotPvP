package net.frozenorb.potpvp.duels;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.party.Party;

public final class PartyDuelInvite extends DuelInvite<Party> {

    public PartyDuelInvite(Party sender, Party target, KitType kitType) {
        super(sender, target, kitType);
    }

}