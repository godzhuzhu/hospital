package com.whlg.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.whlg.hospital.dto.CreateFeedbackRequest;
import com.whlg.hospital.dto.CreateReviewRequest;
import com.whlg.hospital.dto.FamilyMemberRequest;
import com.whlg.hospital.dto.UpdateProfileRequest;
import com.whlg.hospital.entity.FamilyMember;
import com.whlg.hospital.entity.Feedback;
import com.whlg.hospital.entity.Follow;
import com.whlg.hospital.entity.Message;
import com.whlg.hospital.entity.Review;
import com.whlg.hospital.entity.User;
import com.whlg.hospital.mapper.DiseaseMapper;
import com.whlg.hospital.mapper.DoctorMapper;
import com.whlg.hospital.mapper.FamilyMemberMapper;
import com.whlg.hospital.mapper.FeedbackMapper;
import com.whlg.hospital.mapper.FollowMapper;
import com.whlg.hospital.mapper.HospitalMapper;
import com.whlg.hospital.mapper.MessageMapper;
import com.whlg.hospital.mapper.ReviewMapper;
import com.whlg.hospital.mapper.UserMapper;
import com.whlg.hospital.service.UserCenterService;
import com.whlg.hospital.support.ApiException;
import com.whlg.hospital.util.StatusCode;
import com.whlg.hospital.vo.PageResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserCenterServiceImpl extends ServiceSupport implements UserCenterService {

    private final UserMapper userMapper;
    private final FamilyMemberMapper familyMemberMapper;
    private final ReviewMapper reviewMapper;
    private final DoctorMapper doctorMapper;
    private final MessageMapper messageMapper;
    private final FeedbackMapper feedbackMapper;
    private final FollowMapper followMapper;
    private final HospitalMapper hospitalMapper;
    private final DiseaseMapper diseaseMapper;

    public UserCenterServiceImpl(UserMapper userMapper,
                                 FamilyMemberMapper familyMemberMapper,
                                 ReviewMapper reviewMapper,
                                 DoctorMapper doctorMapper,
                                 MessageMapper messageMapper,
                                 FeedbackMapper feedbackMapper,
                                 FollowMapper followMapper,
                                 HospitalMapper hospitalMapper,
                                 DiseaseMapper diseaseMapper) {
        this.userMapper = userMapper;
        this.familyMemberMapper = familyMemberMapper;
        this.reviewMapper = reviewMapper;
        this.doctorMapper = doctorMapper;
        this.messageMapper = messageMapper;
        this.feedbackMapper = feedbackMapper;
        this.followMapper = followMapper;
        this.hospitalMapper = hospitalMapper;
        this.diseaseMapper = diseaseMapper;
    }

    @Override
    public Map<String, Object> getProfile() {
        User user = userMapper.selectById(requireUserId());
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("id", user.getId());
        result.put("name", user.getRealName());
        result.put("phone", user.getPhone());
        result.put("email", user.getEmail());
        result.put("avatar", user.getAvatar());
        result.put("gender", user.getGender());
        result.put("birthday", user.getBirthday());
        return result;
    }

    @Override
    public void updateProfile(UpdateProfileRequest request) {
        User user = userMapper.selectById(requireUserId());
        user.setRealName(request.getName());
        user.setGender(request.getGender());
        user.setBirthday(request.getBirthday());
        user.setEmail(request.getEmail());
        user.setAvatar(request.getAvatar());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public List<Map<String, Object>> listFamilyMembers() {
        Long userId = requireUserId();
        return familyMemberMapper.selectList(new LambdaQueryWrapper<FamilyMember>().eq(FamilyMember::getUserId, userId)).stream()
                .map(this::familyMemberMap)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> createFamilyMember(FamilyMemberRequest request) {
        FamilyMember familyMember = new FamilyMember();
        fillFamilyMember(familyMember, request);
        familyMember.setUserId(requireUserId());
        familyMember.setCreateTime(LocalDateTime.now());
        familyMember.setUpdateTime(LocalDateTime.now());
        familyMemberMapper.insert(familyMember);
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("id", familyMember.getId());
        return result;
    }

    @Override
    public void updateFamilyMember(Long id, FamilyMemberRequest request) {
        FamilyMember familyMember = familyMemberMapper.selectById(id);
        check(familyMember != null && requireUserId().equals(familyMember.getUserId()), "就诊人不存在");
        fillFamilyMember(familyMember, request);
        familyMember.setUpdateTime(LocalDateTime.now());
        familyMemberMapper.updateById(familyMember);
    }

    @Override
    public void deleteFamilyMember(Long id) {
        FamilyMember familyMember = familyMemberMapper.selectById(id);
        check(familyMember != null && requireUserId().equals(familyMember.getUserId()), "就诊人不存在");
        familyMemberMapper.deleteById(id);
    }

    @Override
    public PageResult<Map<String, Object>> listMyReviews(Integer page, Integer pageSize) {
        Long userId = requireUserId();
        return paginate(reviewMapper.selectList(new LambdaQueryWrapper<Review>().eq(Review::getUserId, userId).orderByDesc(Review::getCreateTime)).stream()
                .map(item -> {
                    Map<String, Object> result = new LinkedHashMap<String, Object>();
                    result.put("id", item.getId());
                    result.put("orderType", item.getOrderType());
                    result.put("orderId", item.getOrderId());
                    result.put("doctorId", item.getDoctorId());
                    result.put("doctorName", doctorMapper.selectById(item.getDoctorId()).getName());
                    result.put("content", item.getContent());
                    result.put("rating", item.getRating());
                    result.put("createTime", item.getCreateTime());
                    return result;
                }).collect(Collectors.toList()), page, pageSize);
    }

    @Override
    public Map<String, Object> createReview(CreateReviewRequest request) {
        Review review = new Review();
        review.setOrderType(request.getOrderType());
        review.setOrderId(request.getOrderId());
        review.setDoctorId(request.getDoctorId());
        review.setContent(request.getContent());
        review.setRating(request.getRating());
        review.setUserId(requireUserId());
        review.setCreateTime(LocalDateTime.now());
        reviewMapper.insert(review);
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("id", review.getId());
        return result;
    }

    @Override
    public PageResult<Map<String, Object>> listMessages(Integer page, Integer pageSize) {
        Long userId = requireUserId();
        return paginate(messageMapper.selectList(new LambdaQueryWrapper<Message>().eq(Message::getUserId, userId).orderByDesc(Message::getCreateTime)).stream()
                .map(this::messageMap).collect(Collectors.toList()), page, pageSize);
    }

    @Override
    public void markMessageRead(Long id) {
        Message message = messageMapper.selectById(id);
        check(message != null && requireUserId().equals(message.getUserId()), "消息不存在");
        message.setIsRead(1);
        messageMapper.updateById(message);
    }

    @Override
    public Map<String, Object> unreadCount() {
        Long userId = requireUserId();
        long count = messageMapper.selectCount(new LambdaQueryWrapper<Message>().eq(Message::getUserId, userId).eq(Message::getIsRead, 0));
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("count", count);
        return result;
    }

    @Override
    public PageResult<Map<String, Object>> listFeedbacks(Integer page, Integer pageSize) {
        Long userId = requireUserId();
        return paginate(feedbackMapper.selectList(new LambdaQueryWrapper<Feedback>().eq(Feedback::getUserId, userId).orderByDesc(Feedback::getCreateTime)).stream()
                .map(item -> {
                    Map<String, Object> result = new LinkedHashMap<String, Object>();
                    result.put("id", item.getId());
                    result.put("type", item.getFeedbackType());
                    result.put("content", item.getContent());
                    result.put("images", item.getImages() == null ? Arrays.asList() : Arrays.asList(item.getImages().split(",")));
                    result.put("status", item.getStatus());
                    result.put("replyContent", item.getReplyContent());
                    result.put("replyTime", item.getReplyTime());
                    result.put("createTime", item.getCreateTime());
                    return result;
                }).collect(Collectors.toList()), page, pageSize);
    }

    @Override
    public Map<String, Object> createFeedback(CreateFeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setUserId(requireUserId());
        feedback.setFeedbackType(request.getType());
        feedback.setContent(request.getContent());
        feedback.setImages(request.getImages() == null ? null : String.join(",", request.getImages()));
        feedback.setStatus(1);
        feedback.setCreateTime(LocalDateTime.now());
        feedback.setUpdateTime(LocalDateTime.now());
        feedbackMapper.insert(feedback);
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("id", feedback.getId());
        return result;
    }

    @Override
    public PageResult<Map<String, Object>> listMyFollows(Integer type, Integer page, Integer pageSize) {
        Long userId = requireUserId();
        return paginate(followMapper.selectList(new LambdaQueryWrapper<Follow>()
                        .eq(Follow::getUserId, userId)
                        .eq(type != null, Follow::getFollowType, type)
                        .orderByDesc(Follow::getCreateTime))
                .stream().map(this::followMap).collect(Collectors.toList()), page, pageSize);
    }

    @Override
    public Map<String, Object> createFollow(Integer type, Long targetId) {
        Long userId = requireUserId();
        Follow follow = followMapper.selectOne(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getUserId, userId)
                .eq(Follow::getFollowType, type)
                .eq(Follow::getFollowId, targetId));
        if (follow == null) {
            follow = new Follow();
            follow.setUserId(userId);
            follow.setFollowType(type);
            follow.setFollowId(targetId);
            follow.setCreateTime(LocalDateTime.now());
            followMapper.insert(follow);
        }
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("id", follow.getId());
        return result;
    }

    @Override
    public void deleteFollow(Integer type, Long targetId) {
        Long userId = requireUserId();
        Follow follow = followMapper.selectOne(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getUserId, userId)
                .eq(Follow::getFollowType, type)
                .eq(Follow::getFollowId, targetId));
        check(follow != null, "关注记录不存在");
        followMapper.deleteById(follow.getId());
    }

    private void fillFamilyMember(FamilyMember familyMember, FamilyMemberRequest request) {
        familyMember.setName(request.getName());
        familyMember.setPhone(request.getPhone());
        familyMember.setRelation(request.getRelation());
        familyMember.setGender(request.getGender());
        familyMember.setBirthday(request.getBirthday());
        familyMember.setIdCard(request.getIdCard());
        familyMember.setIsDefault(request.getIsDefault());
    }

    private Map<String, Object> familyMemberMap(FamilyMember item) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("id", item.getId());
        result.put("name", item.getName());
        result.put("phone", item.getPhone());
        result.put("relation", item.getRelation());
        result.put("gender", item.getGender());
        result.put("birthday", item.getBirthday());
        result.put("idCard", item.getIdCard());
        result.put("isDefault", item.getIsDefault());
        return result;
    }

    private Map<String, Object> messageMap(Message item) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("id", item.getId());
        result.put("title", item.getTitle());
        result.put("content", item.getContent());
        result.put("isRead", item.getIsRead());
        result.put("createTime", item.getCreateTime());
        return result;
    }

    private Map<String, Object> followMap(Follow item) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("id", item.getId());
        result.put("type", item.getFollowType());
        result.put("followId", item.getFollowId());
        result.put("createTime", item.getCreateTime());
        if (Integer.valueOf(1).equals(item.getFollowType())) {
            result.put("name", hospitalMapper.selectById(item.getFollowId()).getName());
        } else if (Integer.valueOf(2).equals(item.getFollowType())) {
            result.put("name", doctorMapper.selectById(item.getFollowId()).getName());
        } else if (Integer.valueOf(3).equals(item.getFollowType())) {
            result.put("name", diseaseMapper.selectById(item.getFollowId()).getName());
        } else {
            throw new ApiException(StatusCode.BAD_REQUEST, "非法关注类型");
        }
        return result;
    }
}