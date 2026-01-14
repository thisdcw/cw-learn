package com.cw.server.manager;

import com.cw.plugin.PluginContext;
import com.cw.plugin.PluginException;
import com.cw.plugin.PluginMetadata;
import com.cw.plugin.PluginState;
import com.cw.plugin.spi.Plugin;
import com.cw.server.hook.PluginHook;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author thisdcw
 * @date 2026年01月09日 16:18
 */
@Slf4j
public class PluginManager {

    private static final PluginManager INSTANCE = new PluginManager();

    private final Map<String, PluginContext> pluginContexts = new ConcurrentHashMap<>();
    private final List<PluginHook> hooks = new ArrayList<>();

    private PluginManager() {}

    public static PluginManager getInstance() {
        return INSTANCE;
    }

    public void registerHook(PluginHook hook) {
        hooks.add(hook);
    }

    /**
     * 安装插件
     */
    public synchronized String install(File jarFile) throws PluginException {
        URLClassLoader classLoader = null;
        try {
            // 1. 解析插件元数据
            PluginMetadata metadata = parseMetadata(jarFile);

            // 2. 检查是否已存在
            if (pluginContexts.containsKey(metadata.getId())) {
                return update(metadata.getId(), jarFile);
            }

            // 3. 创建独立的类加载器
            URL jarUrl = jarFile.toURI().toURL();
            classLoader = new URLClassLoader(
                    new URL[]{jarUrl},
                    this.getClass().getClassLoader()
            );

            // 4. 加载插件主类
            Class<?> pluginClass = classLoader.loadClass(metadata.getMainClass());

            // 5. 验证插件接口
            if (!Plugin.class.isAssignableFrom(pluginClass)) {
                throw new PluginException("插件必须实现 Plugin 接口");
            }

            // 6. 实例化插件
            Plugin plugin = (Plugin) pluginClass.getDeclaredConstructor().newInstance();

            // 7. 创建插件上下文
            PluginContext context = new PluginContext(
                    metadata.getId(), plugin, classLoader, metadata
            );

            // 8. 保存上下文
            pluginContexts.put(metadata.getId(), context);

            // 9. 触发安装钩子
            hooks.forEach(PluginHook::onInstall);

            log.info("插件 {} 安装成功", metadata.getId());
            return metadata.getId();

        } catch (Exception e) {
            // 清理资源
            if (classLoader != null) {
                try {
                    classLoader.close();
                } catch (IOException ex) {
                    log.error("关闭类加载器失败", ex);
                }
            }
            throw new PluginException("插件安装失败", e);
        }
    }

    /**
     * 启动插件
     */
    public synchronized void start(String pluginId) throws PluginException {
        PluginContext context = pluginContexts.get(pluginId);
        if (context == null) {
            throw new PluginException("插件不存在: " + pluginId);
        }

        if (context.getState() == PluginState.STARTED) {
            log.warn("插件 {} 已经启动", pluginId);
            return;
        }

        try {
            context.getPlugin().start();
            context.setState(PluginState.STARTED);
            hooks.forEach(PluginHook::onStart);
            log.info("插件 {} 启动成功", pluginId);
        } catch (Exception e) {
            context.setState(PluginState.FAILED);
            throw new PluginException("插件启动失败", e);
        }
    }

    /**
     * 停止插件
     */
    public synchronized void stop(String pluginId) throws PluginException {
        PluginContext context = pluginContexts.get(pluginId);
        if (context == null) {
            throw new PluginException("插件不存在: " + pluginId);
        }

        try {
            context.getPlugin().stop();
            context.setState(PluginState.STOPPED);
            log.info("插件 {} 停止成功", pluginId);
        } catch (Exception e) {
            throw new PluginException("插件停止失败", e);
        }
    }

    /**
     * 卸载插件
     */
    public synchronized void uninstall(String pluginId) throws PluginException {
        PluginContext context = pluginContexts.remove(pluginId);
        if (context == null) {
            log.warn("插件 {} 不存在", pluginId);
            return;
        }

        try {
            // 1. 先停止插件
            if (context.getState() == PluginState.STARTED) {
                stop(pluginId);
            }

            // 2. 关闭类加载器
            context.getClassLoader().close();

            // 3. 触发卸载钩子
            hooks.forEach(PluginHook::onUninstall);

            log.info("插件 {} 卸载成功", pluginId);
        } catch (Exception e) {
            throw new PluginException("插件卸载失败", e);
        }
    }

    /**
     * 更新插件
     */
    private String update(String pluginId, File newJarFile) throws PluginException {
        // 1. 卸载旧版本
        uninstall(pluginId);

        // 2. 安装新版本
        String newPluginId = install(newJarFile);

        // 3. 触发更新钩子
        hooks.forEach(PluginHook::onUpdate);

        log.info("插件 {} 更新成功", pluginId);
        return newPluginId;
    }

    /**
     * 获取所有插件
     */
    public List<PluginContext> getAllPlugins() {
        return new ArrayList<>(pluginContexts.values());
    }

    /**
     * 获取插件
     */
    public PluginContext getPlugin(String pluginId) {
        return pluginContexts.get(pluginId);
    }

    /**
     * 解析插件元数据
     */
    private PluginMetadata parseMetadata(File jarFile) throws IOException {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry entry = jar.getJarEntry("plugin.properties");
            if (entry == null) {
                throw new IOException("缺少 plugin.properties 配置文件");
            }

            Properties props = new Properties();
            try (InputStream is = jar.getInputStream(entry)) {
                props.load(is);
            }

            PluginMetadata metadata = new PluginMetadata();
            metadata.setId(props.getProperty("plugin.id"));
            metadata.setName(props.getProperty("plugin.name"));
            metadata.setVersion(props.getProperty("plugin.version"));
            metadata.setDescription(props.getProperty("plugin.description"));
            metadata.setMainClass(props.getProperty("plugin.mainClass"));
            metadata.setAuthor(props.getProperty("plugin.author"));
            metadata.setUploadTime(System.currentTimeMillis());

            // 验证必填字段
            if (metadata.getId() == null || metadata.getMainClass() == null) {
                throw new IOException("plugin.id 和 plugin.mainClass 是必填项");
            }

            return metadata;
        }
    }

}
