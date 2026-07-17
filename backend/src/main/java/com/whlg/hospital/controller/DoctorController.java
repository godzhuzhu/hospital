package com.whlg.hospital.controller;

import com.whlg.hospital.service.MedicalResourceService;
import com.whlg.hospital.util.R;
import com.whlg.hospital.vo.PageResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final MedicalResourceService medicalResourceService;

    public DoctorController(MedicalResourceService medicalResourceService) {
        this.medicalResourceService = medicalResourceService;
    }

    @GetMapping
    public R<PageResult<Map<String, Object>>> list(@RequestParam(required = false) Integer page,
                                                   @RequestParam(required = false) Integer pageSize,
                                                   @RequestParam(required = false) Long departmentId,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) Long hospitalId) {
        return R.ok(medicalResourceService.listDoctors(page, pageSize, departmentId, keyword, hospitalId));
    }

    @GetMapping("/{id}")
    public R<Map<String, Object>> detail(@PathVariable("id") Long id) {
        return R.ok(medicalResourceService.getDoctorDetail(id));
    }

    @GetMapping("/{id}/schedules")
    public R<List<Map<String, Object>>> schedules(@PathVariable("id") Long id,
                                                  @RequestParam(required = false) String startDate,
                                                  @RequestParam(required = false) Integer days) {
        return R.ok(medicalResourceService.getDoctorSchedules(id, startDate, days));
    }

    @GetMapping("/{id}/reviews")
    public R<PageResult<Map<String, Object>>> reviews(@PathVariable("id") Long id,
                                                      @RequestParam(required = false) Integer page,
                                                      @RequestParam(required = false) Integer pageSize) {
        return R.ok(medicalResourceService.getDoctorReviews(id, page, pageSize));
    }
}