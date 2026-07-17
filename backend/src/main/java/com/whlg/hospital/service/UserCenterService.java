package com.whlg.hospital.service;

import com.whlg.hospital.dto.CreateFeedbackRequest;
import com.whlg.hospital.dto.CreateReviewRequest;
import com.whlg.hospital.dto.FamilyMemberRequest;
import com.whlg.hospital.dto.UpdateProfileRequest;
import com.whlg.hospital.vo.PageResult;

import java.util.List;
import java.util.Map;

public interface UserCenterService {

    Map<String, Object> getProfile();

    void updateProfile(UpdateProfileRequest request);

    List<Map<String, Object>> listFamilyMembers();

    Map<String, Object> createFamilyMember(FamilyMemberRequest request);

    void updateFamilyMember(Long id, FamilyMemberRequest request);

    void deleteFamilyMember(Long id);

    PageResult<Map<String, Object>> listMyReviews(Integer page, Integer pageSize);

    Map<String, Object> createReview(CreateReviewRequest request);

    PageResult<Map<String, Object>> listMessages(Integer page, Integer pageSize);

    void markMessageRead(Long id);

    Map<String, Object> unreadCount();

    PageResult<Map<String, Object>> listFeedbacks(Integer page, Integer pageSize);

    Map<String, Object> createFeedback(CreateFeedbackRequest request);

    PageResult<Map<String, Object>> listMyFollows(Integer type, Integer page, Integer pageSize);

    Map<String, Object> createFollow(Integer type, Long targetId);

    void deleteFollow(Integer type, Long targetId);
}