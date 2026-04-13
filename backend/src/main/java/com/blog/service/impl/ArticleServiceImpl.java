package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.exception.BusinessException;
import com.blog.dto.ArticleRequest;
import com.blog.entity.Article;
import com.blog.entity.ArticleTag;
import com.blog.entity.Category;
import com.blog.entity.Series;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.ArticleTagMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.mapper.SeriesMapper;
import com.blog.service.ArticleService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SeriesMapper seriesMapper;

    @Override
    public Page<Article> listPage(Long categoryId, Long tagId, Integer page, Integer pageSize, String sort) {
        Page<Article> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, 1);

        if (categoryId != null) {
            wrapper.eq(Article::getCategoryId, categoryId);
        }

        if (tagId != null) {
            // Filter by tag via article_tag join
            List<Long> articleIds = articleTagMapper.selectTagIdsByArticleId(tagId);
            if (articleIds.isEmpty()) {
                return new Page<>(page, pageSize);
            }
            wrapper.in(Article::getId, articleIds);
        }

        // Apply sorting
        if ("earliest".equalsIgnoreCase(sort)) {
            wrapper.orderByAsc(Article::getCreateTime);
        } else {
            wrapper.orderByDesc(Article::getCreateTime);
        }
        
        Page<Article> result = articleMapper.selectPage(p, wrapper);

        // 填充分类、合集名称和标签
        for (Article article : result.getRecords()) {
            if (article.getCategoryId() != null) {
                Category category = categoryMapper.selectById(article.getCategoryId());
                if (category != null) {
                    article.setCategoryName(category.getName());
                }
            }
            if (article.getCollectionId() != null) {
                Series collection = seriesMapper.selectById(article.getCollectionId());
                if (collection != null) {
                    article.setCollectionName(collection.getName());
                }
            }
            // 填充标签
            List<String> tagNames = articleTagMapper.selectTagNamesByArticleId(article.getId());
            if (tagNames != null && !tagNames.isEmpty()) {
                article.setTags(String.join(",", tagNames));
            }
        }

        return result;
    }

    @Override
    public Page<Article> listHot(Integer page, Integer pageSize) {
        Page<Article> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, 1);
        wrapper.orderByDesc(Article::getViewCount, Article::getCreateTime);
        return articleMapper.selectPage(p, wrapper);
    }

    @Override
    public Article getById(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException("Article not found");
        }

        // 填充分类和合集名称
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null) {
                article.setCategoryName(category.getName());
            }
        }
        if (article.getCollectionId() != null) {
            Series collection = seriesMapper.selectById(article.getCollectionId());
            if (collection != null) {
                article.setCollectionName(collection.getName());
            }
        }

        // 填充标签
        List<String> tagNames = articleTagMapper.selectTagNamesByArticleId(id);
        if (tagNames != null && !tagNames.isEmpty()) {
            article.setTags(String.join(",", tagNames));
        }

        return article;
    }

    @Override
    public void incrementViewCount(Long id) {
        articleMapper.update(null,
                new LambdaUpdateWrapper<Article>()
                        .eq(Article::getId, id)
                        .setSql("view_count = view_count + 1")
        );
    }

    @Override
    @Transactional
    public void create(ArticleRequest request, Long userId) {
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCoverImage(request.getCoverImage());
        article.setCategoryId(request.getCategoryId());
        article.setCollectionId(request.getCollectionId());
        article.setUserId(userId);
        article.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        article.setViewCount(0);
        articleMapper.insert(article);

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            articleTagMapper.deleteByArticleId(article.getId());
            articleTagMapper.insertBatch(article.getId(), request.getTagIds());
        }
    }

    @Override
    @Transactional
    public void update(ArticleRequest request, Long userId) {
        Article article = articleMapper.selectById(request.getId());
        if (article == null) {
            throw new BusinessException("Article not found");
        }
        if (!article.getUserId().equals(userId)) {
            throw new BusinessException("No permission to update this article");
        }

        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCoverImage(request.getCoverImage());
        article.setCategoryId(request.getCategoryId());
        if (request.getStatus() != null) {
            article.setStatus(request.getStatus());
        }
        articleMapper.updateById(article);

        if (request.getTagIds() != null) {
            articleTagMapper.deleteByArticleId(article.getId());
            if (!request.getTagIds().isEmpty()) {
                articleTagMapper.insertBatch(article.getId(), request.getTagIds());
            }
        }
    }

    @Override
    public void delete(Long id, Long userId) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException("Article not found");
        }
        if (!article.getUserId().equals(userId)) {
            throw new BusinessException("No permission to delete this article");
        }
        articleMapper.deleteById(id);
    }

    @Override
    public void deleteAdmin(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException("Article not found");
        }
        articleMapper.deleteById(id);
    }

    @Override
    public void updateAdmin(ArticleRequest request) {
        Article article = articleMapper.selectById(request.getId());
        if (article == null) {
            throw new BusinessException("Article not found");
        }
        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCoverImage(request.getCoverImage());
        article.setCategoryId(request.getCategoryId());
        article.setCollectionId(request.getCollectionId());
        if (request.getStatus() != null) {
            article.setStatus(request.getStatus());
        }
        articleMapper.updateById(article);

        if (request.getTagIds() != null) {
            articleTagMapper.deleteByArticleId(article.getId());
            if (!request.getTagIds().isEmpty()) {
                articleTagMapper.insertBatch(article.getId(), request.getTagIds());
            }
        }
    }

    @Override
    public Long createAiArticle(String title, String summary, String content, Long userId, Integer status) {
        return createAiArticle(title, summary, content, userId, status, null, null);
    }

    @Override
    public Long createAiArticle(String title, String summary, String content, Long userId, Integer status, Long categoryId, List<Long> tagIds) {
        Article article = new Article();
        article.setTitle(title);
        article.setSummary(summary);
        article.setContent(content);
        article.setUserId(userId);
        article.setStatus(status != null ? status : 0);
        article.setViewCount(0);
        if (categoryId != null) {
            article.setCategoryId(categoryId);
        }
        articleMapper.insert(article);

        // 保存标签
        if (tagIds != null && !tagIds.isEmpty()) {
            articleTagMapper.deleteByArticleId(article.getId());
            articleTagMapper.insertBatch(article.getId(), tagIds);
        }

        return article.getId();
    }

    @Override
    public Page<Article> listPageAdmin(Long categoryId, Long tagId, Integer status, Integer page, Integer pageSize) {
        Page<Article> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Article::getStatus, status);
        }
        if (categoryId != null) {
            wrapper.eq(Article::getCategoryId, categoryId);
        }
        if (tagId != null) {
            List<Long> articleIds = articleTagMapper.selectTagIdsByArticleId(tagId);
            if (articleIds.isEmpty()) {
                return new Page<>(page, pageSize);
            }
            wrapper.in(Article::getId, articleIds);
        }
        wrapper.orderByDesc(Article::getCreateTime);
        Page<Article> result = articleMapper.selectPage(p, wrapper);

        // 填充分类和合集名称
        for (Article article : result.getRecords()) {
            if (article.getCategoryId() != null) {
                Category category = categoryMapper.selectById(article.getCategoryId());
                if (category != null) {
                    article.setCategoryName(category.getName());
                }
            }
            if (article.getCollectionId() != null) {
                Series collection = seriesMapper.selectById(article.getCollectionId());
                if (collection != null) {
                    article.setCollectionName(collection.getName());
                }
            }
            // 填充标签
            List<String> tagNames = articleTagMapper.selectTagNamesByArticleId(article.getId());
            if (tagNames != null && !tagNames.isEmpty()) {
                article.setTags(String.join(",", tagNames));
            }
        }

        return result;
    }

    @Override
    public Article getByIdAdmin(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException("Article not found");
        }
        // 填充分类和合集名称
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null) {
                article.setCategoryName(category.getName());
            }
        }
        if (article.getCollectionId() != null) {
            Series collection = seriesMapper.selectById(article.getCollectionId());
            if (collection != null) {
                article.setCollectionName(collection.getName());
            }
        }
        // 填充标签
        List<String> tagNames = articleTagMapper.selectTagNamesByArticleId(id);
        if (tagNames != null && !tagNames.isEmpty()) {
            article.setTags(String.join(",", tagNames));
        }
        return article;
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException("Article not found");
        }
        article.setStatus(status);
        articleMapper.updateById(article);
    }

    @Override
    public Page<Article> listRelatedArticles(Long articleId, Integer page, Integer pageSize) {
        Article original = articleMapper.selectById(articleId);
        if (original == null || original.getCategoryId() == null) {
            return new Page<>(page, pageSize);
        }

        Page<Article> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, 1)
               .eq(Article::getCategoryId, original.getCategoryId())
               .ne(Article::getId, articleId)
               .orderByDesc(Article::getCreateTime);
        Page<Article> result = articleMapper.selectPage(p, wrapper);

        // 填充分类和合集名称、标签
        for (Article article : result.getRecords()) {
            if (article.getCategoryId() != null) {
                Category category = categoryMapper.selectById(article.getCategoryId());
                if (category != null) {
                    article.setCategoryName(category.getName());
                }
            }
            if (article.getCollectionId() != null) {
                Series collection = seriesMapper.selectById(article.getCollectionId());
                if (collection != null) {
                    article.setCollectionName(collection.getName());
                }
            }
            List<String> tagNames = articleTagMapper.selectTagNamesByArticleId(article.getId());
            if (tagNames != null && !tagNames.isEmpty()) {
                article.setTags(String.join(",", tagNames));
            }
        }

        return result;
    }

    @Override
    public List<Article> listByCategoryId(Long categoryId) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getCategoryId, categoryId)
               .eq(Article::getStatus, 1)
               .orderByDesc(Article::getCreateTime);
        List<Article> articles = articleMapper.selectList(wrapper);

        for (Article article : articles) {
            if (article.getCollectionId() != null) {
                Series collection = seriesMapper.selectById(article.getCollectionId());
                if (collection != null) {
                    article.setCollectionName(collection.getName());
                }
            }
            List<String> tagNames = articleTagMapper.selectTagNamesByArticleId(article.getId());
            if (tagNames != null && !tagNames.isEmpty()) {
                article.setTags(String.join(",", tagNames));
            }
        }
        return articles;
    }
}
