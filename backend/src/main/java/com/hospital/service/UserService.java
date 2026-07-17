package com.hospital.service;

import com.hospital.entity.User;

public interface UserService {
    User getProfile(Long userId);
    void updateProfile(Long userId, User updates);
    String uploadAvatar(Long userId, String avatarPath);
}
