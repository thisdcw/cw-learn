package com.cw.server.service.impl;

import com.cw.plugin.PluginContext;
import com.cw.plugin.PluginException;
import com.cw.server.manager.PluginManager;
import com.cw.server.service.PluginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author thisdcw
 * @date 2026年01月08日 16:38
 */
@Service
@Slf4j
public class PluginServiceImpl implements PluginService {

    @Override
    public String install(MultipartFile file) {
        File tempFile = null;
        try {
            // 1. 保存临时文件
            tempFile = File.createTempFile("plugin-", ".jar");
            file.transferTo(tempFile);

            // 2. 安装插件
            String pluginId = PluginManager.getInstance().install(tempFile);

            // 3. 自动启动插件
            PluginManager.getInstance().start(pluginId);

            return pluginId;
        } catch (IOException | PluginException e) {
            log.error("插件安装失败", e);
            throw new RuntimeException("插件安装失败: " + e.getMessage(), e);
        } finally {
            // 4. 清理临时文件
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    @Override
    public void start(String pluginId) {
        try {
            PluginManager.getInstance().start(pluginId);
        } catch (PluginException e) {
            throw new RuntimeException("插件启动失败", e);
        }
    }

    @Override
    public void stop(String pluginId) {
        try {
            PluginManager.getInstance().stop(pluginId);
        } catch (PluginException e) {
            throw new RuntimeException("插件停止失败", e);
        }
    }

    @Override
    public void uninstall(String pluginId) {
        try {
            PluginManager.getInstance().uninstall(pluginId);
        } catch (PluginException e) {
            throw new RuntimeException("插件卸载失败", e);
        }
    }

    @Override
    public List<PluginContext> listPlugins() {
        return PluginManager.getInstance().getAllPlugins();
    }
}
