package com.cw.plugin;

import com.cw.plugin.spi.Plugin;
import lombok.Getter;
import lombok.Setter;

import java.net.URLClassLoader;

/**
 * @author thisdcw
 * @date 2026年01月13日 11:19
 */
@Getter
public class PluginContext {

    private final String pluginId;
    private final Plugin plugin;
    private final URLClassLoader classLoader;
    private final PluginMetadata metadata;
    @Setter
    private PluginState state;

    public PluginContext(String pluginId, Plugin plugin,
                         URLClassLoader classLoader, PluginMetadata metadata) {
        this.pluginId = pluginId;
        this.plugin = plugin;
        this.classLoader = classLoader;
        this.metadata = metadata;
        this.state = PluginState.INSTALLED;
    }

}
