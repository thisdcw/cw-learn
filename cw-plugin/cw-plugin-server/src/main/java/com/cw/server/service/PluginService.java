package com.cw.server.service;

import com.cw.plugin.PluginContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author thisdcw
 * @date 2026年01月08日 16:38
 */
public interface PluginService {

    String install(MultipartFile file);

    void start(String pluginId);

    void stop(String pluginId);

    void uninstall(String pluginId);

    List<PluginContext> listPlugins();

}
