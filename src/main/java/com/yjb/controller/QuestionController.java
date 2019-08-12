package com.yjb.controller;

import com.yjb.dto.CommentDTO;
import com.yjb.dto.QuestionDTO;
import com.yjb.enums.CommentTypeEnum;
import com.yjb.exception.CustomizeErrorCode;
import com.yjb.exception.CustomizeException;
import com.yjb.service.CommentService;
import com.yjb.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @Autowired
    CommentService commentService;

    @GetMapping("/question/{id}")
    public String toQuestion(@PathVariable("id") Long id, Model model) {
        if(!StringUtils.isNumeric(String.valueOf(id))) {
            throw new CustomizeException(CustomizeErrorCode.INVALID_INPUT);
        }

        QuestionDTO questionDTO = questionService.findById(id);
        model.addAttribute("question", questionDTO);
        model.addAttribute("comments", commentService.findByTargetId(id, CommentTypeEnum.QUESTION));
        model.addAttribute("relatedQuestions", questionService.selectRelated(questionDTO));

        //增加阅读数
        questionService.incView(id);
        return "question";
    }
}
