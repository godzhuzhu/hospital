package com.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hospital.entity.Message;

public interface MessageService {
    IPage<Message> getMyMessages(Long userId, int page, int pageSize);
    void markRead(Long id, Long userId);
}
