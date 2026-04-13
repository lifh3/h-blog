package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.ArticleTag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {

    @Select("SELECT tag_id FROM article_tag WHERE article_id = #{articleId}")
    List<Long> selectTagIdsByArticleId(Long articleId);

    @Select("SELECT t.name FROM tag t " +
            "INNER JOIN article_tag at ON t.id = at.tag_id " +
            "WHERE at.article_id = #{articleId}")
    List<String> selectTagNamesByArticleId(Long articleId);

    @Insert("<script>" +
            "INSERT INTO article_tag (article_id, tag_id) VALUES " +
            "<foreach collection='tagIds' item='tagId' separator=','>" +
            "(#{articleId}, #{tagId})" +
            "</foreach>" +
            "</script>")
    void insertBatch(Long articleId, List<Long> tagIds);

    @Delete("DELETE FROM article_tag WHERE article_id = #{articleId}")
    void deleteByArticleId(Long articleId);
}
