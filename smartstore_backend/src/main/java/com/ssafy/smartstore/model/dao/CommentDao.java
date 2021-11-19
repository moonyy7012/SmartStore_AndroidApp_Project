package com.ssafy.smartstore.model.dao;

import com.ssafy.smartstore.model.dto.Comment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CommentDao {
    int insert(Comment comment);

    int update(Comment comment);

    int delete(Integer commentId);

    Comment select(Integer commentId);

    List<Comment> selectAll();

    List<Comment> selectByProduct(Integer productId);
}
