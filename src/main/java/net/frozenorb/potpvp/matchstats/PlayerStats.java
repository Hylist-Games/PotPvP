package net.frozenorb.potpvp.matchstats;

import lombok.Getter;
import lombok.Setter;

final class PlayerStats {

    @Getter @Setter private int swings;
    @Getter @Setter private int swingHits;
    @Getter @Setter private int bowShots;
    @Getter @Setter private int bowHits;

}