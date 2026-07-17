package com.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hospital.entity.FamilyMember;
import java.util.List;

public interface FamilyMemberService {
    List<FamilyMember> listByUserId(Long userId);
    FamilyMember getById(Long id, Long userId);
    void add(FamilyMember member);
    void update(FamilyMember member, Long userId);
    void delete(Long id, Long userId);
}
