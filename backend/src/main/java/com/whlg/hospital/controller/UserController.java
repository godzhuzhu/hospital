package com.whlg.hospital.controller;

import com.whlg.hospital.dto.UpdateProfileRequest;
import com.whlg.hospital.service.UserCenterService;
import com.whlg.hospital.util.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserCenterService userCenterService;

    public UserController(UserCenterService userCenterService) {
        this.userCenterService = userCenterService;
    }

    @GetMapping("/profile")
    public R<Map<String, Object>> profile() {
        return R.ok(userCenterService.getProfile());
    }

    @PutMapping("/profile")
    public R<Object> updateProfile(@RequestBody UpdateProfileRequest request) {
        userCenterService.updateProfile(request);
        return R.ok();
    }
}