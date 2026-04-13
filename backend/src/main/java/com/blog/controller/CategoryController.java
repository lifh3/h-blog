package com.blog.controller;

import com.blog.common.AuthContext;
import com.blog.common.Result;
import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.service.ArticleService;
import com.blog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleService articleService;

    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryService.listAll());
    }

    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    @GetMapping("/{id}/articles")
    public Result<List<Article>> getCategoryArticles(@PathVariable Long id) {
        return Result.success(articleService.listByCategoryId(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody Category category) {
        AuthContext.getUserId(); // 确保已登录
        categoryService.create(category);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Category category) {
        AuthContext.getUserId();
        category.setId(id);
        categoryService.update(category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        AuthContext.getUserId();
        categoryService.delete(id);
        return Result.success();
    }
}
