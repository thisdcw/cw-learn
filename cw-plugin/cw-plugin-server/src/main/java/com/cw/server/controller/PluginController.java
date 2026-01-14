package com.cw.server.controller;

import com.cw.server.service.PluginService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author thisdcw
 * @date 2026年01月08日 16:37
 */
@RestController
@RequestMapping("/plugin/")
@AllArgsConstructor
public class PluginController {

    private final PluginService pluginService;

    @PostMapping("install")
    public String installPlugin(@RequestPart("file") MultipartFile file) {
        pluginService.install(file);
        return "success";
    }

    @PostMapping("start")
    public String installPlugin() {
//        pluginService.start();
        return "success";
    }

}
