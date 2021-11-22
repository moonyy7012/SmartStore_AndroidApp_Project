package com.ssafy.smartstore.model.service;

import com.ssafy.smartstore.model.dao.CommentDao;
import com.ssafy.smartstore.model.dto.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentDao commentDao;

    @Override
    public void addComment(Comment comment) {
        commentDao.insert(comment);
    }

    @Override
    public Comment selectComment(Integer id) {
        return commentDao.select(id);
    }

    @Override
    public void removeComment(Integer id) {
        commentDao.delete(id);
    }

    @Override
    public void updateComment(Comment comment) {
        commentDao.update(comment);
    }

    @Override
    public List<Comment> selectByProduct(Integer productId) {
        return commentDao.selectByProduct(productId);
    }
}
