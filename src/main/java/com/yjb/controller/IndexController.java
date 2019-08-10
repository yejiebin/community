package com.yjb.controller;

import com.yjb.dto.PaginationDTO;
import com.yjb.model.User;
import com.yjb.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    QuestionService questionService;

    @GetMapping("/")
    public String toIndex(@RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "10") Integer size,
                          HttpServletRequest request, Model model) {

        User user = (User) request.getSession().getAttribute("user");
        PaginationDTO questionList = questionService.list(page, size);
        model.addAttribute("pagination", questionList);

        return "index";
    }

}
