package com.cw.server.biz;

import com.cw.server.hook.PluginHook;
import com.cw.server.manager.PluginManager;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author thisdcw
 * @date 2026年01月12日 15:32
 */
@Slf4j
public abstract class AbstractPlugin implements PluginHook {

    protected final PluginManager pluginManager = PluginManager.getInstance();

    protected final AtomicBoolean allow = new AtomicBoolean(false);

    public AbstractPlugin() {
        pluginManager.registerHook(this);
    }

    protected void checkAllow() {
        if (!allow.get()) {
            log.error("业务不可用");
            throw new RuntimeException("业务不可用");
        }
    }

    @Override
    public void onInstall() {
        log.info("插件安装了");
    }

    @Override
    public void onUpdate() {
        log.info("插件更新了");
    }

    @Override
    public void onStart() {
        log.info("插件启动了");
        allow.set(true);
    }

    @Override
    public void onUninstall() {
        log.info("插件卸载了");
        allow.set(false);
    }
}
