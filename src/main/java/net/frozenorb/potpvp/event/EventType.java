package net.frozenorb.potpvp.event;

import net.frozenorb.potpvp.event.event.EvolutionEvent;
import net.frozenorb.potpvp.event.event.KothEvent;
import net.frozenorb.potpvp.event.event.LastManStandingEvent;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import lombok.Getter;

public enum EventType {

    LAST_MAN_STANDING(
        "Last Man Standing",
        new String[] {
            "LMS 1",
            "LMS 2"
        },
        5,
        100,
        new MaterialData(Material.WOOD_SWORD),
        LastManStandingEvent.class
    ),
    KOTH(
        "KOTH",
        new String[] {
            "KOTH 1",
            "KOTH 2"
        },
        5,
        100,
        new MaterialData(Material.BEACON),
        KothEvent.class
    ),
    EVOLUTION(
        "Evolution",
        new String[] {
            "EVO 1",
            "EVO 2"
        },
        5,
        20,
        new MaterialData(Material.EMERALD_BLOCK),
        EvolutionEvent.class
    );

    @Getter private final String name;
    @Getter private final String[] description;
    @Getter private final int minPlayers;
    @Getter private final int maxPlayers;
    @Getter private final MaterialData icon;
    private final Class<? extends Event> clazz;

    EventType(String name, String[] description, int minPlayers, int maxPlayers, MaterialData icon, Class<? extends Event> clazz) {
        this.name = name;
        this.description = description;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.icon = icon;
        this.clazz = clazz;
    }

    Event createInstance() {
        try {
            return clazz.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}