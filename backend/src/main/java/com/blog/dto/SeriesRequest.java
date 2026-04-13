package com.blog.dto;

import lombok.Data;
import java.util.List;

@Data
public class SeriesRequest {
    private Long id;
    private String name;
    private String description;
    private String coverImage;
    private Integer sort;
    private List<Long> articleIds;
}
