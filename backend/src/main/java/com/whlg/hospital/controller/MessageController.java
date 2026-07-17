package com.whlg.hospital.controller;

import com.whlg.hospital.service.UserCenterService;
import com.whlg.hospital.util.R;
import com.whlg.hospital.vo.PageResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final UserCenterService userCenterService;

    public MessageController(UserCenterService userCenterService) {
        this.userCenterService = userCenterService;
    }

    @GetMapping
    public R<PageResult<Map<String, Object>>> list(@RequestParam(required = false) Integer page,
                                                   @RequestParam(required = false) Integer pageSize) {
        return R.ok(userCenterService.listMessages(page, pageSize));
    }

    @PostMapping("/{id}/read")
    public R<Object> markRead(@PathVariable("id") Long id) {
        userCenterService.markMessageRead(id);
        return R.ok();
    }

    @GetMapping("/unread-count")
    public R<Map<String, Object>> unreadCount() {
        return R.ok(userCenterService.unreadCount());
    }
}