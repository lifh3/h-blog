package com.blog.controller;

import com.blog.common.AuthContext;
import com.blog.common.Result;
import com.blog.dto.CommentRequest;
import com.blog.entity.Comment;
import com.blog.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/article/{articleId}")
    public Result<List<Comment>> listByArticle(@PathVariable Long articleId) {
        return Result.success(commentService.listByArticleId(articleId));
    }

    @GetMapping
    public Result<List<Comment>> list() {
        AuthContext.getUserId();
        return Result.success(commentService.listAll());
    }

    @GetMapping("/search")
    public Result<List<Comment>> search(
            @RequestParam(required = false) Long articleId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        AuthContext.getUserId();
        return Result.success(commentService.search(articleId, startDate, endDate));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody CommentRequest request) {
        Long userId = AuthContext.getUserId();
        commentService.create(request, userId);
        return Result.success();
    }

    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        AuthContext.getUserId();
        commentService.updateStatus(id, 1);
        return Result.success();
    }

    @PostMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id) {
        AuthContext.getUserId();
        commentService.updateStatus(id, 0);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        AuthContext.getUserId();
        commentService.delete(id);
        return Result.success();
    }
}
