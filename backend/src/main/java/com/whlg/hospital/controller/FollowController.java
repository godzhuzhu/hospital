package com.whlg.hospital.controller;

import com.whlg.hospital.service.UserCenterService;
import com.whlg.hospital.util.R;
import com.whlg.hospital.vo.PageResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    private final UserCenterService userCenterService;

    public FollowController(UserCenterService userCenterService) {
        this.userCenterService = userCenterService;
    }

    @GetMapping("/my")
    public R<PageResult<Map<String, Object>>> mine(@RequestParam Integer type,
                                                   @RequestParam(required = false) Integer page,
                                                   @RequestParam(required = false) Integer pageSize) {
        return R.ok(userCenterService.listMyFollows(type, page, pageSize));
    }

    @PostMapping("/{type}/{id}")
    public R<Map<String, Object>> create(@PathVariable("type") Integer type, @PathVariable("id") Long id) {
        return R.ok(userCenterService.createFollow(type, id));
    }

    @DeleteMapping("/{type}/{id}")
    public R<Object> delete(@PathVariable("type") Integer type, @PathVariable("id") Long id) {
        userCenterService.deleteFollow(type, id);
        return R.ok();
    }
}