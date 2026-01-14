package com.cw.plugin.service;

import com.cw.plugin.PluginException;
import com.cw.plugin.PluginState;
import com.cw.plugin.spi.Plugin;

/**
 * @author thisdcw
 * @date 2026年01月08日 16:26
 */
public class SumPlugin implements Plugin {


    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getVersion() {
        return "";
    }

    @Override
    public void start() throws PluginException {

    }

    @Override
    public void stop() throws PluginException {

    }

    @Override
    public PluginState getState() {
        return null;
    }
}
