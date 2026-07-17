package com.whlg.hospital.controller;

import com.whlg.hospital.service.SystemService;
import com.whlg.hospital.util.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final SystemService systemService;

    public HealthController(SystemService systemService) {
        this.systemService = systemService;
    }

    @GetMapping("/health")
    public R<Map<String, Object>> health() {
        return R.ok(systemService.health());
    }
}