package com.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("article_collection")
public class ArticleCollection {
    private Long articleId;
    private Long collectionId;
    private Integer sort;
}
