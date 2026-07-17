package com.hospital.controller;

import com.hospital.common.Result;
import com.hospital.entity.FamilyMember;
import com.hospital.service.FamilyMemberService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/family-members")
public class FamilyMemberController {

    @Autowired
    private FamilyMemberService familyMemberService;

    @GetMapping
    public Result<List<FamilyMember>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(familyMemberService.listByUserId(userId));
    }

    @PostMapping
    public Result<Void> add(@RequestBody FamilyMember member,
                            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        member.setUserId(userId);
        familyMemberService.add(member);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @RequestBody FamilyMember member,
                               HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        member.setId(id);
        familyMemberService.update(member, userId);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        familyMemberService.delete(id, userId);
        return Result.ok();
    }
}
