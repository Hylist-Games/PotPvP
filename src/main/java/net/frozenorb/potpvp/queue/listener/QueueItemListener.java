package net.frozenorb.potpvp.queue.listener;

import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kittype.menu.CustomSelectKitTypeMenu;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.potpvp.queue.QueueItems;
import net.frozenorb.potpvp.util.ItemListener;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public final class QueueItemListener extends ItemListener {

    private final Function<KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionMenuAddition = kitType -> {
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        int inFights = matchHandler.countPlayersPlayingMatches(m -> m.getKitType() == kitType);
        int inQueue = queueHandler.countPlayersQueued(kitType);


        return new CustomSelectKitTypeMenu.CustomKitTypeMeta(
            // clamp value to >= 1 && <= 64
            Math.max(1, Math.min(64, inFights + inQueue)),
            ImmutableList.of(
                ChatColor.GREEN + "In fights: " + ChatColor.WHITE + inFights,
                ChatColor.GREEN + "In queue: " + ChatColor.WHITE + inQueue
            )
        );
    };

    public QueueItemListener(QueueHandler queueHandler) {
        addHandler(QueueItems.JOIN_SOLO_UNRANKED_QUEUE_ITEM, player -> {
            // try to check validation issues in advance
            // (will be called again in QueueHandler#joinQueue)
            if (PotPvPValidation.canJoinQueue(player)) {
                new CustomSelectKitTypeMenu(kitType -> {
                    queueHandler.joinQueue(player, kitType, false);
                    player.closeInventory();
                }, selectionMenuAddition).openMenu(player);
            }
        });

        addHandler(QueueItems.LEAVE_SOLO_UNRANKED_QUEUE_ITEM, player -> {
            queueHandler.leaveQueue(player, false);
        });

        addHandler(QueueItems.JOIN_PARTY_UNRANKED_QUEUE_ITEM, player -> {
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
                    queueHandler.joinQueue(party, kitType, false);
                    player.closeInventory();
                }, selectionMenuAddition).openMenu(player);
            }
        });

        addHandler(QueueItems.LEAVE_PARTY_UNRANKED_QUEUE_ITEM, player -> {
            Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);

            // just fail silently, players who aren't a leader
            // of a party shouldn't even have this item
            if (party != null && party.isLeader(player.getUniqueId())) {
                queueHandler.leaveQueue(party, false);
            }
        });

        Consumer<Player> messageRankedDisabled = p -> p.sendMessage(ChatColor.RED + "Ranked is currently disabled.");
        addHandler(QueueItems.JOIN_SOLO_RANKED_QUEUE_ITEM, messageRankedDisabled);
        addHandler(QueueItems.JOIN_PARTY_RANKED_QUEUE_ITEM, messageRankedDisabled);
        addHandler(QueueItems.LEAVE_SOLO_RANKED_QUEUE_ITEM, messageRankedDisabled);
        addHandler(QueueItems.LEAVE_PARTY_RANKED_QUEUE_ITEM, messageRankedDisabled);
    }

}