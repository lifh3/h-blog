package com.blog.service.impl;

import com.blog.common.exception.BusinessException;
import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public List<Category> listAll() {
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort)
        );
        
        // 填充每个分类的文章数量
        for (Category category : categories) {
            Long count = articleMapper.selectCount(
                    new LambdaQueryWrapper<Article>()
                            .eq(Article::getCategoryId, category.getId())
                            .eq(Article::getStatus, 1)
            );
            category.setArticleCount(count.intValue());
        }
        
        return categories;
    }

    @Override
    public Category getById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return category;
    }

    @Override
    public void create(Category category) {
        categoryMapper.insert(category);
    }

    @Override
    public void update(Category category) {
        if (categoryMapper.selectById(category.getId()) == null) {
            throw new BusinessException("分类不存在");
        }
        categoryMapper.updateById(category);
    }

    @Override
    public void delete(Long id) {
        if (categoryMapper.selectById(id) == null) {
            throw new BusinessException("分类不存在");
        }
        categoryMapper.deleteById(id);
    }
}
