package de.inspire.ac;

import com.github.retrooper.packetevents.PacketEvents;
import de.inspire.ac.api.API;
import de.inspire.ac.api.utils.console.ConsoleColors;
import de.inspire.ac.impl.events.internal.startup.StartTime;
import de.inspire.ac.impl.events.internal.EventStartup;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@Getter @FieldDefaults(level = AccessLevel.PRIVATE)
public final class InspirePlugin extends JavaPlugin {

    private static final String ASCII = """
                _                  _        \s
               (_)___  _________  (_)_______\s
              / / __ \\/ ___/ __ \\/ / ___/ _ \\
             / / / / (__  ) /_/ / / /  /  __/
            /_/_/ /_/____/ .___/_/_/   \\___/\s
                        /_/                 \s
            """;

    public static final Logger LOGGER = Inspire.INSTANCE.getLogger();

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();

        API.INSTANCE.postEvent(new EventStartup(this, StartTime.LOAD));
    }

    @Override
    public void onEnable() {
        API.INSTANCE.postEvent(new EventStartup(this, StartTime.ENABLE_PRE));

        PacketEvents.getAPI().init();

        printLogo();

        API.INSTANCE.postEvent(new EventStartup(this, StartTime.ENABLE_POST));
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }

    private void printLogo() {
        Arrays.stream(ASCII.split("\n")).forEach(line ->
                LOGGER.info("  {}", ConsoleColors.applyTheme(line))
        );

        LOGGER.info(
                "{}Inspire AntiCheat v{}.{}",
                ConsoleColors.getTheme(), this.getDescription().getVersion(), ConsoleColors.RESET
        );
    }
}
