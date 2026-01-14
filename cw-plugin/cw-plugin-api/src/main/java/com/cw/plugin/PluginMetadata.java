package com.cw.plugin;

import lombok.Data;

/**
 * @author thisdcw
 * @date 2026年01月13日 11:20
 */
@Data
public class PluginMetadata {

    private String id;
    private String name;
    private String version;
    private String description;
    private String mainClass;
    private String author;
    private Long uploadTime;

}
