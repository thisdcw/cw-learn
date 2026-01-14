package com.cw.plugin.service;

import com.cw.plugin.PluginException;
import com.cw.plugin.PluginState;
import com.cw.plugin.spi.Plugin;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author thisdcw
 * @date 2026年01月13日 13:22
 */
@Slf4j
public class TrafficLimiterPlugin implements Plugin {
    private ScheduledExecutorService scheduler;
    private volatile PluginState state = PluginState.INSTALLED;

    @Override
    public String getId() {
        return "traffic-limiter";
    }

    @Override
    public String getName() {
        return "流量限制器";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public void start() throws PluginException {
        log.info("启动流量限制插件...");
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            checkTraffic();
        }, 0, 1, TimeUnit.MINUTES);
        state = PluginState.STARTED;
    }

    @Override
    public void stop() throws PluginException {
        log.info("停止流量限制插件...");
        if (scheduler != null) {
            scheduler.shutdown();
        }
        state = PluginState.STOPPED;
    }

    @Override
    public PluginState getState() {
        return state;
    }

    private void checkTraffic() {
        // 检查租户流量
    }
}
