package com.ssafy.smartstore.model.service;

import com.ssafy.smartstore.model.dto.Comment;
import java.util.List;

public interface CommentService {
    /**
     * Comment를 등록한다.
     * @param comment
     */
    void addComment(Comment comment);

    /**
     * id에 해당하는 comment를 반환한다.
     * @param id
     * @return
     */
    Comment selectComment(Integer id);

    /**
     * id에 해당하는 Comment를 삭제한다.
     * @param id
     */
    void removeComment(Integer id);

    /**
     * Comment를 수정한다. 수정 내용은 rating과 comment이다.
     * @param comment
     */
    void updateComment(Comment comment);

    /**
     * productId에 해당하는 Comment의 목록을 Comment id의 내림차순으로 반환한다.
     * @param productId
     * @return
     */
    List<Comment> selectByProduct(Integer productId);
}
