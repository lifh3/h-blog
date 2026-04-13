package com.blog.service.impl;

import com.blog.common.exception.BusinessException;
import com.blog.dto.CommentRequest;
import com.blog.entity.Comment;
import com.blog.mapper.CommentMapper;
import com.blog.service.CommentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<Comment> listByArticleId(Long articleId) {
        return commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getArticleId, articleId)
                        .eq(Comment::getStatus, 1)
                        .orderByAsc(Comment::getCreateTime)
        );
    }

    @Override
    public List<Comment> listAll() {
        return commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .orderByDesc(Comment::getCreateTime)
        );
    }

    @Override
    public List<Comment> search(Long articleId, LocalDateTime startDate, LocalDateTime endDate) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (articleId != null) {
            wrapper.eq(Comment::getArticleId, articleId);
        }
        if (startDate != null) {
            wrapper.ge(Comment::getCreateTime, startDate);
        }
        if (endDate != null) {
            wrapper.le(Comment::getCreateTime, endDate);
        }
        wrapper.orderByDesc(Comment::getCreateTime);
        return commentMapper.selectList(wrapper);
    }

    @Override
    public void create(CommentRequest request, Long userId) {
        Comment comment = new Comment();
        comment.setArticleId(request.getArticleId());
        comment.setUserId(userId);
        comment.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        comment.setContent(request.getContent());
        comment.setNickname(request.getNickname());
        comment.setReplyTo(request.getReplyTo());
        comment.setStatus(1);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.insert(comment);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        comment.setStatus(status);
        commentMapper.updateById(comment);
    }

    @Override
    public void delete(Long id) {
        if (commentMapper.selectById(id) == null) {
            throw new BusinessException("评论不存在");
        }
        commentMapper.deleteById(id);
    }
}
