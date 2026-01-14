package com.cw.plugin;

import com.cw.plugin.spi.Plugin;

import java.net.URLClassLoader;

/**
 * @author thisdcw
 * @date 2026年01月13日 11:19
 */
public class PluginContext {

    private final String pluginId;
    private final Plugin plugin;
    private final URLClassLoader classLoader;
    private final PluginMetadata metadata;
    private PluginState state;

    public PluginContext(String pluginId, Plugin plugin,
                         URLClassLoader classLoader, PluginMetadata metadata) {
        this.pluginId = pluginId;
        this.plugin = plugin;
        this.classLoader = classLoader;
        this.metadata = metadata;
        this.state = PluginState.INSTALLED;
    }

    public String getPluginId() {
        return pluginId;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }

    public PluginMetadata getMetadata() {
        return metadata;
    }

    public PluginState getState() {
        return state;
    }

    public void setState(PluginState state) {
        this.state = state;
    }

}
