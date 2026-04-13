package com.blog.service;

import com.blog.dto.CommentRequest;
import com.blog.entity.Comment;
import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    List<Comment> listByArticleId(Long articleId);
    List<Comment> listAll();
    List<Comment> search(Long articleId, LocalDateTime startDate, LocalDateTime endDate);
    void create(CommentRequest request, Long userId);
    void updateStatus(Long id, Integer status);
    void delete(Long id);
}
