package de.inspire.ac.api;

import de.inspire.ac.InspirePlugin;
import de.inspire.ac.api.eventbus.EventBus;
import de.inspire.ac.api.loaders.impl.MixedLoader;
import de.inspire.ac.api.loaders.impl.ReflectiveLoader;
import de.inspire.ac.api.loaders.impl.SPILoader;
import de.inspire.ac.api.plugins.Plugin;
import de.inspire.ac.api.plugins.PluginLoader;
import de.inspire.ac.api.plugins.PluginPipeline;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum API {

    INSTANCE;

    ClassLoader defaultClassLoader = InspirePlugin.class.getClassLoader();
    EventBus eventBus = new EventBus();

    PluginLoader pluginLoader = PluginLoader.builder()
            .loader(new MixedLoader<>(
                    new SPILoader<>(Plugin.class, defaultClassLoader),
                    new ReflectiveLoader<>("de.inspire.ac.impl.plugins", Plugin.class, defaultClassLoader)
            ))
            .pipeline(new PluginPipeline(eventBus) {

                @Override
                protected void registerCall(Class<? extends Plugin> plugin) {
                    this.registerLambda(plugin);
                }
            }).build();

    public void postEvent(Object event) {
        eventBus.post(event);
    }
}
