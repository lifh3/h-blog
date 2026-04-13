package com.blog.service;

import com.blog.entity.Tag;
import java.util.List;

public interface TagService {
    List<Tag> listAll();
    Tag getById(Long id);
    Tag getByName(String name);
    void create(Tag tag);
    void update(Tag tag);
    void delete(Long id);
}
