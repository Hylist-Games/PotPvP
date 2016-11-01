package net.frozenorb.potpvp.duels;

import static net.frozenorb.potpvp.PotPvPLang.LEFT_ARROW;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RED;

/**
 * @author Mazen Kotb
 */
public enum DuelLang {
    DUEL_PREFIX(LEFT_ARROW + " " + GRAY),
    ERROR_STARTING_MATCH(DARK_RED + "Sorry! There was an error starting the match, please contact an admin."),
    CANT_DUEL_YOURSELF(DARK_RED + "You can't duel yourself!"),
    CANT_ACCEPT_DUEL_FROM_YOURSELF(DARK_RED + " You can't accept a duel from yourself!"),
    NO_INVITE_HAS_BEEN_SENT(DUEL_PREFIX + "No invite has been sent to you from %s!"),
    PLAYER_LEAVE_WARNING(DUEL_PREFIX + string(RED) + "WARNING: Accepting this invite *will* force you to leave your party."),
    DUEL_PARTY_SUGGESTION_START(DUEL_PREFIX + string(GOLD) + "%s" + GRAY + " suggested to invite %s to a duel!"),
    DUEL_PARTY_SUGGESTION_CLICK(DUEL_PREFIX + string(GOLD) + "Click here to invite them to a duel!"),
    DUEL_PARTY_SUGGESTED(DUEL_PREFIX + "Suggested to " + GOLD + "%s" + " to invite %s to a duel"),
    PREVIOUS_INVITE_DELETED(DUEL_PREFIX + "Previous invite deleted..."),
    ALREADY_INVITED_PLAYER(DUEL_PREFIX + string(RED) + "You already invited %s to a duel with this kit!"),
    CANNOT_INVITE_PLAYER(DUEL_PREFIX + string(RED) + "Sorry! You can't invite %s due to their settings."),
    INVITED_MESSAGE_START(DUEL_PREFIX + string(GOLD) + "%s" + GRAY + " has invited you to a duel with kit type %s!"),
    INVITED_MESSAGE_BUTTON(DUEL_PREFIX + string(GOLD) + "Click here to accept their invite!"),
    INVITED_MESSAGE_OR_COMMAND(DUEL_PREFIX + "Or type " + GOLD + "/accept %s" + GRAY + "!"),
    SUCCESSFULLY_SENT_INVITE(DUEL_PREFIX + "Successfully sent a duel invite to " + GOLD + "%s" + GRAY + "!")
    ;

    private String value;

    DuelLang(String value) {
        this.value = value;
    }

    private static String string(Enum e) {
        return e.toString();
    }

    public String fill(Object... values) {
        return String.format(toString(), values);
    }

    @Override
    public String toString() {
        return value;
    }
}
