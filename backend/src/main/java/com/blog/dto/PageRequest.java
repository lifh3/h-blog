package com.blog.dto;

import lombok.Data;

@Data
public class PageRequest {
    private Integer page = 1;
    private Integer pageSize = 10;
    private Long categoryId;
    private Long tagId;
}
