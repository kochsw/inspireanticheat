package de.inspire.ac.api.plugins;

import de.inspire.ac.api.eventbus.EventBus;
import lombok.Getter;

import java.lang.invoke.MethodHandles;

@Getter
public abstract class PluginPipeline {

    private final EventBus eventBus;

    public PluginPipeline(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void subscribe(Object plugin) {
        eventBus.subscribe(plugin);
    }

    public void unsubscribe(Object plugin) {
        eventBus.unsubscribe(plugin);
    }

    protected abstract void registerCall(Class<? extends Plugin> plugin);

    public void register(Class<? extends Plugin> plugin) {
        registerCall(plugin);

        // Other logic... (no further logic has been implemented at this time)
    }

    protected void registerLambda(Class<? extends Plugin> plugin) {
        getEventBus().registerLambdaFactory(
                plugin.getPackageName(),
                (lookupInMethod, clazz) ->
                        (MethodHandles.Lookup) lookupInMethod.invoke(null, clazz, MethodHandles.lookup())
        );
    }
}
