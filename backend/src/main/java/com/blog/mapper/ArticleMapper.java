package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    @Select("SELECT IFNULL(SUM(view_count), 0) FROM article WHERE status = 1")
    Long selectTotalViews();
}
