package com.blog.controller;

import com.blog.common.AuthContext;
import com.blog.common.Result;
import com.blog.dto.UserUpdateRequest;
import com.blog.entity.User;
import com.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public Result<User> profile() {
        Long userId = AuthContext.getUserId();
        return Result.success(userService.getById(userId));
    }

    @PatchMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UserUpdateRequest request) {
        Long userId = AuthContext.getUserId();
        userService.updateProfile(userId, request);
        return Result.success();
    }

    @PostMapping("/change-password")
    public Result<Void> changePassword(@RequestBody Map<String, String> passwordData) {
        Long userId = AuthContext.getUserId();
        userService.changePassword(userId, passwordData.get("currentPassword"), passwordData.get("newPassword"));
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        Long currentUserId = AuthContext.getUserId();
        userService.update(id, request, currentUserId);
        return Result.success();
    }
}
