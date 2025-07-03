package de.inspire.ac.impl.plugins;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import de.inspire.ac.api.API;
import de.inspire.ac.api.eventbus.annotations.EventHandler;
import de.inspire.ac.api.plugins.Plugin;
import de.inspire.ac.api.plugins.annotations.Internal;
import de.inspire.ac.impl.events.ingame.EventSendPacket;
import de.inspire.ac.impl.events.ingame.EventReceivePacket;
import de.inspire.ac.impl.events.internal.EventStartup;
import de.inspire.ac.impl.events.internal.startup.StartTime;

@Internal
public class SystemStartupPlugin extends Plugin {

    @EventHandler
    public void onLoad(EventStartup event) {
        if (event.startTime() != StartTime.LOAD) return;

        registerListener(new PacketListener());
    }

    private void registerListener(PacketListener listener) {
        registerListener(listener, PacketListenerPriority.NORMAL);
    }

    private void registerListener(PacketListener listener, PacketListenerPriority priority) {
        PacketEvents.getAPI().getEventManager().registerListener(listener, priority);
    }

    // Event adapter
    public static class PacketListener implements com.github.retrooper.packetevents.event.PacketListener {

        @Override
        public void onPacketReceive(PacketReceiveEvent event) {
            API.INSTANCE.postEvent(new EventReceivePacket(event));
        }

        @Override
        public void onPacketSend(PacketSendEvent event) {
            API.INSTANCE.postEvent(new EventSendPacket(event));
        }
    }
}
