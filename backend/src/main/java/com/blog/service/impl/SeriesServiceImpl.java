package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.exception.BusinessException;
import com.blog.entity.Article;
import com.blog.entity.Series;
import com.blog.mapper.ArticleCollectionMapper;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.SeriesMapper;
import com.blog.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SeriesServiceImpl implements SeriesService {

    @Autowired
    private SeriesMapper seriesMapper;

    @Autowired
    private ArticleCollectionMapper articleCollectionMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public List<Series> listCollections() {
        List<Series> collections = seriesMapper.selectWithArticleCount();
        for (Series collection : collections) {
            // Count from article_collection mapping table (only published articles)
            List<Long> mappedIds = articleCollectionMapper.selectArticleIdsByCollectionId(collection.getId());
            long mappedPublishedCount = 0;
            if (mappedIds != null && !mappedIds.isEmpty()) {
                mappedPublishedCount = articleMapper.selectCount(
                        new LambdaQueryWrapper<Article>()
                                .in(Article::getId, mappedIds)
                                .eq(Article::getStatus, 1)
                );
            }
            // Count from article.collection_id directly
            Long directCount = articleMapper.selectCount(
                    new LambdaQueryWrapper<Article>()
                            .eq(Article::getCollectionId, collection.getId())
                            .eq(Article::getStatus, 1)
            );
            collection.setArticleCount((int) (mappedPublishedCount + directCount));
        }
        return collections;
    }

    @Override
    public Map<String, Object> getCollectionWithArticles(Long id) {
        Series series = seriesMapper.selectById(id);
        if (series == null) {
            throw new BusinessException("Series not found");
        }

        // Fetch articles by collection_id directly in article table
        List<Article> articles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getCollectionId, id)
                        .eq(Article::getStatus, 1)
                        .orderByDesc(Article::getCreateTime)
        );

        // Also fetch from article_collection mapping table if needed
        List<Long> mappedArticleIds = articleCollectionMapper.selectArticleIdsByCollectionId(id);
        if (mappedArticleIds != null && !mappedArticleIds.isEmpty()) {
            List<Article> mappedArticles = articleMapper.selectList(
                    new LambdaQueryWrapper<Article>()
                            .in(Article::getId, mappedArticleIds)
                            .eq(Article::getStatus, 1)
            );
            // Merge articles from both sources, avoiding duplicates
            for (Article mapped : mappedArticles) {
                boolean found = false;
                for (Article existing : articles) {
                    if (existing.getId().equals(mapped.getId())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    articles.add(mapped);
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("series", series);
        result.put("articles", articles);
        return result;
    }

    @Override
    @Transactional
    public void create(Series series) {
        seriesMapper.insert(series);
    }

    @Override
    @Transactional
    public void update(Series series) {
        Series existing = seriesMapper.selectById(series.getId());
        if (existing == null) {
            throw new BusinessException("Series not found");
        }
        seriesMapper.updateById(series);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Series existing = seriesMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("Series not found");
        }
        articleCollectionMapper.deleteByCollectionId(id);
        seriesMapper.deleteById(id);
    }
}
