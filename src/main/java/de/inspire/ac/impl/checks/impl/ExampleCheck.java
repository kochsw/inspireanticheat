package de.inspire.ac.impl.checks.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import de.inspire.ac.api.eventbus.annotations.EventHandler;
import de.inspire.ac.impl.events.ingame.EventReceivePacket;
import de.inspire.ac.impl.checks.api.Check;
import de.inspire.ac.impl.checks.api.annotations.CheckInfo;
import de.inspire.ac.impl.checks.api.enums.Category;

@CheckInfo(name = "Example", description = "Just example", category = Category.OTHER)
public class ExampleCheck extends Check {

    @EventHandler
    public void onReceive(EventReceivePacket e) {
        PacketReceiveEvent packet = e.packet();

        User user = packet.getUser();

        user.sendMessage(packet.getPacketType().getName());

        if (packet.getPacketType().getName().equals(PacketType.Play.Client.INTERACT_ENTITY.name())) { // Yeah.
            WrapperPlayClientInteractEntity interact = new WrapperPlayClientInteractEntity(packet);

            user.sendMessage("Attack");

            if (interact.isSneaking().isEmpty()) return;

            if (interact.isSneaking().get()) {
                user.sendMessage("You cant sneak!");
                packet.setCancelled(true);
            }
        }
    }
}
