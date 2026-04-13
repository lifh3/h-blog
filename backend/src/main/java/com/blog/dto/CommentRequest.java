package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {
    private Long articleId;
    private Long parentId;
    @NotBlank(message = "Comment content is required")
    private String content;
    private String nickname;
    private String replyTo;
}
