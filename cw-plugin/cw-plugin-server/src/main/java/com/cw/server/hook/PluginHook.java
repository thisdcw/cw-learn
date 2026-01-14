package com.cw.server.hook;

/**
 * @author thisdcw
 * @date 2026年01月09日 17:08
 */
public interface PluginHook {

    /**
     * 插件被加载
     */
    default void onInstall() {
    }

    /**
     * 插件被更新
     */
    default void onUpdate() {
    }

    /**
     * 插件即将被执行
     */
    default void onStart() {
    }

    /**
     * 插件被卸载
     */
    default void onUninstall() {
    }

}
