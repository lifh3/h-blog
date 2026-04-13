package com.blog.controller;

import com.blog.common.AuthContext;
import com.blog.common.Result;
import com.blog.entity.Article;
import com.blog.entity.Tag;
import com.blog.service.ArticleService;
import com.blog.service.TagService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @Autowired
    private ArticleService articleService;

    @GetMapping
    public Result<List<Tag>> list() {
        return Result.success(tagService.listAll());
    }

    @GetMapping("/{id}")
    public Result<Tag> getById(@PathVariable Long id) {
        return Result.success(tagService.getById(id));
    }

    @GetMapping("/{name}/articles")
    public Result<Page<Article>> getArticlesByTag(
            @PathVariable String name,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false, defaultValue = "latest") String sort) {
        Tag tag = tagService.getByName(name);
        if (tag == null) {
            return Result.success(new Page<>());
        }
        return Result.success(articleService.listPage(null, tag.getId(), page, pageSize, sort));
    }

    @PostMapping
    public Result<Void> create(@RequestBody Tag tag) {
        AuthContext.getUserId();
        tagService.create(tag);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Tag tag) {
        AuthContext.getUserId();
        tag.setId(id);
        tagService.update(tag);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        AuthContext.getUserId();
        tagService.delete(id);
        return Result.success();
    }
}
