package com.ssafy.smartstore.controller.rest;

import com.ssafy.smartstore.model.dto.Comment;
import com.ssafy.smartstore.model.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/comment")
@CrossOrigin("*")
public class CommentRestController {

    @Autowired
    CommentService commentService;

    @PostMapping
    @Transactional
    public Boolean insert(@RequestBody Comment comment) {
        commentService.addComment(comment);
        return true;
    }
}
