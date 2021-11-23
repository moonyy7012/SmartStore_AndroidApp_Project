package com.ssafy.smartstore.controller.rest;

import com.ssafy.smartstore.model.dto.Comment;
import com.ssafy.smartstore.model.service.CommentService;
import io.swagger.annotations.ApiOperation;
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
    @ApiOperation(value="comment를 등록합니다.")
    @Transactional
    public Boolean insert(@RequestBody Comment comment) {
        commentService.addComment(comment);
        return true;
    }

    @DeleteMapping("/{id}")
    @Transactional
    @ApiOperation(value="사용자가 작성한 comment를 삭제합니다.", response = Boolean.class)
    public Boolean delete(@PathVariable Integer id) {
        commentService.removeComment(id);
        return true;
    }

    @PutMapping
    @Transactional
    @ApiOperation(value="사용자가 작성한 comment를 수정합니다.", response = Boolean.class)
    public Boolean update(@RequestBody Comment comment  ) {
        commentService.updateComment(comment);
        return true;
    }


}
