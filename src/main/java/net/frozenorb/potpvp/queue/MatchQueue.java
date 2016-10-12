package net.frozenorb.potpvp.queue;

import net.frozenorb.potpvp.kittype.KitType;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import lombok.Getter;

public abstract class MatchQueue {

    /**
     * KitType for which this MatchQueue creates {@link net.frozenorb.potpvp.match.Match}s
     */
    @Getter private final KitType kitType;


}