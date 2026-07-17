package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.entity.Message;
import com.hospital.mapper.MessageMapper;
import com.hospital.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public IPage<Message> getMyMessages(Long userId, int page, int pageSize) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId).orderByDesc(Message::getCreateTime);
        return messageMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public void markRead(Long id, Long userId) {
        LambdaUpdateWrapper<Message> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Message::getId, id).eq(Message::getUserId, userId)
               .set(Message::getIsRead, 1);
        messageMapper.update(null, wrapper);
    }
}
