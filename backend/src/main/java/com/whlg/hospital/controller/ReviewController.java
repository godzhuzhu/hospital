package com.whlg.hospital.controller;

import com.whlg.hospital.dto.CreateReviewRequest;
import com.whlg.hospital.service.UserCenterService;
import com.whlg.hospital.util.R;
import com.whlg.hospital.vo.PageResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final UserCenterService userCenterService;

    public ReviewController(UserCenterService userCenterService) {
        this.userCenterService = userCenterService;
    }

    @GetMapping("/my")
    public R<PageResult<Map<String, Object>>> mine(@RequestParam(required = false) Integer page,
                                                   @RequestParam(required = false) Integer pageSize) {
        return R.ok(userCenterService.listMyReviews(page, pageSize));
    }

    @PostMapping
    public R<Map<String, Object>> create(@RequestBody CreateReviewRequest request) {
        return R.ok(userCenterService.createReview(request));
    }
}