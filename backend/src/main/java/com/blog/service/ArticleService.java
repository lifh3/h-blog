package com.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.dto.ArticleRequest;
import com.blog.entity.Article;

import java.util.List;

public interface ArticleService {
    Page<Article> listPage(Long categoryId, Long tagId, Integer page, Integer pageSize, String sort);
    Page<Article> listHot(Integer page, Integer pageSize);
    Page<Article> listPageAdmin(Long categoryId, Long tagId, Integer status, Integer page, Integer pageSize);
    Article getById(Long id);
    Article getByIdAdmin(Long id);
    void incrementViewCount(Long id);
    void create(ArticleRequest request, Long userId);
    void update(ArticleRequest request, Long userId);
    void updateStatus(Long id, Integer status);
    void delete(Long id, Long userId);
    void deleteAdmin(Long id);
    void updateAdmin(ArticleRequest request);
    Page<Article> listRelatedArticles(Long articleId, Integer page, Integer pageSize);

    List<Article> listByCategoryId(Long categoryId);

    Long createAiArticle(String title, String summary, String content, Long userId, Integer status);

    /**
     * 使用 AI 生成文章（支持自动分类和标签）
     * @param title 标题
     * @param summary 摘要
     * @param content 内容
     * @param userId 用户ID
     * @param status 状态
     * @param categoryId 分类ID（可选，AI会自动选择）
     * @param tagIds 标签ID列表（可选，AI会自动选择）
     * @return 文章ID
     */
    Long createAiArticle(String title, String summary, String content, Long userId, Integer status, Long categoryId, List<Long> tagIds);
}
