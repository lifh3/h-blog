package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.Article;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.mapper.TagMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatsController {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private TagMapper tagMapper;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        long articleCount = articleMapper.selectCount(new LambdaQueryWrapper<Article>().eq(Article::getStatus, 1));
        long categoryCount = categoryMapper.selectCount(null);
        long tagCount = tagMapper.selectCount(null);

        // 总浏览量
        Long totalViews = articleMapper.selectTotalViews();

        Map<String, Object> data = new HashMap<>();
        data.put("articles", articleCount);
        data.put("categories", categoryCount);
        data.put("tags", tagCount);
        data.put("views", totalViews != null ? totalViews : 0);
        return Result.success(data);
    }
}
