package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("collection")
public class Series {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String description;
    private String coverImage;
    private Integer sort;
    private Integer articleCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
