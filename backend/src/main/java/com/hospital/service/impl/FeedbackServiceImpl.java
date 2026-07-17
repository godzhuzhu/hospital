package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.entity.Feedback;
import com.hospital.mapper.FeedbackMapper;
import com.hospital.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Override
    public IPage<Feedback> getMyFeedbacks(Long userId, int page, int pageSize) {
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feedback::getUserId, userId).orderByDesc(Feedback::getCreateTime);
        return feedbackMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public void submit(Feedback feedback) {
        feedback.setStatus(1);
        feedbackMapper.insert(feedback);
    }
}
