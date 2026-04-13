package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.ArticleCollection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

@Mapper
public interface ArticleCollectionMapper extends BaseMapper<ArticleCollection> {

    @Select("SELECT article_id FROM article_collection WHERE collection_id = #{collectionId} ORDER BY sort ASC")
    List<Long> selectArticleIdsByCollectionId(@Param("collectionId") Long collectionId);

    @Insert("<script>" +
            "INSERT INTO article_collection (article_id, collection_id, sort) VALUES " +
            "<foreach collection='collectionIds' item='cid' separator=','>" +
            "(#{articleId}, #{cid}, 0)" +
            "</foreach>" +
            "</script>")
    void insertBatch(@Param("articleId") Long articleId, @Param("collectionIds") List<Long> collectionIds);

    @Delete("DELETE FROM article_collection WHERE collection_id = #{collectionId}")
    void deleteByCollectionId(@Param("collectionId") Long collectionId);
}
