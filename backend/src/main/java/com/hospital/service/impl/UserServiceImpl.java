package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hospital.entity.User;
import com.hospital.mapper.UserMapper;
import com.hospital.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getProfile(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public void updateProfile(Long userId, User updates) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, userId);

        if (updates.getEmail() != null) wrapper.set(User::getEmail, updates.getEmail());
        if (updates.getRealName() != null) wrapper.set(User::getRealName, updates.getRealName());
        if (updates.getGender() != null) wrapper.set(User::getGender, updates.getGender());
        if (updates.getBirthday() != null) wrapper.set(User::getBirthday, updates.getBirthday());

        userMapper.update(null, wrapper);
    }

    @Override
    public String uploadAvatar(Long userId, String avatarPath) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, userId).set(User::getAvatar, avatarPath);
        userMapper.update(null, wrapper);
        return avatarPath;
    }
}
