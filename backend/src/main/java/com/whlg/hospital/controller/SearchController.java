package com.whlg.hospital.controller;

import com.whlg.hospital.service.MedicalResourceService;
import com.whlg.hospital.util.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final MedicalResourceService medicalResourceService;

    public SearchController(MedicalResourceService medicalResourceService) {
        this.medicalResourceService = medicalResourceService;
    }

    @GetMapping("/global")
    public R<Map<String, Object>> global(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) String type,
                                         @RequestParam(required = false) Integer page,
                                         @RequestParam(required = false) Integer pageSize) {
        return R.ok(medicalResourceService.globalSearch(keyword, type, page, pageSize));
    }
}