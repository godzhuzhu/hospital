package com.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hospital.entity.Feedback;

public interface FeedbackService {
    IPage<Feedback> getMyFeedbacks(Long userId, int page, int pageSize);
    void submit(Feedback feedback);
}
