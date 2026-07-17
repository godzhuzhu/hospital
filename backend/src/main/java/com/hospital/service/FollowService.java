package com.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hospital.entity.Follow;

public interface FollowService {
    IPage<?> getMyFollows(Long userId, Integer type, int page, int pageSize);
    void follow(Long userId, Integer type, Long followId);
    void unfollow(Long userId, Integer type, Long followId);
    boolean isFollowing(Long userId, Integer type, Long followId);
}
