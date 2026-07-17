package com.whlg.hospital.controller;

import com.whlg.hospital.service.SystemService;
import com.whlg.hospital.util.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/configs")
public class ConfigController {

    private final SystemService systemService;

    public ConfigController(SystemService systemService) {
        this.systemService = systemService;
    }

    @GetMapping("/{key}")
    public R<Map<String, Object>> getConfig(@PathVariable("key") String key) {
        return R.ok(systemService.getConfig(key));
    }
}