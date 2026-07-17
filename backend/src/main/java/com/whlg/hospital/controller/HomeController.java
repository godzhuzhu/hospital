package com.whlg.hospital.controller;

import com.whlg.hospital.service.MedicalResourceService;
import com.whlg.hospital.util.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final MedicalResourceService medicalResourceService;

    public HomeController(MedicalResourceService medicalResourceService) {
        this.medicalResourceService = medicalResourceService;
    }

    @GetMapping("/index")
    public R<Map<String, Object>> index() {
        return R.ok(medicalResourceService.homeIndex());
    }
}