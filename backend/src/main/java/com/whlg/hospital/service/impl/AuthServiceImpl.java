package com.whlg.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.whlg.hospital.dto.ChangePasswordRequest;
import com.whlg.hospital.dto.LoginRequest;
import com.whlg.hospital.dto.RegisterRequest;
import com.whlg.hospital.entity.User;
import com.whlg.hospital.entity.UserToken;
import com.whlg.hospital.mapper.UserMapper;
import com.whlg.hospital.mapper.UserTokenMapper;
import com.whlg.hospital.service.AuthService;
import com.whlg.hospital.support.ApiException;
import com.whlg.hospital.support.CurrentUserHolder;
import com.whlg.hospital.util.PasswordUtil;
import com.whlg.hospital.util.StatusCode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AuthServiceImpl extends ServiceSupport implements AuthService {

    private final UserMapper userMapper;
    private final UserTokenMapper userTokenMapper;

    public AuthServiceImpl(UserMapper userMapper, UserTokenMapper userTokenMapper) {
        this.userMapper = userMapper;
        this.userTokenMapper = userTokenMapper;
    }

    @Override
    public Map<String, Object> register(RegisterRequest request) {
        check(request != null, "请求体不能为空");
        check(request.getPhone() != null && !request.getPhone().trim().isEmpty(), "手机号不能为空");
        check(request.getPassword() != null && !request.getPassword().trim().isEmpty(), "密码不能为空");
        check(request.getPassword().equals(request.getConfirmPassword()), "两次密码不一致");
        User exists = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, request.getPhone()));
        if (exists != null) {
            throw new ApiException(StatusCode.BAD_REQUEST, "手机号已注册");
        }

        User user = new User();
        user.setUsername(request.getPhone());
        user.setPhone(request.getPhone());
        user.setPassword(PasswordUtil.md5(request.getPassword()));
        user.setRealName("用户" + request.getPhone().substring(request.getPhone().length() - 4));
        user.setAvatar("/avatar/default.png");
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("userId", user.getId());
        return result;
    }

    @Override
    public Map<String, Object> login(LoginRequest request) {
        check(request != null, "请求体不能为空");
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, request.getPhone()));
        check(user != null && PasswordUtil.matches(request.getPassword(), user.getPassword()), "手机号或密码错误");
        upgradePasswordIfLegacy(user, request.getPassword());

        String token = "token-" + user.getId() + "-" + System.nanoTime();
        UserToken userToken = new UserToken();
        userToken.setUserId(user.getId());
        userToken.setToken(token);
        userToken.setStatus(1);
        userToken.setExpireTime(LocalDateTime.now().plusDays(7));
        userToken.setCreateTime(LocalDateTime.now());
        userToken.setUpdateTime(LocalDateTime.now());
        userTokenMapper.insert(userToken);

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("token", token);
        result.put("userInfo", buildUserInfo(user));
        return result;
    }

    @Override
    public Map<String, Object> me() {
        User user = userMapper.selectById(requireUserId());
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("userInfo", buildUserInfo(user));
        return result;
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        Long userId = requireUserId();
        User user = userMapper.selectById(userId);
        check(PasswordUtil.matches(request.getOldPassword(), user.getPassword()), "旧密码错误");
        check(request.getNewPassword() != null && !request.getNewPassword().trim().isEmpty(), "新密码不能为空");
        check(request.getNewPassword().equals(request.getConfirmPassword()), "两次密码不一致");
        user.setPassword(PasswordUtil.md5(request.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        disableUserTokens(userId);
    }

    @Override
    public void logout(String authorization) {
        check(authorization != null && authorization.startsWith("Bearer "), "未登录");
        String token = authorization.substring("Bearer ".length()).trim();
        userTokenMapper.update(null, new LambdaUpdateWrapper<UserToken>()
                .eq(UserToken::getToken, token)
                .set(UserToken::getStatus, 0)
                .set(UserToken::getUpdateTime, LocalDateTime.now()));
        CurrentUserHolder.clear();
    }

    private void disableUserTokens(Long userId) {
        userTokenMapper.update(null, new LambdaUpdateWrapper<UserToken>()
                .eq(UserToken::getUserId, userId)
                .eq(UserToken::getStatus, 1)
                .set(UserToken::getStatus, 0)
                .set(UserToken::getUpdateTime, LocalDateTime.now()));
    }

    private void upgradePasswordIfLegacy(User user, String rawPassword) {
        if (user == null || rawPassword == null || user.getPassword() == null) {
            return;
        }
        if (!user.getPassword().equals(rawPassword)) {
            return;
        }
        user.setPassword(PasswordUtil.md5(rawPassword));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    private Map<String, Object> buildUserInfo(User user) {
        Map<String, Object> userInfo = new LinkedHashMap<String, Object>();
        userInfo.put("id", user.getId());
        userInfo.put("name", user.getRealName());
        userInfo.put("phone", user.getPhone());
        userInfo.put("avatar", user.getAvatar());
        return userInfo;
    }
}