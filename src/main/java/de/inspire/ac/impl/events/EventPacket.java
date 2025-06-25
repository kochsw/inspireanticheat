package de.inspire.ac.impl.events;

import com.comphenix.protocol.events.PacketEvent;
import de.inspire.ac.api.eventbus.interfaces.Cancellable;
import lombok.Getter;

@Getter
public class EventPacket implements Cancellable {

    private boolean cancelled;
    private final PacketEvent packet;

    public EventPacket(PacketEvent packet) {
        this.packet = packet;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
        packet.setCancelled(cancelled);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
