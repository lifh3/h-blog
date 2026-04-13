package com.blog.service.impl;

import com.blog.common.exception.BusinessException;
import com.blog.entity.ArticleTag;
import com.blog.entity.Tag;
import com.blog.mapper.ArticleTagMapper;
import com.blog.mapper.TagMapper;
import com.blog.service.TagService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Override
    public List<Tag> listAll() {
        List<Tag> tags = tagMapper.selectList(new LambdaQueryWrapper<Tag>());
        
        // 填充每个标签的文章数量
        for (Tag tag : tags) {
            Long count = articleTagMapper.selectCount(
                    new LambdaQueryWrapper<ArticleTag>()
                            .eq(ArticleTag::getTagId, tag.getId())
            );
            tag.setCount(count.intValue());
        }
        
        return tags;
    }

    @Override
    public Tag getById(Long id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }
        return tag;
    }

    @Override
    public Tag getByName(String name) {
        Tag tag = tagMapper.selectOne(
                new LambdaQueryWrapper<Tag>()
                        .eq(Tag::getName, name)
        );
        return tag;
    }

    @Override
    public void create(Tag tag) {
        tagMapper.insert(tag);
    }

    @Override
    public void update(Tag tag) {
        if (tagMapper.selectById(tag.getId()) == null) {
            throw new BusinessException("标签不存在");
        }
        tagMapper.updateById(tag);
    }

    @Override
    public void delete(Long id) {
        if (tagMapper.selectById(id) == null) {
            throw new BusinessException("标签不存在");
        }
        tagMapper.deleteById(id);
    }
}
