package com.blog.controller;

import com.blog.common.AuthContext;
import com.blog.common.Result;
import com.blog.dto.SeriesRequest;
import com.blog.entity.Series;
import com.blog.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/collections")
public class SeriesController {

    @Autowired
    private SeriesService seriesService;

    @GetMapping
    public Result<List<Series>> list() {
        return Result.success(seriesService.listCollections());
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(seriesService.getCollectionWithArticles(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody SeriesRequest request) {
        AuthContext.getUserId();
        Series series = new Series();
        series.setName(request.getName());
        series.setDescription(request.getDescription());
        series.setCoverImage(request.getCoverImage());
        series.setSort(request.getSort() != null ? request.getSort() : 0);
        series.setArticleCount(0);
        seriesService.create(series);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SeriesRequest request) {
        AuthContext.getUserId();
        Series series = new Series();
        series.setId(id);
        series.setName(request.getName());
        series.setDescription(request.getDescription());
        series.setCoverImage(request.getCoverImage());
        series.setSort(request.getSort() != null ? request.getSort() : 0);
        seriesService.update(series);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        AuthContext.getUserId();
        seriesService.delete(id);
        return Result.success();
    }
}
