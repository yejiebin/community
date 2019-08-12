package com.yjb.controller;

import com.yjb.dto.CommentCreateDTO;
import com.yjb.dto.CommentDTO;
import com.yjb.dto.ResultDTO;
import com.yjb.enums.CommentTypeEnum;
import com.yjb.exception.CustomizeErrorCode;
import com.yjb.exception.CustomizeException;
import com.yjb.model.Comment;
import com.yjb.model.User;
import com.yjb.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CommentController {

    @Autowired
    CommentService commentService;

    @ResponseBody
    @PostMapping("/comment")
    public Object addComment(@RequestBody CommentCreateDTO commentCreateDTO,
                             @SessionAttribute(name = "user", required = false) User user) {
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        if (commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())) {
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }

        Comment comment = new Comment();
        BeanUtils.copyProperties(commentCreateDTO, comment);
        comment.setCommentator(user.getId());
        comment.setCommentCount(0);
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());
        comment.setLikeCount(0L);
        commentService.insert(comment, user);
        return ResultDTO.okOf();
    }

    @ResponseBody
    @GetMapping("/comment/{id}")
    public Object comments(@PathVariable("id") Long id) {
        if (!StringUtils.isNumeric(String.valueOf(id))) {
            throw new CustomizeException(CustomizeErrorCode.INVALID_INPUT);
        }
        List<CommentDTO> commentDTOS = commentService.findByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }

}
