package net.frozenorb.potpvp.queue.listener;

import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kittype.menu.select.CustomSelectKitTypeMenu;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.potpvp.queue.QueueItems;
import net.frozenorb.potpvp.util.ItemListener;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;

import java.util.function.Consumer;
import java.util.function.Function;

// This class followes a different organizational style from other item listeners
// because we need seperate listeners for ranked/unranked, we have methods which
// we call which generate a Consumer<Player> designed for either ranked/unranked,
// based on the argument passed. Returning Consumers makes this code slightly
// harder to follow, but saves us from a lot of duplication
public final class QueueItemListener extends ItemListener {

    private final Function<KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionAdditionRanked = selectionMenuAddition(true);
    private final Function<KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionAdditionUnranked = selectionMenuAddition(false);
    private final QueueHandler queueHandler;

    public QueueItemListener(QueueHandler queueHandler) {
        this.queueHandler = queueHandler;

        addHandler(QueueItems.JOIN_SOLO_UNRANKED_QUEUE_ITEM, joinSoloConsumer(false));
        addHandler(QueueItems.JOIN_SOLO_RANKED_QUEUE_ITEM, joinSoloConsumer(true));

        addHandler(QueueItems.JOIN_PARTY_UNRANKED_QUEUE_ITEM, joinPartyConsumer(false));
        addHandler(QueueItems.JOIN_PARTY_RANKED_QUEUE_ITEM, joinPartyConsumer(true));

        addHandler(QueueItems.LEAVE_SOLO_UNRANKED_QUEUE_ITEM, p -> queueHandler.leaveQueue(p, false));
        addHandler(QueueItems.LEAVE_SOLO_RANKED_QUEUE_ITEM, p -> queueHandler.leaveQueue(p, false));

        Consumer<Player> leavePartyConsumer = player -> {
            Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);

            // don't message, players who aren't leader shouldn't even get this item
            if (party != null && party.isLeader(player.getUniqueId())) {
                queueHandler.leaveQueue(party, false);
            }
        };

        addHandler(QueueItems.LEAVE_PARTY_UNRANKED_QUEUE_ITEM, leavePartyConsumer);
        addHandler(QueueItems.LEAVE_PARTY_RANKED_QUEUE_ITEM, leavePartyConsumer);
    }

    private Consumer<Player> joinSoloConsumer(boolean ranked) {
        return player -> {
            if (PotPvPValidation.canJoinQueue(player)) {
                new CustomSelectKitTypeMenu(kitType -> {
                    queueHandler.joinQueue(player, kitType, ranked);
                    player.closeInventory();
                }, ranked ? selectionAdditionRanked : selectionAdditionUnranked, "Join " + (ranked ? "ranked" : "unranked") + " queue...").openMenu(player);
            }
        };
    }

    private Consumer<Player> joinPartyConsumer(boolean ranked) {
        return player -> {
            Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);

            // just fail silently, players who aren't a leader
            // of a party shouldn't even have this item
            if (party == null || !party.isLeader(player.getUniqueId())) {
                return;
            }

            // try to check validation issues in advance
            // (will be called again in QueueHandler#joinQueue)
            if (PotPvPValidation.canJoinQueue(party)) {
                new CustomSelectKitTypeMenu(kitType -> {
                    queueHandler.joinQueue(party, kitType, ranked);
                    player.closeInventory();
                }, ranked ? selectionAdditionRanked : selectionAdditionUnranked, "Join " + (ranked ? "ranked" : "unranked") + " queue...").openMenu(player);
            }
        };
    }

    private Function<KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionMenuAddition(boolean ranked) {
        return kitType -> {
            MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

            int inFightsRanked = matchHandler.countPlayersPlayingMatches(m -> m.getKitType() == kitType && m.isRanked());
            int inQueueRanked = queueHandler.countPlayersQueued(kitType, true);

            int inFightsUnranked = matchHandler.countPlayersPlayingMatches(m -> m.getKitType() == kitType && !m.isRanked());
            int inQueueUnranked = queueHandler.countPlayersQueued(kitType, false);

            return new CustomSelectKitTypeMenu.CustomKitTypeMeta(
                // clamp value to >= 1 && <= 64
                Math.max(1, Math.min(64, ranked ? inFightsRanked + inQueueRanked : inFightsUnranked + inQueueUnranked)),
                ImmutableList.of(
                    ChatColor.AQUA.toString() + ChatColor.BOLD + (ranked ? ChatColor.UNDERLINE.toString() : "") + "Ranked:",
                    ChatColor.GREEN + "   In fights: " + ChatColor.WHITE + inFightsRanked,
                    ChatColor.GREEN + "   In queue: " + ChatColor.WHITE + inQueueRanked,
                    "",
                    ChatColor.AQUA.toString() + ChatColor.BOLD + (!ranked ? ChatColor.UNDERLINE.toString() : "") + "Unranked:",
                    ChatColor.GREEN + "   In fights: " + ChatColor.WHITE + inFightsUnranked,
                    ChatColor.GREEN + "   In queue: " + ChatColor.WHITE + inQueueUnranked
                )
            );
        };
    }

}