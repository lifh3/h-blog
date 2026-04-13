package com.blog.controller;

import com.blog.common.AuthContext;
import com.blog.common.Result;
import com.blog.dto.ArticleRequest;
import com.blog.dto.CommentRequest;
import com.blog.dto.PageRequest;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.service.ArticleService;
import com.blog.service.CommentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CommentService commentService;

    @GetMapping
    public Result<Page<Article>> list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false, defaultValue = "latest") String sort) {
        return Result.success(articleService.listPage(categoryId, tagId, page, pageSize, sort));
    }

    @GetMapping("/hot")
    public Result<Page<Article>> listHot(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(articleService.listHot(page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<Article> getById(@PathVariable Long id) {
        articleService.incrementViewCount(id);
        return Result.success(articleService.getById(id));
    }

    @GetMapping("/{id}/related")
    public Result<Page<Article>> getRelatedArticles(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize) {
        return Result.success(articleService.listRelatedArticles(id, page, pageSize));
    }

    @GetMapping("/{id}/comments")
    public Result<List<Comment>> getComments(@PathVariable Long id) {
        return Result.success(commentService.listByArticleId(id));
    }

    @PostMapping("/{id}/comments")
    public Result<Void> addComment(@PathVariable Long id, @Valid @RequestBody CommentRequest request) {
        Long userId = AuthContext.getUserId();
        request.setArticleId(id);
        commentService.create(request, userId);
        return Result.success();
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody ArticleRequest request) {
        Long userId = AuthContext.getUserId();
        articleService.create(request, userId);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ArticleRequest request) {
        request.setId(id);
        Long userId = AuthContext.getUserId();
        articleService.update(request, userId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = AuthContext.getUserId();
        articleService.delete(id, userId);
        return Result.success();
    }
}
