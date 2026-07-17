package com.hospital.controller;

import com.hospital.common.Result;
import com.hospital.entity.User;
import com.hospital.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("")
    private String uploadPath;

    @GetMapping("/profile")
    public Result<Map<String, Object>> profile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getProfile(userId);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("phone", user.getPhone());
        data.put("email", user.getEmail());
        data.put("realName", user.getRealName());
        data.put("gender", user.getGender());
        data.put("birthday", user.getBirthday());
        data.put("avatar", user.getAvatar());
        return Result.ok(data);
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody Map<String, String> body,
                                       HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        User updates = new User();
        updates.setEmail(body.get("email"));
        updates.setRealName(body.get("realName"));

        String gender = body.get("gender");
        if (gender != null) updates.setGender(Integer.valueOf(gender));

        String birthday = body.get("birthday");
        if (birthday != null) updates.setBirthday(java.time.LocalDate.parse(birthday));

        userService.updateProfile(userId, updates);
        return Result.ok();
    }

    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file,
                                                      HttpServletRequest request) {
        if (file.isEmpty()) {
            return Result.fail(400, "文件为空");
        }

        Long userId = (Long) request.getAttribute("userId");
        try {
            File dir = new File(uploadPath + "/avatar");
            if (!dir.exists()) dir.mkdirs();

            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + ext;
            File dest = new File(dir, fileName);
            file.transferTo(dest);

            String avatarPath = "/uploads/avatar/" + fileName;
            userService.uploadAvatar(userId, avatarPath);

            Map<String, String> data = new LinkedHashMap<>();
            data.put("avatar", avatarPath);
            return Result.ok(data);
        } catch (IOException e) {
            return Result.fail(500, "上传失败：" + e.getMessage());
        }
    }
}
