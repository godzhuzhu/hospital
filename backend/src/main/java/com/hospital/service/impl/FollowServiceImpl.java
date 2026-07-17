package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.entity.*;
import com.hospital.mapper.*;
import com.hospital.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private HospitalMapper hospitalMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private DiseaseMapper diseaseMapper;

    @Override
    public IPage<Map<String, Object>> getMyFollows(Long userId, Integer type, int page, int pageSize) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getUserId, userId)
               .eq(type != null, Follow::getFollowType, type)
               .orderByDesc(Follow::getCreateTime);

        IPage<Follow> followPage = followMapper.selectPage(new Page<>(page, pageSize), wrapper);
        
        IPage<Map<String, Object>> result = new Page<>(page, pageSize, followPage.getTotal());
        List<Map<String, Object>> records = new ArrayList<>();

        for (Follow follow : followPage.getRecords()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", follow.getId());
            item.put("followType", follow.getFollowType());
            item.put("followId", follow.getFollowId());
            item.put("createTime", follow.getCreateTime());

            if (follow.getFollowType() == 1) {
                Hospital hospital = hospitalMapper.selectById(follow.getFollowId());
                if (hospital != null) {
                    item.put("name", hospital.getName());
                    item.put("image", hospital.getImage());
                    item.put("level", hospital.getLevel());
                }
            } else if (follow.getFollowType() == 2) {
                Doctor doctor = doctorMapper.selectById(follow.getFollowId());
                if (doctor != null) {
                    item.put("name", doctor.getName());
                    item.put("image", doctor.getAvatar());
                    item.put("title", doctor.getTitle());
                }
            } else if (follow.getFollowType() == 3) {
                Disease disease = diseaseMapper.selectById(follow.getFollowId());
                if (disease != null) {
                    item.put("name", disease.getName());
                }
            }
            records.add(item);
        }
        result.setRecords(records);
        return result;
    }

    @Override
    public void follow(Long userId, Integer type, Long followId) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getUserId, userId)
               .eq(Follow::getFollowType, type)
               .eq(Follow::getFollowId, followId);
        if (followMapper.selectCount(wrapper) > 0) return;

        Follow follow = new Follow();
        follow.setUserId(userId);
        follow.setFollowType(type);
        follow.setFollowId(followId);
        followMapper.insert(follow);
    }

    @Override
    public void unfollow(Long userId, Integer type, Long followId) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getUserId, userId)
               .eq(Follow::getFollowType, type)
               .eq(Follow::getFollowId, followId);
        followMapper.delete(wrapper);
    }

    @Override
    public boolean isFollowing(Long userId, Integer type, Long followId) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getUserId, userId)
               .eq(Follow::getFollowType, type)
               .eq(Follow::getFollowId, followId);
        return followMapper.selectCount(wrapper) > 0;
    }
}
