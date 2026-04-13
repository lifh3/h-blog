package com.blog.service;

import com.blog.entity.Series;
import java.util.List;
import java.util.Map;

public interface SeriesService {
    List<Series> listCollections();
    Map<String, Object> getCollectionWithArticles(Long id);
    void create(Series series);
    void update(Series series);
    void delete(Long id);
}
