package com.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hospital.entity.Review;

public interface ReviewService {
    IPage<Review> getMyReviews(Long userId, int page, int pageSize);
    void addReview(Review review);
}
