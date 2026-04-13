package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class ArticleRequest {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String summary;
    private String content;
    private String coverImage;
    private Long categoryId;
    private Long collectionId;
    private List<Long> tagIds;
    private Integer status;
}
