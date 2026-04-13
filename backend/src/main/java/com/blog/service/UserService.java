package com.blog.service;

import com.blog.dto.UserUpdateRequest;
import com.blog.entity.User;

public interface UserService {
    User getById(Long id);
    void update(Long id, UserUpdateRequest request, Long currentUserId);
    void updateProfile(Long userId, UserUpdateRequest request);
    void changePassword(Long userId, String currentPassword, String newPassword);
}
