package com.whlg.hospital.controller;

import com.whlg.hospital.service.MedicalResourceService;
import com.whlg.hospital.util.R;
import com.whlg.hospital.vo.PageResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final MedicalResourceService medicalResourceService;

    public ArticleController(MedicalResourceService medicalResourceService) {
        this.medicalResourceService = medicalResourceService;
    }

    @GetMapping
    public R<PageResult<Map<String, Object>>> list(@RequestParam(required = false) Integer page,
                                                   @RequestParam(required = false) Integer pageSize,
                                                   @RequestParam(required = false) Long departmentId,
                                                   @RequestParam(required = false) String keyword) {
        return R.ok(medicalResourceService.listArticles(page, pageSize, departmentId, keyword));
    }

    @GetMapping("/{id}")
    public R<Map<String, Object>> detail(@PathVariable("id") Long id) {
        return R.ok(medicalResourceService.getArticleDetail(id));
    }
}