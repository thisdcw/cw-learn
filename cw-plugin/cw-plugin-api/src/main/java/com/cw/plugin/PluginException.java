package com.cw.plugin;

/**
 * @author thisdcw
 * @date 2026年01月13日 11:21
 */
public class PluginException extends RuntimeException{

    public PluginException() {
        super();
    }

    public PluginException(String message) {
        super(message);
    }

    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }
}
