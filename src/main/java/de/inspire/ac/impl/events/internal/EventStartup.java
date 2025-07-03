package de.inspire.ac.impl.events.internal;

import de.inspire.ac.impl.events.internal.startup.StartTime;
import org.bukkit.plugin.java.JavaPlugin;

public record EventStartup(JavaPlugin plugin, StartTime startTime) { }
