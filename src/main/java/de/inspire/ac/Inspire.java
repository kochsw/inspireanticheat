package de.inspire.ac;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import de.inspire.ac.api.API;
import de.inspire.ac.api.loaders.impl.ReflectiveLoader;
import de.inspire.ac.api.plugins.Plugin;
import de.inspire.ac.api.plugins.PluginLoader;
import de.inspire.ac.impl.events.EventPacket;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@Getter @FieldDefaults(level = AccessLevel.PRIVATE)
public final class Inspire extends JavaPlugin {

    private static final String ASCII = """
             /$$                               /$$
            |__/                              |__/
             /$$ /$$$$$$$   /$$$$$$$  /$$$$$$  /$$  /$$$$$$   /$$$$$$
            | $$| $$__  $$ /$$_____/ /$$__  $$| $$ /$$__  $$ /$$__  $$
            | $$| $$  \\ $$|  $$$$$$ | $$  \\ $$| $$| $$  \\__/| $$$$$$$$
            | $$| $$  | $$ \\____  $$| $$  | $$| $$| $$      | $$_____/
            | $$| $$  | $$ /$$$$$$$/| $$$$$$$/| $$| $$      |  $$$$$$$
            |__/|__/  |__/|_______/ | $$____/ |__/|__/       \\_______/
                                    | $$
                                    | $$
                                    |__/""";

    ProtocolManager protocolManager;

    public static final Logger LOGGER = LogManager.getLogger(Inspire.class.getName());

    @Override
    public void onEnable() {
        Arrays.stream(ASCII.split("\n")).forEach(LOGGER::info);
        LOGGER.info("Starting Inspire AntiCheat ({}).", this.getDescription().getVersion());

        protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.values()) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                API.INSTANCE.postEvent(new EventPacket(event));
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
