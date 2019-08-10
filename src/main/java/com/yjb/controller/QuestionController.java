package com.yjb.controller;

import com.yjb.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @GetMapping("/question/{id}")
    public String toQuestion(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("question", questionService.findById(id));
        return "question";
    }
}
