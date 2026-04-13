package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Series;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SeriesMapper extends BaseMapper<Series> {

    @Select("SELECT c.*, 0 AS articleCount FROM collection c ORDER BY c.sort ASC")
    List<Series> selectWithArticleCount();
}
