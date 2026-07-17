package com.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hospital.common.Result;
import com.hospital.entity.Follow;
import com.hospital.service.FollowService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @GetMapping("/my")
    public Result<IPage<?>> myFollows(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int pageSize,
                                       @RequestParam(required = false) Integer type,
                                       HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(followService.getMyFollows(userId, type, page, pageSize));
    }

    @PostMapping("/{type}/{id}")
    public Result<Void> follow(@PathVariable Integer type,
                               @PathVariable Long id,
                               HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        followService.follow(userId, type, id);
        return Result.ok();
    }

    @DeleteMapping("/{type}/{id}")
    public Result<Void> unfollow(@PathVariable Integer type,
                                 @PathVariable Long id,
                                 HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        followService.unfollow(userId, type, id);
        return Result.ok();
    }

    @GetMapping("/check")
    public Result<Map<String, Boolean>> check(@RequestParam Integer type,
                                               @RequestParam Long followId,
                                               HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean following = followService.isFollowing(userId, type, followId);
        return Result.ok(Map.of("following", following));
    }
}
