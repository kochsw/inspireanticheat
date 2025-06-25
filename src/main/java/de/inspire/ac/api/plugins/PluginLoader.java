package de.inspire.ac.api.plugins;

import de.inspire.ac.api.loaders.Loader;
import de.inspire.ac.api.loaders.impl.SPILoader;
import de.inspire.ac.api.plugins.exceptions.NotInitializedException;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class PluginLoader {

    private Loader<Plugin> loader = new SPILoader<>(Plugin.class);
    private PluginPipeline pipeline;
    private StaticLoader staticLoader = new StaticLoader() {
        @Override
        public List<Plugin> load() {
            return new ArrayList<>();
        }
    };

    private List<Plugin> plugins;

    public static final Logger LOGGER = LogManager.getLogger(PluginLoader.class.getName());

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

    public PluginLoader staticLoader(StaticLoader staticLoader) {
        this.staticLoader = staticLoader;
        return this;
    }

    public PluginLoader build() {

        if (pipeline == null || staticLoader == null || loader == null)
            throw new NotInitializedException("One of the important fields is not initialized.");

        plugins = new ArrayList<>();

        plugins.addAll(staticLoader.load());
        plugins.addAll(getLoader().load());

        for (Plugin plugin : plugins) {
            getPipeline().register(plugin.getClass());
            getPipeline().subscribe(plugin);
        }

        LOGGER.info("Loaded and registered {} plugins.", plugins.size());
        return this;
    }
}
