package com.blog.controller;

import com.blog.common.AuthContext;
import com.blog.common.Result;
import com.blog.dto.ArticleRequest;
import com.blog.entity.Article;
import com.blog.service.ArticleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/articles")
public class AdminArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 管理端文章列表（包含草稿）
     */
    @GetMapping
    public Result<Page<Article>> list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        AuthContext.getUserId(); // 确保已登录
        return Result.success(articleService.listPageAdmin(categoryId, tagId, status, page, pageSize));
    }

    /**
     * 获取单个文章（含草稿）
     */
    @GetMapping("/{id}")
    public Result<Article> getById(@PathVariable Long id) {
        AuthContext.getUserId();
        return Result.success(articleService.getByIdAdmin(id));
    }

    /**
     * 发布草稿文章
     */
    @PostMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        AuthContext.getUserId();
        articleService.updateStatus(id, 1);
        return Result.success();
    }

    /**
     * 撤回文章（改为草稿）
     */
    @PostMapping("/{id}/unpublish")
    public Result<Void> unpublish(@PathVariable Long id) {
        AuthContext.getUserId();
        articleService.updateStatus(id, 0);
        return Result.success();
    }

    /**
     * 删除文章（管理端）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        AuthContext.getUserId(); // 确保已登录
        articleService.deleteAdmin(id);
        return Result.success();
    }

    /**
     * 更新文章（管理端，不检查作者权限）
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ArticleRequest request) {
        AuthContext.getUserId();
        request.setId(id);
        articleService.updateAdmin(request);
        return Result.success();
    }
}
