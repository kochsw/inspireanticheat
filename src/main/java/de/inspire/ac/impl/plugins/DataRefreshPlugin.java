package de.inspire.ac.impl.plugins;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.inspire.ac.api.eventbus.interfaces.EventHandler;
import de.inspire.ac.api.plugins.Plugin;
import de.inspire.ac.api.plugins.annotations.Internal;
import de.inspire.ac.impl.events.EventPacket;

@Internal
public class DataRefreshPlugin extends Plugin {

    @EventHandler
    public void onPacket(EventPacket event) {
        PacketEvent e = event.getPacket();
        PacketContainer packet = e.getPacket();

        PacketType type = packet.getType();

        if (type == PacketType.Play.Client.ENTITY_ACTION) {
            e.getPlayer().sendMessage("Interact");
        } else if (type == PacketType.Play.Client.USE_ENTITY) {
            e.getPlayer().sendMessage("Attack");
        }
    }
}
