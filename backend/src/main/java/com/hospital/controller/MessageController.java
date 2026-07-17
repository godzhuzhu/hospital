package com.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hospital.common.Result;
import com.hospital.entity.Message;
import com.hospital.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    public Result<IPage<Message>> list(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int pageSize,
                                        HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(messageService.getMyMessages(userId, page, pageSize));
    }

    @PostMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id,
                                  HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        messageService.markRead(id, userId);
        return Result.ok();
    }
}
