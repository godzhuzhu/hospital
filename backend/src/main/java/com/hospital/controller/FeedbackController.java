package com.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hospital.common.Result;
import com.hospital.entity.Feedback;
import com.hospital.service.FeedbackService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/my")
    public Result<IPage<Feedback>> myFeedbacks(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int pageSize,
                                                HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(feedbackService.getMyFeedbacks(userId, page, pageSize));
    }

    @PostMapping
    public Result<Void> submit(@RequestBody Feedback feedback,
                               HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        feedback.setUserId(userId);
        feedbackService.submit(feedback);
        return Result.ok();
    }
}
