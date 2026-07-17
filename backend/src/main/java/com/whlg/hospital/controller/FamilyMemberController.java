package com.whlg.hospital.controller;

import com.whlg.hospital.dto.FamilyMemberRequest;
import com.whlg.hospital.service.UserCenterService;
import com.whlg.hospital.util.R;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/family-members")
public class FamilyMemberController {

    private final UserCenterService userCenterService;

    public FamilyMemberController(UserCenterService userCenterService) {
        this.userCenterService = userCenterService;
    }

    @GetMapping
    public R<List<Map<String, Object>>> list() {
        return R.ok(userCenterService.listFamilyMembers());
    }

    @PostMapping
    public R<Map<String, Object>> create(@RequestBody FamilyMemberRequest request) {
        return R.ok(userCenterService.createFamilyMember(request));
    }

    @PutMapping("/{id}")
    public R<Object> update(@PathVariable("id") Long id, @RequestBody FamilyMemberRequest request) {
        userCenterService.updateFamilyMember(id, request);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Object> delete(@PathVariable("id") Long id) {
        userCenterService.deleteFamilyMember(id);
        return R.ok();
    }
}