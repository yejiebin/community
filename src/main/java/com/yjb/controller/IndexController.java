package com.yjb.controller;

import com.yjb.cache.HotTagCache;
import com.yjb.dto.PaginationDTO;
import com.yjb.model.User;
import com.yjb.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    QuestionService questionService;

    @Autowired
    HotTagCache hotTagCache;

    @GetMapping("/")
    public String toIndex(@RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "3") Integer size,
                          @RequestParam(name = "search", required = false) String search,
                          @RequestParam(name = "tag", required = false) String tag,
                          @SessionAttribute(name = "user", required = false) User user,
                          Model model) {

        PaginationDTO questionList = questionService.list(search, tag, page, size);
        model.addAttribute("pagination", questionList);
        model.addAttribute("search", search);
        model.addAttribute("tag", tag);
        model.addAttribute("hotTags", hotTagCache.getHots());
        return "index";
    }

}
