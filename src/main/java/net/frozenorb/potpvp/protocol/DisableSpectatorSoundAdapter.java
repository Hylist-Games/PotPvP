package net.frozenorb.potpvp.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import net.frozenorb.potpvp.PotPvPSI;

public final class DisableSpectatorSoundAdapter extends PacketAdapter {

    public DisableSpectatorSoundAdapter() {
        super(PotPvPSI.getInstance(), PacketType.Play.Server.NAMED_SOUND_EFFECT);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        String effectName = event.getPacket().getStrings().read(0);

        // blocks the block place sounds which are sent when spectators attempt to
        // place a block (even if it's cancelled, damn bukkit)
        // dig.cloth = carpet, dig.wood = fire (for leaving a match)
        if (effectName.toLowerCase().startsWith("dig.cloth") || effectName.toLowerCase().startsWith("dig.wood")) {
            event.setCancelled(true);
        }
    }

}