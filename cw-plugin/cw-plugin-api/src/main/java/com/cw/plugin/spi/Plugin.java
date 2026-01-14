package com.cw.plugin.spi;

import com.cw.plugin.PluginException;
import com.cw.plugin.PluginState;

/**
 * @author thisdcw
 * @date 2026年01月08日 16:22
 */
public interface Plugin {

    /**
     * 插件唯一标识
     */
    String getId();

    /**
     * 插件名称
     */
    String getName();

    /**
     * 插件版本
     */
    String getVersion();

    /**
     * 插件启动
     */
    void start() throws PluginException;

    /**
     * 插件停止
     */
    void stop() throws PluginException;

    /**
     * 获取插件状态
     */
    PluginState getState();

}
