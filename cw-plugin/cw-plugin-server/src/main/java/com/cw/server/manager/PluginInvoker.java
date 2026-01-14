package com.cw.server.manager;

import java.lang.reflect.Method;

/**
 * @author thisdcw
 * @date 2026年01月09日 16:45
 */
@FunctionalInterface
public interface PluginInvoker {
    void invoke(Object plugin, Method method) throws Exception;
}

