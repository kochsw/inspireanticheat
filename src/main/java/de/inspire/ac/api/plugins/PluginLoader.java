package de.inspire.ac.api.plugins;

import de.inspire.ac.api.loaders.Loader;
import de.inspire.ac.api.loaders.impl.SPILoader;
import de.inspire.ac.api.plugins.annotations.Internal;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@Getter @Setter
public class PluginLoader {

    private Loader<Plugin> loader = new SPILoader<>(Plugin.class);
    private PluginPipeline pipeline;

    private Set<Plugin> plugins = new HashSet<>();

    private volatile boolean isBuilt = false;

    public static final Logger LOGGER = LogManager.getLogger("plugin");

    protected PluginLoader() {}

    public static PluginLoader builder() {
        return new PluginLoader();
    }

    public PluginLoader loader(Loader<Plugin> loader) {
        this.loader = loader;
        return this;
    }

    public PluginLoader pipeline(PluginPipeline pipeline) {
        this.pipeline = pipeline;
        return this;
    }

    public synchronized PluginLoader build() {
        if (isBuilt) {
            LOGGER.debug("PluginLoader already built.");
            return this;
        }

        List<Plugin> newPlugins = loadUniquePlugins();

        registerPlugins(newPlugins);

        isBuilt = true;

        logPluginStats();

        return this;
    }

    private List<Plugin> loadUniquePlugins() {
        Map<String, Plugin> pluginMap = new LinkedHashMap<>();
        loader.load().stream()
                .filter(Objects::nonNull)
                .forEach(plugin -> {
                    String pluginId = plugin.getPluginId();
                    if (pluginMap.putIfAbsent(pluginId, plugin) != null) {
                        LOGGER.warn("Duplicate plugin detected and skipped: {}", pluginId);
                    }
                });

        return new ArrayList<>(pluginMap.values());
    }

    private void registerPlugins(List<Plugin> pluginsToRegister) {
        for (Plugin plugin : pluginsToRegister) {
            try {
                pipeline.register(plugin.getClass());
                pipeline.subscribe(plugin);
                plugins.add(plugin);
            } catch (Exception e) {
                LOGGER.error("Failed to register plugin: {}", plugin.getPluginId(), e);
            }
        }
    }

    private void logPluginStats() {
        long internalCount = plugins.stream()
                .filter(p -> p.getClass().isAnnotationPresent(Internal.class))
                .count();

        LOGGER.info("Loaded and registered {} plugin(s) with {} internal.", plugins.size(), internalCount);
    }

    public Set<Plugin> getPlugins() {
        return Collections.unmodifiableSet(plugins);
    }
}
