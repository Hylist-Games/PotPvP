package net.frozenorb.potpvp.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import net.frozenorb.potpvp.PotPvPSI;

public final class DisableCarpetSoundAdapter extends PacketAdapter {

    public DisableCarpetSoundAdapter() {
        super(PotPvPSI.getInstance(), PacketType.Play.Server.NAMED_SOUND_EFFECT);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        String effectName = event.getPacket().getStrings().read(0);

        if (effectName.toLowerCase().startsWith("dig.cloth")) {
            event.setCancelled(true);
        }
    }

}