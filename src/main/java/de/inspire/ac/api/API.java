package de.inspire.ac.api;

import de.inspire.ac.api.eventbus.EventBus;
import de.inspire.ac.api.plugins.Plugin;
import de.inspire.ac.api.plugins.PluginLoader;
import de.inspire.ac.api.plugins.PluginPipeline;
import de.inspire.ac.api.plugins.StaticLoader;
import de.inspire.ac.impl.plugins.DataRefreshPlugin;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum API {

    INSTANCE;

    EventBus eventBus = new EventBus();

    PluginLoader pluginLoader = PluginLoader.builder()
            .staticLoader(new StaticLoader() {

                @Override
                public List<Plugin> load() {
                    return new ArrayList<>() {{
                        add(new DataRefreshPlugin());
                    }};
                }
            })
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
