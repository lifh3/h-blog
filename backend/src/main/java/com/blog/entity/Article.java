package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private Long categoryId;
    private Long collectionId;
    private Long userId;
    private Integer viewCount;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 关联查询的字段，不存储到数据库
    @TableField(exist = false)
    private String categoryName;

    @TableField(exist = false)
    private String collectionName;

    @TableField(exist = false)
    private String tags;  // 逗号分隔的标签名称
}
