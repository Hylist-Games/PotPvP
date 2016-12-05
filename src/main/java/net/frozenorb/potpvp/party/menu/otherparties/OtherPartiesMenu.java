package net.frozenorb.potpvp.party.menu.otherparties;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class OtherPartiesMenu extends Menu {

    public OtherPartiesMenu() {
        super("Other parties");
        setPlaceholder(true);
        setAutoUpdate(true);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        for (Party party : partyHandler.getParties()) {
            if (party.isMember(player.getUniqueId())) {
                continue;
            }

            if (matchHandler.isPlayingOrSpectatingMatch(party.getLeader())) {
                continue;
            }

            if (!settingHandler.getSetting(party.getLeader(), Setting.RECEIVE_DUELS)) {
                continue;
            }

            buttons.put(index++, new OtherPartyButton(party));
        }

        return buttons;
    }

}