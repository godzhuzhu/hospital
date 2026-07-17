package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hospital.entity.FamilyMember;
import com.hospital.mapper.FamilyMemberMapper;
import com.hospital.service.FamilyMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FamilyMemberServiceImpl implements FamilyMemberService {

    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Override
    public List<FamilyMember> listByUserId(Long userId) {
        LambdaQueryWrapper<FamilyMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FamilyMember::getUserId, userId).orderByDesc(FamilyMember::getCreateTime);
        return familyMemberMapper.selectList(wrapper);
    }

    @Override
    public FamilyMember getById(Long id, Long userId) {
        LambdaQueryWrapper<FamilyMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FamilyMember::getId, id).eq(FamilyMember::getUserId, userId);
        return familyMemberMapper.selectOne(wrapper);
    }

    @Override
    public void add(FamilyMember member) {
        familyMemberMapper.insert(member);
    }

    @Override
    public void update(FamilyMember member, Long userId) {
        member.setUserId(userId);
        familyMemberMapper.updateById(member);
    }

    @Override
    public void delete(Long id, Long userId) {
        LambdaQueryWrapper<FamilyMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FamilyMember::getId, id).eq(FamilyMember::getUserId, userId);
        familyMemberMapper.delete(wrapper);
    }
}
